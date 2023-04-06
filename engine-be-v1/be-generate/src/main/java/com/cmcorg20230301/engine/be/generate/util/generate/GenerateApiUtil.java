package com.cmcorg20230301.engine.be.generate.util.generate;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.cmcorg20230301.engine.be.generate.model.bo.BeApi;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

    private static final String MY_ORDER_DTO = "MyOrderDTO";

    private static final String SORT = "\n    sort?: Record<string, SortOrder> // 排序字段（只在前端使用，实际传值：order）";

    private static final String SORT_IMPORT = "import {SortOrder} from \"antd/es/table/interface\";\n";

    private static final String SORT_ORDER = "SortOrder";

    private static final String UNDEFINED = "undefined";

    private static final String COLLECT = "[]";

    public static void main(String[] args) {

        // 执行
        exec(SPRING_DOC_ENDPOINT, API_PATH, TS);

    }

    /**
     * 执行
     */
    private static void exec(String springDocEndpoint, String apiPath, String ts) {

        HashMap<String, HashMap<String, BeApi>> apiMap = SpringDocUtil.get(springDocEndpoint);

        //        System.out.println(JSONUtil.toJsonStr(apiMap));

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

        // 生成 vo

    }

    /**
     * 生成 dto
     */
    private static void generateDTO(BeApi beApi, StrBuilder strBuilder, Set<String> classNameSet) {

        BeApi.BeApiSchema requestBody = beApi.getRequestBody();

        if (requestBody == null) {

            log.info("处理失败，requestBody是 null：{}", beApi.getPath());
            return;

        }

        String className = requestBody.getClassName();

        if (classNameSet.contains(className)) {
            return;
        }

        classNameSet.add(className);

        String objectStr = "object";

        if (BooleanUtil.isFalse(objectStr.equals(requestBody.getType()))) {

            log.info("处理失败，requestBody不是 object类型：{}", beApi.getPath());
            return;

        }

        // 所有字段的 StrBuilder
        StrBuilder dtoBuilder = StrBuilder.create();

        int index = 0;

        for (Map.Entry<String, BeApi.BeApiField> item : requestBody.getFieldMap().entrySet()) {

            // 一个字段
            BeApi.BeApiField beApiField = item.getValue();

            if (beApiField instanceof BeApi.BeApiParameter) {

                generateDTOParameter(dtoBuilder, item, (BeApi.BeApiParameter)beApiField);

            } else if (beApiField instanceof BeApi.BeApiSchema) {

                BeApi.BeApiSchema beApiSchema = (BeApi.BeApiSchema)beApiField;

                log.info("处理失败，beApiField BeApiSchema类型：{}", beApi.getPath());
                return;

            }

            if (index != requestBody.getFieldMap().size() - 1) {

                dtoBuilder.append("\n");

            }

            index++;

        }

        strBuilder.append(StrUtil.format(API_INTERFACE_TEMP, className, dtoBuilder.toString()));

    }

    /**
     * 生成 dto，BeApiParameter类型
     */
    private static void generateDTOParameter(StrBuilder dtoBuilder, Map.Entry<String, BeApi.BeApiField> item,
        BeApi.BeApiParameter beApiParameter) {

        String type = beApiParameter.getType();

        String integerStr = "integer";
        String formatInt64 = "int64";

        if (integerStr.equals(type)) {

            if (formatInt64.equals(beApiParameter.getFormat())) {

                type = "string";

            } else {

                type = "number";

            }

        }

        dtoBuilder.append(
            StrUtil.format(API_INTERFACE_FIELD_TEMP, item.getKey(), "?", "", type, beApiParameter.getDescription()));

        if (StrUtil.isNotBlank(beApiParameter.getPattern())) {

            dtoBuilder.append("，正则表达式：").append(beApiParameter.getPattern());

        }

        if (beApiParameter.getMaxLength() != null) {

            dtoBuilder.append("，maxLength：").append(beApiParameter.getMaxLength());

        }

        if (beApiParameter.getMinLength() != null) {

            dtoBuilder.append("，minLength：").append(beApiParameter.getMinLength());

        }

        if (BooleanUtil.isTrue(beApiParameter.getRequired())) {

            dtoBuilder.append("，required：").append(beApiParameter.getRequired());

        }

        if (StrUtil.isNotBlank(beApiParameter.getFormat())) {

            dtoBuilder.append("，format：").append(beApiParameter.getFormat());

        }

    }

}
