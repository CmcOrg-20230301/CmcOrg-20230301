package com.cmcorg20230301.engine.be.generate.util.generate;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.cmcorg20230301.engine.be.generate.model.bo.BeApi;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

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

        //        HashMap<String, HashMap<String, BeApi>> apiMap = SpringDocUtil.get(springDocEndpoint);

        JSONObject apiJson = JSONUtil.parseObj(FileUtil.readUtf8String("apiMap.json"));

        HashMap<String, HashMap<String, BeApi>> apiMap = new HashMap<>(apiJson.size());

        BeanUtil.copyProperties(apiJson, apiMap);

        System.out.println(JSONUtil.toJsonStr(apiMap));

        for (Map.Entry<String, HashMap<String, BeApi>> item : apiMap.entrySet()) {

        }

    }

}
