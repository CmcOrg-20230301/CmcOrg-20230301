package com.cmcorg20230301.engine.be.generate.util.generate;

import cn.hutool.core.collection.CollUtil;
import com.cmcorg20230301.engine.be.generate.model.bo.BeApi;
import com.cmcorg20230301.engine.be.generate.util.apitest.ApiTestHelper;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;

/**
 * 生成页面的工具类
 */
@Slf4j
public class GeneratePageUtil {

    // 读取：接口的地址
    private static final String SPRING_DOC_ENDPOINT = "http://43.154.37.130:10001/v3/api-docs/be";
    //    private static final String SPRING_DOC_ENDPOINT = "http://127.0.0.1:10001/v3/api-docs/be";

    private static final String SYSTEM_USER_DIR = System.getProperty("user.dir"); // 例如：D:\GitHub\CmcOrg-20230301

    private static final String PAGE_PATH = SYSTEM_USER_DIR + "/fe-antd-v1/src/page/sys";

    private static final String TS = ".ts";

    public static void main(String[] args) {

        String group = ApiTestHelper.getStrFromScanner("请输入要生成页面的 group");

        // 执行
        exec(SPRING_DOC_ENDPOINT, PAGE_PATH, TS, group);

    }

    /**
     * 执行
     */
    private static void exec(String springDocEndpoint, String pagePath, String ts, String group) {

        HashMap<String, HashMap<String, BeApi>> apiMap = SpringDocUtil.get(springDocEndpoint);

        //        System.out.println(JSONUtil.toJsonStr(apiMap));

        HashMap<String, BeApi> pathBeApiMap = apiMap.get(group);

        if (CollUtil.isEmpty(pathBeApiMap)) {

            log.info("操作失败：group不存在");
            return;

        }

    }

}
