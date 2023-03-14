package com.cmcorg20230301.engine.be.generate.project.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import com.admin.common.mapper.SyncEntityFromDbMapper;
import com.admin.common.model.vo.SyncEntityFromDbVO;
import com.admin.start.model.enums.ColumnTypeRefEnum;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.File;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@SpringBootTest
public class SyncEntityFromDb {

    @Resource
    SyncEntityFromDbMapper baseMapper;

    /**
     * 同步：数据库字段和 entity实体类字段一致
     */
    @Test
    void syncEntityFromDb() {

        System.out.println("后端同步 ↓");

        long timeNumber = System.currentTimeMillis();

        Set<Class<?>> classSet = ClassUtil.scanPackageByAnnotation("com.admin", TableName.class);

        if (CollUtil.isEmpty(classSet)) {
            return;
        }

        List<SyncEntityFromDbVO> allColumnList = baseMapper.getAllColumnList();
        if (CollUtil.isEmpty(allColumnList)) {
            return;
        }

        Map<String, List<SyncEntityFromDbVO>> groupMap =
            allColumnList.stream().peek(it -> it.setColumnName(StrUtil.toCamelCase(it.getColumnName())))
                .collect(Collectors.groupingBy(SyncEntityFromDbVO::getTableName));

        String slash = "/";

        String java = ".java";

        String basePath = "/src/main/java/";

        String userDir = StrUtil.subBefore(System.getProperty("user.dir"), "\\", true);

        String apiModelPropertyTemp = "@ApiModelProperty(value = \"{}\")";

        String fieldTypeTemp = "private {} {};";

        String apiModelTemp = "@ApiModel(description = \"{}\")";

        File userDirFile = FileUtil.file(userDir);

        File[] fileArr = userDirFile.listFiles(File::isDirectory);

        List<File> fileList = CollUtil.newArrayList(fileArr);

        Map<String, String> hashMap = fileList.stream().collect(Collectors
            .toMap(it -> slash + StrUtil.replace(it.getName(), "-", "").toLowerCase() + slash, File::getName));

        StrBuilder strBuilder = StrBuilder.create();

        for (Class<?> item : classSet) {

            TableName tableName = item.getAnnotation(TableName.class);
            List<SyncEntityFromDbVO> syncEntityFromDbVOList = groupMap.get(tableName.value());
            if (CollUtil.isEmpty(syncEntityFromDbVOList)) {
                continue;
            }

            Field[] declaredFieldArr = item.getDeclaredFields();
            List<Field> declaredFieldList = CollUtil.newArrayList(declaredFieldArr);
            declaredFieldList = declaredFieldList.stream().filter(it -> {
                TableField tableField = it.getAnnotation(TableField.class);
                if (tableField != null && !tableField.exist()) {
                    return false;
                }
                return true;
            }).collect(Collectors.toList());

            if (CollUtil.isEmpty(declaredFieldList)) {
                return;
            }

            Map<String, SyncEntityFromDbVO> dbGroupMap =
                syncEntityFromDbVOList.stream().collect(Collectors.toMap(SyncEntityFromDbVO::getColumnName, it -> it));

            Set<String> delSet = new HashSet<>();
            Map<String, String> updateMap = MapUtil.newHashMap();

            ApiModel apiModel = item.getAnnotation(ApiModel.class);
            String tableComment = syncEntityFromDbVOList.get(0).getTableComment();
            if (apiModel != null && !apiModel.description().equals(tableComment)) {
                String oldVal = StrUtil.format(apiModelTemp, apiModel.description());
                String newVal = StrUtil.format(apiModelTemp, tableComment);
                updateMap.put(oldVal, newVal);
            }

            for (Field subItem : declaredFieldList) {
                SyncEntityFromDbVO syncEntityFromDbVO = dbGroupMap.get(subItem.getName());
                if (syncEntityFromDbVO == null) {
                    delSet.add(subItem.getName());
                    continue;
                }
                ApiModelProperty apiModelProperty = subItem.getAnnotation(ApiModelProperty.class);
                if (apiModelProperty == null) {
                    continue;
                }
                String apiModelPropertyValue = apiModelProperty.value();
                if (!apiModelPropertyValue.equals(syncEntityFromDbVO.getColumnComment())) {
                    String oldVal = StrUtil.format(apiModelPropertyTemp, apiModelPropertyValue);
                    String newVal = StrUtil.format(apiModelPropertyTemp, syncEntityFromDbVO.getColumnComment());
                    updateMap.put(oldVal, newVal);
                }
                if (ColumnTypeRefEnum.getByJavaType(subItem.getType().getSimpleName()) == null) {
                    continue;
                }
                ColumnTypeRefEnum columnTypeRefEnum = ColumnTypeRefEnum.getByColumnType(syncEntityFromDbVO);
                if (columnTypeRefEnum == null) {
                    continue;
                }
                if (!columnTypeRefEnum.getJavaType().equals(subItem.getType().getSimpleName())) {
                    String oldVal = StrUtil.format(fieldTypeTemp, subItem.getType().getSimpleName(), subItem.getName());
                    String newVal = StrUtil.format(fieldTypeTemp, columnTypeRefEnum.getJavaType(), subItem.getName());
                    updateMap.put(oldVal, newVal);
                }
            }

            if (delSet.size() == 0 && updateMap.size() == 0) {
                continue;
            }

            String replace = StrUtil.replace(item.getName(), ".", slash);

            AtomicReference<String> name = new AtomicReference<>();
            hashMap.entrySet().stream().filter(it -> replace.contains(it.getKey())).findFirst()
                .ifPresent(it -> name.set(it.getValue()));

            if (name.get() == null) {
                System.err.println("路径匹配失败：" + replace);
                continue;
            }

            String fullPath =
                strBuilder.append(userDir).append(slash).append(name.get()).append(basePath).append(replace)
                    .append(java).toStringAndReset();

            File file = FileUtil.file(fullPath);

            if (!FileUtil.exist(file)) {
                System.err.println("文件不存在");
                continue;
            }

            AtomicReference<String> fileStr = new AtomicReference<>(FileUtil.readUtf8String(file));

            delSet.forEach(it -> fileStr.set(StrUtil.replace(fileStr.get(), it, "")));

            updateMap.forEach((key, value) -> fileStr.set(StrUtil.replace(fileStr.get(), key, value)));

            FileUtil.writeUtf8String(fileStr.get(), file);

        }

        timeNumber = System.currentTimeMillis() - timeNumber;
        String timeStr = DateUtil.formatBetween(timeNumber);

        System.out.println("后端同步 ↑ 耗时：" + timeStr);

        System.out.println("后端同步完毕！");
    }

}
