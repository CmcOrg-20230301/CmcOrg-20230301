package com.cmcorg20230301.engine.be.generate.fe.antd.model.enums;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.cmcorg20230301.engine.be.generate.fe.antd.model.dto.RequestFieldDTO;
import com.cmcorg20230301.engine.be.generate.model.annotation.RequestClass;
import com.cmcorg20230301.engine.be.generate.model.annotation.RequestField;
import com.cmcorg20230301.engine.be.generate.model.enums.FormInputTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import javax.validation.constraints.*;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;
import java.util.Set;

@AllArgsConstructor
@Getter
public enum ColumnTypeRefEnum {

    BOOLEAN(ColumnTypeRefEnum.TINYINT_ONE, "Boolean", "boolean"), //
    TINYINT("tinyint", "Byte", "number"), //
    BYTE("tinyint", "byte", "number"), //
    INTEGER("int", "Integer", "number"), //
    INT("int", "int", "number"), //
    BIGINT("bigint", "Long", "number"), //
    LONG("bigint", "long", "number"), //
    DECIMAL("decimal", "BigDecimal", "number"), //
    VARCHAR("varchar", "String", "string"), //
    TEXT("text", "String", "string"), //
    LONGTEXT("longtext", "String", "string"), //
    DATETIME("datetime", "Date", "string"), //
    ;

    public static final String TINYINT_ONE = "tinyint(1)";

    private final String columnType; // 数据库字段类型
    private final String javaType; // java数据类型
    private final String tsType; // ts数据类型

    // 其他常量
    private static final String CHILDREN = "children";

    /**
     * 把 class里面的变量转换为 map
     */
    @org.jetbrains.annotations.NotNull
    public static Map<String, RequestFieldDTO> getFieldMapByClazz(Class<?> clazz) {

        Field[] declaredFieldArr = ReflectUtil.getFields(clazz);

        Map<String, RequestFieldDTO> resultMap = MapUtil.newHashMap(declaredFieldArr.length, true);

        List<String> tableIgnoreFieldList = null;
        RequestClass requestClassAnnotation = clazz.getAnnotation(RequestClass.class);
        if (requestClassAnnotation != null && StrUtil.isNotBlank(requestClassAnnotation.tableIgnoreFields())) {
            tableIgnoreFieldList = StrUtil.splitTrim(requestClassAnnotation.tableIgnoreFields(), ",");
        }

        for (Field item : declaredFieldArr) {

            RequestFieldDTO requestFieldDTO = new RequestFieldDTO();

            Schema schemaAnnotation = item.getAnnotation(Schema.class);
            if (schemaAnnotation != null) {
                requestFieldDTO.setDescription(schemaAnnotation.description());
            }

            // 处理：RequestField 注解
            requestFieldAnnotationHandler(item, requestFieldDTO);

            if (schemaAnnotation != null && StrUtil.isBlank(requestFieldDTO.getFormTitle()) && StrUtil
                .isBlank(requestFieldDTO.getTableTitle())) {
                requestFieldDTO.setFormTitle(schemaAnnotation.description());
                requestFieldDTO.setTableTitle(schemaAnnotation.description());
            }

            if (CollUtil.isNotEmpty(tableIgnoreFieldList) && tableIgnoreFieldList.contains(item.getName())) {
                requestFieldDTO.setTableIgnoreFlag(true); // 设置为：忽略
            }

            String simpleName;
            if (item.getGenericType() instanceof ParameterizedType) { // 如果是 泛型类型的 class

                ParameterizedType parameterizedTypeOne = (ParameterizedType)item.getGenericType();

                if (item.getType().equals(List.class) || item.getType().equals(Set.class)) { // 如果：外层是 list或者 set
                    requestFieldDTO.setCollectFlag(true);
                }

                if (CHILDREN.equals(item.getName())) {
                    requestFieldDTO.setFieldClass(clazz); // 如果是 children，则直接就是 class类型
                    simpleName = clazz.getSimpleName();
                } else {
                    // 这里只获取一层的泛型
                    Class<Object> loadClass =
                        ClassUtil.loadClass(parameterizedTypeOne.getActualTypeArguments()[0].getTypeName());

                    requestFieldDTO.setFieldClass(loadClass);
                    simpleName = loadClass.getSimpleName();
                }
            } else { // 普通类型的 class
                simpleName = item.getType().getSimpleName();
                requestFieldDTO.setFieldClass(item.getType());
            }

            ColumnTypeRefEnum columnTypeRefEnum = getByJavaType(simpleName);

            if (columnTypeRefEnum != null) {

                requestFieldDTO.setTsType(columnTypeRefEnum.getTsType()); // ts 数据类型

            } else if (ClassUtil.isEnum(item.getType())) { // 如果是：枚举类

                List<Object> codeList = EnumUtil.getFieldValues((Class<? extends Enum<?>>)item.getType(), "code");
                if (CollUtil.isEmpty(codeList)) {
                    requestFieldDTO.setTsType("void");
                } else {
                    requestFieldDTO.setTsType(CollUtil.join(codeList, " | ")); // 列举枚举类
                }

            }

            setValid(item, requestFieldDTO); // 设置：校验相关

            resultMap.put(item.getName(), requestFieldDTO);
        }

        return resultMap;
    }

