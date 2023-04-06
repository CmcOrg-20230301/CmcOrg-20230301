package com.cmcorg20230301.engine.be.generate.util.generate;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.cmcorg20230301.engine.be.generate.model.bo.BeApi;
import lombok.extern.slf4j.Slf4j;

/**
 * 生成 api的工具类
 */
@Slf4j
public class GenerateApiUtil {

    // 读取：接口的地址
    //    private static final String SPRING_DOC_ENDPOINT = "http://43.154.37.130:10001/v3/api-docs/be";
    private static final String SPRING_DOC_ENDPOINT = "http://127.0.0.1:10001/v3/api-docs/be";

    public static void main(String[] args) {

        // 执行
        exec(SPRING_DOC_ENDPOINT);

    }

    /**
     * 执行
     */
    private static void exec(String springDocEndpoint) {

        //        HashMap<String, BeApi> apiList = SpringDocUtil.get(springDocEndpoint);

        JSONArray apiJsonArr = JSONUtil.parseArray(FileUtil.readUtf8String("apiList.json"));

        //        List<BeApi> apiList = new ArrayList<>();

        //        BeanUtil.copyProperties(apiJsonArr, apiList);

        //        System.out.println(JSONUtil.toJsonStr(apiList));

        System.out.println(System.getProperty("user.dir")); // 例如：D:\GitHub\CmcOrg-20230301

        //        for (Map.Entry<String, BeApi> item : apiList.entrySet()) {
        //
        //            handler(item.getValue());
        //
        //        }

    }

    /**
     * 处理
     */
    private static void handler(BeApi beApi) {

    }

}
