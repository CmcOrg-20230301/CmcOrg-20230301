package com.cmcorg20230301.engine.be.generate.util.generate;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.CharPool;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cmcorg20230301.engine.be.generate.model.bo.BeApi;
import com.cmcorg20230301.engine.be.model.model.dto.MyOrderDTO;
import com.cmcorg20230301.engine.be.security.model.vo.ApiResultVO;
import com.cmcorg20230301.engine.be.util.util.CallBack;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

/**
 * 生成 api的工具类
 */
@Slf4j
public class GenerateApiUtil {

    // 读取：接口的地址
    private static final String SPRING_DOC_ENDPOINT = "http://43.154.37.130:10001/v3/api-docs/be";
    //    private static final String SPRING_DOC_ENDPOINT = "http://127.0.0.1:10001/v3/api-docs/be";

    private static final String SYSTEM_USER_DIR = System.getProperty("user.dir"); // 例如：D:\GitHub\CmcOrg-20230301

    private static final String API_PATH = SYSTEM_USER_DIR + "/fe-antd-v1/src/api/";

    private static final String TS = ".ts";

    private static final String API_INTERFACE_TEMP = "\nexport interface {} {\n{}\n}\n";

    private static final String API_INTERFACE_FIELD_TEMP = "    {}{}: {}{} // {}";

    private static final String API_REQUEST_TEMP =
        "\n" + "// {}\n" + "export function {}({}config?: AxiosRequestConfig) {\n"
            + "    return $http.{}<{}>('{}', {}, config)\n" + "}\n";

    private static final String API_REQUEST_FORM_NAME = "form";

    private static final String API_REQUEST_FORM_TEMP = API_REQUEST_FORM_NAME + ": {}, ";

    private static final String API_IMPORT_BASE =
        "import $http from \"@/util/HttpUtil\";\nimport {AxiosRequestConfig} from \"axios\";\n";

    private static final String API_IMPORT_BASE_MY_ORDER_DTO = "import MyOrderDTO from \"@/model/dto/MyOrderDTO\";\n";

    private static final String SORT = "\n    sort?: Record<string, SortOrder> // 排序字段（只在前端使用，实际传值：order）";

    private static final String SORT_IMPORT = "import {SortOrder} from \"antd/es/table/interface\";\n";

    private static final String SORT_ORDER = "SortOrder";

    private static final String UNDEFINED = "undefined";

    public static void main(String[] args) {

        // 执行
        exec(SPRING_DOC_ENDPOINT, API_PATH, TS);

    }

    /**
     * 执行
     */
    private static void exec(String springDocEndpoint, String apiPath, String ts) {

        HashMap<String, HashMap<String, BeApi>> apiMap = SpringDocUtil.get(springDocEndpoint);

        System.out.println(JSONUtil.toJsonStr(apiMap));

        log.info("清除：api文件夹：{}", API_PATH);
        FileUtil.del(API_PATH);

        log.info("生成：api文件夹：执行开始 =====================>");
        long startTs = System.currentTimeMillis();

        StrBuilder strBuilder = StrBuilder.create();

        for (Map.Entry<String, HashMap<String, BeApi>> item : apiMap.entrySet()) {

            // 生成：api文件
            File apiFile = FileUtil.touch(apiPath + item.getKey() + ts);

            // 要导入的基础内容
            strBuilder.append(API_IMPORT_BASE);

            Set<String> classNameSet = new HashSet<>(); // 防止重复写入

            for (BeApi subItem : item.getValue().values()) {

                if (CollUtil.newArrayList("/sys/file/upload").contains(subItem.getPath())) {
                    continue;
                }

                // 处理
                handler(subItem, strBuilder, classNameSet);

            }

            String content = strBuilder.toStringAndReset();

            FileUtil.writeUtf8String(content, apiFile); // 写入文件里

        }

        log.info("生成：api文件夹：执行结束 =====================> 耗时：{}毫秒", System.currentTimeMillis() - startTs);

    }

