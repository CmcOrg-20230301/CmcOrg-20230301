package com.cmcorg20230301.engine.be.generate.fe.antd.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.cmcorg.engine.web.model.model.constant.LogTopicConstant;
import com.cmcorg.generate.page.h5.util.model.dto.PageDTO;
import com.cmcorg.generate.page.h5.util.model.dto.RequestDTO;
import com.cmcorg.generate.page.h5.util.model.dto.RequestFieldDTO;
import com.cmcorg.generate.page.h5.util.model.dto.WebDTO;
import com.cmcorg.generate.page.h5.util.model.enums.ColumnTypeRefEnum;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j(topic = LogTopicConstant.JAVA_TO_WEB)
public class WebJsonToWebGenerateApiUtil {

    private static final String TEMPLATE_NAME =
        "E:/GitHub/CmcOrg/generate-page-h5-v1/generate-page-h5/src/main/resources/template/";
    public static String WEB_SRC_API_PATH = "D:/GitHub/CmcOrg/frontend-h5-web-v1/src/api/";

    private static final String WEB_SRC_API_SUF = ".ts"; // api文件的后缀

    // API Interface 模板
    private static final String API_INTERFACE_TEMP = "\nexport interface {} {\n{}\n}\n";
    // API Interface 字段 模板
    private static final String API_INTERFACE_FIELD_TEMP = "    {}{}: {}{} // {}";
    // API 接口 模板
    private static final String API_REQUEST_TEMP = FileUtil.readUtf8String(TEMPLATE_NAME + "ApiRequestTemp.txt");
    // API 接口 form 模板
    private static final String API_REQUEST_FORM_NAME = "form";
    private static final String API_REQUEST_FORM_TEMP = API_REQUEST_FORM_NAME + ": {}, ";
    // API import 基础
    private static final String API_IMPORT_BASE =
        "import $http from \"@/util/HttpUtil\";\nimport {AxiosRequestConfig} from \"axios\";\n";
    // API import MyOrderDTO
    private static final String API_IMPORT_BASE_MY_ORDER_DTO = "import MyOrderDTO from \"@/model/dto/MyOrderDTO\";\n";
    private static final String MY_ORDER_DTO = "MyOrderDTO";

    // 写 interface时，如果是继承 MyPageDTO，前端需要额外写上该属性
    private static final String SORT = "\n    sort?: Record<string, SortOrder> // 排序字段（只在前端使用，实际传值：order）";
    private static final String SORT_IMPORT = "import {SortOrder} from \"antd/es/table/interface\";\n";
    private static final String SORT_ORDER = "SortOrder";

    // 其他常量
    private static final String UNDEFINED = "undefined";
    private static final String COLLECT = "[]";

    /**
     * 生成api
     */
    public static void generateApi(WebDTO webDTO) {

        log.info("清除 api文件夹：{}", WEB_SRC_API_PATH);
        FileUtil.del(WEB_SRC_API_PATH);

        log.info("生成 api文件夹：执行开始 =====================>");
        TimeInterval timer = DateUtil.timer();

        StrBuilder strBuilder = StrBuilder.create();

        for (PageDTO item : webDTO.getPageList()) {

            File mkdir = FileUtil.mkdir(WEB_SRC_API_PATH + item.getType().name().toLowerCase());

            // 生成 api文件
            File touchFile = FileUtil.touch(mkdir, item.getFileName() + WEB_SRC_API_SUF);

            // 要导入的基础内容
            strBuilder.append(API_IMPORT_BASE);

            Set<String> importClassNameSet = new HashSet<>(); // 防止重复写入
            Set<String> classNameSet = new HashSet<>(); // 防止重复写入

            // 构建 api文件里面的内容
            for (RequestDTO subItem : item.getRequestList()) {

                // 生成 dto
                if (subItem.getParamClass() != null && !classNameSet
                    .contains(subItem.getParamClass().getSimpleName())) {
                    classNameSet.add(subItem.getParamClass().getSimpleName());
                    strBuilder.append(StrUtil.format(API_INTERFACE_TEMP, subItem.getParamClass().getSimpleName(),
                        tsInterfaceField(importClassNameSet, strBuilder, subItem.getFormMap(), classNameSet, subItem)));
                }

                // 生成 vo
                if ((BooleanUtil.orOfWrap(subItem.getInfoByIdFlag(), subItem.getPageFlag(), subItem.getTreeFlag()))) {
                    if (!classNameSet.contains(subItem.getReturnRealClass().getSimpleName())) {
                        classNameSet.add(subItem.getReturnRealClass().getSimpleName());
                        strBuilder.append(StrUtil
                            .format(API_INTERFACE_TEMP, subItem.getReturnRealClass().getSimpleName(),
                                tsInterfaceField(importClassNameSet, strBuilder,
                                    ColumnTypeRefEnum.getFieldMapByClazz(subItem.getReturnRealClass()), classNameSet,
                                    subItem)));
                    }
                }

                // 获取：接口请求类型和返回值类型
                String[] returnForTsArr = returnForTsArr(subItem);

                String formStr = ""; // 拼接 form参数
                String formValueStr = UNDEFINED; // 拼接 form值
                if (subItem.getParamClass() != null) {
                    formStr = StrUtil.format(API_REQUEST_FORM_TEMP, subItem.getParamClass().getSimpleName());
                    formValueStr = API_REQUEST_FORM_NAME;
                }

                // 生成接口
                strBuilder.append(StrUtil
                    .format(API_REQUEST_TEMP, subItem.getDescription(), subItem.getFullUriHump(), formStr,
                        returnForTsArr[0], returnForTsArr[1], subItem.getFullUri(), formValueStr));
            }

            // 写入内容到文件里
            FileUtil.writeUtf8String(strBuilder.toStringAndReset(), touchFile);
        }

        long interval = timer.interval();
        log.info("生成 api文件夹：执行结束 =====================> 耗时：{}毫秒", interval);
    }