    /**
     * 处理：RequestField 注解
     */
    private static void requestFieldAnnotationHandler(Field item, RequestFieldDTO requestFieldDTO) {

        RequestField requestFieldAnnotation = item.getAnnotation(RequestField.class);

        if (requestFieldAnnotation != null) {

            requestFieldDTO.setFormDeleteNameFlag(requestFieldAnnotation.formDeleteNameFlag());

            if (StrUtil.isNotBlank(requestFieldAnnotation.formTitle())) {
                requestFieldDTO.setFormTitle(requestFieldAnnotation.formTitle());
            }
            if (StrUtil.isNotBlank(requestFieldAnnotation.tableTitle())) {
                requestFieldDTO.setTableTitle(requestFieldAnnotation.tableTitle());
            }
            if (StrUtil.isBlank(requestFieldDTO.getFormTitle()) && StrUtil
                .isNotBlank(requestFieldDTO.getTableTitle())) {
                requestFieldDTO.setFormTitle(requestFieldDTO.getTableTitle());
            } else if (StrUtil.isBlank(requestFieldDTO.getTableTitle()) && StrUtil
                .isNotBlank(requestFieldDTO.getFormTitle())) {
                requestFieldDTO.setTableTitle(requestFieldDTO.getFormTitle());
            }
            requestFieldDTO.setFormIgnoreFlag(requestFieldAnnotation.formIgnoreFlag());
            requestFieldDTO.setTableIgnoreFlag(requestFieldAnnotation.tableIgnoreFlag());
            requestFieldDTO.setFormTooltip(requestFieldAnnotation.formTooltip());
            requestFieldDTO.setTableOrderNo(requestFieldAnnotation.tableOrderNo());
            requestFieldDTO.setHideInSearchFlag(requestFieldAnnotation.hideInSearchFlag());

            requestFieldDTO.setFormInputType(requestFieldAnnotation.formInputType());
            requestFieldDTO.setFormSelectMultipleFlag(requestFieldAnnotation.formSelectMultipleFlag());

            requestFieldDTO.setFormSelectOptionsStr(requestFieldAnnotation.formSelectOptionsStr());
            requestFieldDTO.setFormSelectRequestStr(requestFieldAnnotation.formSelectRequestStr());
            requestFieldDTO.setFormSelectRequestTreeFlag(requestFieldAnnotation.formSelectRequestTreeFlag());
            requestFieldDTO
                .setFormSelectOptionsOrRequestImportStr(requestFieldAnnotation.formSelectOptionsOrRequestImportStr());

        } else {
            requestFieldDTO.setTableOrderNo(Integer.MIN_VALUE);
            requestFieldDTO.setFormInputType(FormInputTypeEnum.TEXT);
        }
    }

    /**
     * 设置：校验相关
     */
    private static void setValid(Field item, RequestFieldDTO requestFieldDTO) {

        NotNull notNullAnnotation = item.getAnnotation(NotNull.class);
        if (notNullAnnotation != null) {
            requestFieldDTO.setNotNull(true);
        } else {
            requestFieldDTO.setNotNull(false);
        }
        NotBlank notBlankAnnotation = item.getAnnotation(NotBlank.class);
        if (notBlankAnnotation != null) {
            requestFieldDTO.setNotBlank(true);
        } else {
            requestFieldDTO.setNotBlank(false);
        }
        NotEmpty notEmptyAnnotation = item.getAnnotation(NotEmpty.class);
        if (notEmptyAnnotation != null) {
            requestFieldDTO.setNotEmpty(true);
        } else {
            requestFieldDTO.setNotEmpty(false);
        }
        Size sizeAnnotation = item.getAnnotation(Size.class);
        if (sizeAnnotation != null) {
            requestFieldDTO.setSizeMax(sizeAnnotation.max());
            requestFieldDTO.setSizeMin(sizeAnnotation.min());
        }
        Max maxAnnotation = item.getAnnotation(Max.class);
        if (maxAnnotation != null) {
            requestFieldDTO.setMax(maxAnnotation.value());
        }
        Min minAnnotation = item.getAnnotation(Min.class);
        if (minAnnotation != null) {
            requestFieldDTO.setMin(minAnnotation.value());
        }
        Pattern patternAnnotation = item.getAnnotation(Pattern.class);
        if (patternAnnotation != null) {
            requestFieldDTO.setRegexp(patternAnnotation.regexp());
        }

    }

    @Nullable
    public static ColumnTypeRefEnum getByJavaType(String javaType) {
        if (javaType == null) {
            return null;
        }
        for (ColumnTypeRefEnum item : ColumnTypeRefEnum.values()) {
            if (item.getJavaType().equalsIgnoreCase(javaType)) {
                return item;
            }
        }
        return null;
    }

}
