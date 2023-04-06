package com.cmcorg20230301.engine.be.generate.util.generate;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
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

        System.out.println(JSONUtil.toJsonStr(apiMap));

        log.info("清除：api文件夹：{}", API_PATH);
        FileUtil.del(API_PATH);

        log.info("生成：api文件夹：执行开始 =====================>");
        long startTs = System.currentTimeMillis();

        for (Map.Entry<String, HashMap<String, BeApi>> item : apiMap.entrySet()) {

            // 生成：api文件
            File apiFile = FileUtil.touch(apiPath + item.getKey() + ts);

            StrBuilder strBuilder = StrBuilder.create();

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

        String className = requestBody.getClassName();

        StrBuilder dtoBuilder = StrBuilder.create();

        strBuilder.append(StrUtil.format(API_INTERFACE_TEMP, className));

    }

}