    /**
     * 处理
     */
    private static void handler(BeApi beApi, StrBuilder strBuilder, Set<String> classNameSet) {

        // 生成 dto
        generateDTO(beApi, strBuilder, classNameSet);

        // 生成 api的时候，是否使用：myProPagePost
        CallBack<Boolean> myProPagePostCallBack = new CallBack<>(false);

        // 生成 vo
        generateVO(beApi, strBuilder, classNameSet, myProPagePostCallBack);

        // 生成 api
        generateApi(beApi, strBuilder, classNameSet, myProPagePostCallBack);

    }

    /**
     * 生成 api
     */
    private static void generateApi(BeApi beApi, StrBuilder strBuilder, Set<String> classNameSet,
        CallBack<Boolean> myProPagePostCallBack) {

        // api的方法名
        String apiName = getApiName(beApi.getPath());

        String formStr = ""; // 拼接 form参数
        String formValueStr = UNDEFINED; // 拼接 form值

        Map<String, BeApi.BeApiField> parameter = beApi.getParameter();

        if (beApi.getRequestBody() != null && StrUtil.isNotBlank(beApi.getRequestBody().getClassName())) {

            formStr = StrUtil.format(API_REQUEST_FORM_TEMP, beApi.getRequestBody().getClassName());
            formValueStr = API_REQUEST_FORM_NAME;

        } else if (CollUtil.isNotEmpty(parameter)) {

            // 备注：这里只获取一个参数
            BeApi.BeApiField beApiField = parameter.get(new ArrayList<>(parameter.keySet()).get(0));

            // 如果是：对象类型
            if (beApiField instanceof BeApi.BeApiSchema) {

                BeApi.BeApiSchema parameterBeApiSchema = (BeApi.BeApiSchema)beApiField;

                formStr = StrUtil.format(API_REQUEST_FORM_TEMP, parameterBeApiSchema.getClassName());
                formValueStr = API_REQUEST_FORM_NAME;

            }

        }

        String returnTypeStr = beApi.getReturnTypeStr(); // 返回的类型

        if (StrUtil.isBlank(returnTypeStr)) {
            returnTypeStr = "void";
        }

        String httpStr; // 请求的类型

        if (beApi.getPath().contains("infoById")) {

            httpStr = "myProPost";

        } else if (myProPagePostCallBack.getValue()) {

            httpStr = "myProPagePost";

        } else if (beApi.getPath().contains("tree")) {

            httpStr = "myProTreePost";

        } else {

            httpStr = "myPost";

            if (BooleanUtil.isTrue(beApi.getReturnTypeArrFlag())) {
                returnTypeStr = returnTypeStr + "[]";
            }

        }

        strBuilder.append(StrUtil
            .format(API_REQUEST_TEMP, beApi.getSummary(), apiName, formStr, httpStr, returnTypeStr, beApi.getPath(),
                formValueStr));

    }

    /**
     * 通过：path，获取：api的方法名
     */
    @NotNull
    static String getApiName(String path) {

        List<String> splitTrimList = StrUtil.splitTrim(path, CharPool.SLASH);

        return splitTrimList.stream().reduce((x, y) -> StrUtil.upperFirst(x) + StrUtil.upperFirst(y)).orElse("");

    }

