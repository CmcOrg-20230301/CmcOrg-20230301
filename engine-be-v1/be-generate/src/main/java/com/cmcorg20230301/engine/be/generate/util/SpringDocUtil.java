package com.cmcorg20230301.engine.be.generate.util;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

public class SpringDocUtil {

    // 读取：接口的地址
    //    private static final String SPRING_DOC_ENDPOINT = "http://43.154.37.130:10001/v3/api-docs/be";
    private static final String SPRING_DOC_ENDPOINT = "http://127.0.0.1:10001/v3/api-docs/be";

    public static void main(String[] args) {

        // 开始处理
        get(SPRING_DOC_ENDPOINT);

    }

    /**
     * 开始处理
     */
    public static void get(String springDocEndpoint) {

        String bodyStr = HttpRequest.get(springDocEndpoint).execute().body();

        JSONObject jsonObject = JSONUtil.parseObj(bodyStr);

        System.out.println(jsonObject);

    }

}
