package com.cmcorg20230301.engine.be.generate.util.generate;

import com.cmcorg20230301.engine.be.generate.model.bo.BeApi;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;

/**
 * 生成 api的工具类
 */
@Slf4j
public class GenerateApiUtil {

    // 读取：接口的地址
    private static final String SPRING_DOC_ENDPOINT = "http://43.154.37.130:10001/v3/api-docs/be";
    //    private static final String SPRING_DOC_ENDPOINT = "http://127.0.0.1:10001/v3/api-docs/be";

    private static final String SYSTEM_USER_DIR = System.getProperty("user.dir"); // 例如：D:\GitHub\CmcOrg-20230301

    private static final String API_PATH = SYSTEM_USER_DIR + "/fe-antd-v1/src/api";

    public static void main(String[] args) {

        // 执行
        exec(SPRING_DOC_ENDPOINT, API_PATH);

    }

    /**
     * 执行
     */
    private static void exec(String springDocEndpoint, String apiPath) {

        HashMap<String, HashMap<String, BeApi>> apiMap = SpringDocUtil.get(springDocEndpoint);

    }

    /**
     * 处理
     */
    private static void handler(BeApi beApi) {

    }

}