    /**
     * 生成 vo
     */
    private static void generateVO(BeApi beApi, StrBuilder strBuilder, Set<String> classNameSet,
        CallBack<Boolean> myProPagePostCallBack) {

        BeApi.BeApiSchema response = (BeApi.BeApiSchema)beApi.getResponse();

        if (response == null) {
            return;
        }

        boolean apiResultVOFlag = response.getClassName().startsWith(ApiResultVO.class.getSimpleName());

        if (apiResultVOFlag) {

            BeApi.BeApiSchema beApiSchema = (BeApi.BeApiSchema)response.getFieldMap().get(response.getClassName());

            BeApi.BeApiField data = beApiSchema.getFieldMap().get("data");

            if (data instanceof BeApi.BeApiSchema) {

                BeApi.BeApiSchema dataBeApiSchema = (BeApi.BeApiSchema)data;

                beApi.setReturnTypeArrFlag(dataBeApiSchema.getArrFlag());

                BeApi.BeApiSchema dataRealBeApiSchema =
                    (BeApi.BeApiSchema)dataBeApiSchema.getFieldMap().get(dataBeApiSchema.getClassName());

                // 如果是：分页排序查询相关
                if (dataRealBeApiSchema.getClassName().startsWith(Page.class.getSimpleName())) {

                    BeApi.BeApiSchema records = (BeApi.BeApiSchema)dataRealBeApiSchema.getFieldMap().get("records");

                    BeApi.BeApiSchema recordsBeApiSchema =
                        (BeApi.BeApiSchema)records.getFieldMap().get(records.getClassName());

                    beApi.setReturnTypeStr(recordsBeApiSchema.getClassName());

                    // 生成：interface
                    generateInterface(beApi, strBuilder, classNameSet, recordsBeApiSchema, "vo-page：");

                    myProPagePostCallBack.setValue(true); // 设置：回调值

                } else {

                    beApi.setReturnTypeStr(dataRealBeApiSchema.getClassName());

                    // 生成：interface
                    generateInterface(beApi, strBuilder, classNameSet, dataRealBeApiSchema, "vo：");

                }

            } else if (data instanceof BeApi.BeApiParameter) {

                BeApi.BeApiParameter dataBeApiParameter = (BeApi.BeApiParameter)data;

                String type = dataBeApiParameter.getType();

                // 处理：integer类型
                type = handleIntegerType(dataBeApiParameter, type);

                beApi.setReturnTypeStr(type);

                beApi.setReturnTypeArrFlag(dataBeApiParameter.getArrFlag());

            }

        } else {

            log.info("暂不支持其他类型的 vo：{}，name：{}", beApi.getPath(), response.getName());

        }

    }

    /**
     * 生成 dto
     */
    private static void generateDTO(BeApi beApi, StrBuilder strBuilder, Set<String> classNameSet) {

        BeApi.BeApiSchema requestBody = null;

        Map<String, BeApi.BeApiField> parameter = beApi.getParameter();

        if (CollUtil.isNotEmpty(parameter)) {

            // 备注：这里只获取一个参数
            BeApi.BeApiField beApiField = parameter.get(new ArrayList<>(parameter.keySet()).get(0));

            // 如果是：对象类型
            if (beApiField instanceof BeApi.BeApiSchema) {

                BeApi.BeApiSchema parameterBeApiSchema = (BeApi.BeApiSchema)beApiField;

                requestBody =
                    (BeApi.BeApiSchema)parameterBeApiSchema.getFieldMap().get(parameterBeApiSchema.getClassName());

            }

        } else {

            requestBody = beApi.getRequestBody();

        }

        if (requestBody == null) {

            log.info("处理失败，requestBody是 null，path：{}", beApi.getPath());
            return;

        }

        String objectStr = "object";

        if (BooleanUtil.isFalse(objectStr.equals(requestBody.getType()))) {

            log.info("处理失败，requestBody不是 object类型，path：{}", beApi.getPath());
            return;

        }

        // 生成：interface
        generateInterface(beApi, strBuilder, classNameSet, requestBody, "dto：");

    }