    /**
     * tsInterface，字段生成
     */
    private static String tsInterfaceField(Set<String> importClassNameSet, StrBuilder allStrBuilder,
        Map<String, RequestFieldDTO> fieldMap, Set<String> classNameSet, RequestDTO subItem) {

        StrBuilder strBuilder = StrBuilder.create();
        int index = 0;
        for (Map.Entry<String, RequestFieldDTO> item : fieldMap.entrySet()) {

            RequestFieldDTO dto = item.getValue();
            boolean notNullFlag = BooleanUtil.orOfWrap(dto.getNotNull(), dto.getNotBlank(), dto.getNotEmpty());

            String fieldTypeStr =
                StrUtil.isBlank(dto.getTsType()) ? dto.getFieldClass().getSimpleName() : dto.getTsType();

            if (MY_ORDER_DTO.equals(fieldTypeStr) && !importClassNameSet.contains(MY_ORDER_DTO)) {
                importClassNameSet.add(MY_ORDER_DTO);
                allStrBuilder.insert(0, API_IMPORT_BASE_MY_ORDER_DTO); // 在顶部添加导入
            } else if (StrUtil.isBlank(dto.getTsType()) && !classNameSet.contains(fieldTypeStr)) {

                classNameSet.add(fieldTypeStr);

                allStrBuilder.append(StrUtil.format(API_INTERFACE_TEMP, dto.getFieldClass().getSimpleName(),
                    tsInterfaceField(importClassNameSet, allStrBuilder,
                        ColumnTypeRefEnum.getFieldMapByClazz(dto.getFieldClass()), classNameSet,
                        subItem))); // 递归写：其他的 class

            }

            RequestFieldDTO requestFieldDTO = new RequestFieldDTO();
            requestFieldDTO.setSizeMax(dto.getSizeMax());
            requestFieldDTO.setSizeMin(dto.getSizeMin());
            requestFieldDTO.setMax(dto.getMax());
            requestFieldDTO.setMin(dto.getMin());
            requestFieldDTO.setRegexp(dto.getRegexp());

            String jsonStr = JSONUtil.toJsonStr(requestFieldDTO);

            String description = dto.getDescription();
            if (!"{}".equals(jsonStr)) {
                description = description + " " + jsonStr;
            }

            strBuilder.append(StrUtil
                .format(API_INTERFACE_FIELD_TEMP, item.getKey(), notNullFlag ? "" : "?", fieldTypeStr,
                    BooleanUtil.isTrue(dto.getCollectFlag()) ? COLLECT : "", description));
            if (index != fieldMap.size() - 1) {
                strBuilder.append("\n");
            } else { // 如果是：最后一个元素
                if (BooleanUtil.isTrue(subItem.getPageFlag())) { // 如果 page类型接口
                    if (!importClassNameSet.contains(SORT_ORDER)) {
                        importClassNameSet.add(SORT_ORDER);
                        allStrBuilder.insert(0, SORT_IMPORT); // 在顶部添加导入
                        strBuilder.append(SORT); // 添加 sort字段
                    }
                }
            }
            index++;
        }

        return strBuilder.toString();
    }

    /**
     * 获取：接口请求类型和返回值类型
     */
    private static String[] returnForTsArr(RequestDTO dto) {

        String[] resultArr = new String[] {"myPost", "void"};

        if (BooleanUtil.isTrue(dto.getInfoByIdFlag())) {
            resultArr = new String[] {"myProPost", dto.getReturnRealClass().getSimpleName()};
        } else if (BooleanUtil.isTrue(dto.getPageFlag())) {
            resultArr = new String[] {"myProPagePost", dto.getReturnRealClass().getSimpleName()};
        } else if (BooleanUtil.isTrue(dto.getTreeFlag())) {
            resultArr = new String[] {"myProTreePost", dto.getReturnRealClass().getSimpleName()};
        } else if (dto.getReturnRealClass() != null) {

            ColumnTypeRefEnum columnTypeRefEnum =
                ColumnTypeRefEnum.getByJavaType(dto.getReturnRealClass().getSimpleName());

            if (columnTypeRefEnum != null) {
                resultArr = new String[] {"myPost", columnTypeRefEnum.getTsType()};
            }
        }

        return resultArr;
    }

}
