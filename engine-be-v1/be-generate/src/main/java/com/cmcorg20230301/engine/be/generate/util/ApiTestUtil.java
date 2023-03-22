package com.cmcorg20230301.engine.be.generate.util;

/**
 * 接口自动化测试工具
 */
public class ApiTestUtil {

    // 执行，接口的地址，备注：最后面不要加斜杠 /
    private static final String API_ENDPOINT = "http://43.154.37.130:10001";

    // 2023-03-23 18:31:00 之前可用：
    // Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2Nzk1NjcwNzUsInVzZXJJZCI6MH0.PRXmeSw9w9lTtwXXIaYfPqpqJdMKO_0ElK-6F4THmfo

    public static void main(String[] args) {

        exec(API_ENDPOINT);

    }

    /**
     * 执行：测试
     */
    public static void exec(String apiEndpoint) {

    }

}