    /**
     * 生成：interface
     */
    private static void generateInterface(BeApi beApi, StrBuilder strBuilder, Set<String> classNameSet,
        BeApi.BeApiSchema beApiSchema, String preMsg) {

        String className = beApiSchema.getClassName();

        if (classNameSet.contains(className)) {
            return;
        }

        classNameSet.add(className);

        // 所有字段的 StrBuilder
        StrBuilder interfaceBuilder = StrBuilder.create();

        int index = 0;

        for (Map.Entry<String, BeApi.BeApiField> item : beApiSchema.getFieldMap().entrySet()) {

            // 一个字段
            BeApi.BeApiField beApiField = item.getValue();

            if (beApiField instanceof BeApi.BeApiParameter) {

                // 生成 interface，BeApiParameter类型
                generateInterfaceParameter(interfaceBuilder, item.getKey(), (BeApi.BeApiParameter)beApiField);

            } else if (beApiField instanceof BeApi.BeApiSchema) {

                // 生成 interface，BeApiSchema类型
                generateInterfaceSchema(beApi, strBuilder, classNameSet, interfaceBuilder,
                    (BeApi.BeApiSchema)beApiField, preMsg);

            }

            if (index != beApiSchema.getFieldMap().size() - 1) {

                interfaceBuilder.append("\n");

            }

            index++;

        }

        strBuilder.append(StrUtil.format(API_INTERFACE_TEMP, className, interfaceBuilder.toString()));

    }

    /**
     * 生成 interface，BeApiSchema类型
     */
    private static void generateInterfaceSchema(BeApi beApi, StrBuilder strBuilder, Set<String> classNameSet,
        StrBuilder interfaceBuilder, BeApi.BeApiSchema beApiSchema, String preMsg) {

        // 如果是：排序字段
        if (beApiSchema.getClassName().equals(MyOrderDTO.class.getSimpleName())) {

            if (BooleanUtil.isFalse(classNameSet.contains(beApiSchema.getClassName()))) {

                classNameSet.add(beApiSchema.getClassName());

                strBuilder.insert(0, API_IMPORT_BASE_MY_ORDER_DTO); // 在顶部添加导入

                classNameSet.add(SORT_ORDER);

                strBuilder.insert(0, SORT_IMPORT); // 在顶部添加导入

            }

            interfaceBuilder.append(StrUtil
                .format(API_INTERFACE_FIELD_TEMP, beApiSchema.getName(), "?", beApiSchema.getClassName(), "", "排序字段"));

            interfaceBuilder.append(SORT);

        } else {

            interfaceBuilder.append(StrUtil
                .format(API_INTERFACE_FIELD_TEMP, beApiSchema.getName(), "?", beApiSchema.getClassName(),
                    BooleanUtil.isTrue(beApiSchema.getArrFlag()) ? "[]" : "", beApiSchema.getDescription()));

        }

    }

    /**
     * 生成 interface，BeApiParameter类型
     */
    private static void generateInterfaceParameter(StrBuilder interfaceBuilder, String fieldName,
        BeApi.BeApiParameter beApiParameter) {

        String type = beApiParameter.getType();

        // 处理：integer类型
        type = handleIntegerType(beApiParameter, type);

        interfaceBuilder.append(StrUtil.format(API_INTERFACE_FIELD_TEMP, fieldName, "?", type,
            BooleanUtil.isTrue(beApiParameter.getArrFlag()) ? "[]" : "", beApiParameter.getDescription()));

        if (StrUtil.isNotBlank(beApiParameter.getPattern())) {

            interfaceBuilder.append("，正则表达式：").append(beApiParameter.getPattern());

        }

        if (beApiParameter.getMaxLength() != null) {

            interfaceBuilder.append("，maxLength：").append(beApiParameter.getMaxLength());

        }

        if (beApiParameter.getMinLength() != null) {

            interfaceBuilder.append("，minLength：").append(beApiParameter.getMinLength());

        }

        if (BooleanUtil.isTrue(beApiParameter.getRequired())) {

            interfaceBuilder.append("，required：").append(beApiParameter.getRequired());

        }

        if (StrUtil.isNotBlank(beApiParameter.getFormat())) {

            interfaceBuilder.append("，format：").append(beApiParameter.getFormat());

        }

    }

    /**
     * 处理：integer类型
     */
    private static String handleIntegerType(BeApi.BeApiParameter beApiParameter, String type) {

        String integerStr = "integer";
        String formatInt64 = "int64";

        if (integerStr.equals(type)) {

            if (formatInt64.equals(beApiParameter.getFormat())) {

                type = "string"; // long 转换为 string

            } else {

                type = "number";

            }

        }

        return type;

    }

}
