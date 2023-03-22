package com.cmcorg20230301.engine.be.generate.util;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.StrMatcher;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.cmcorg20230301.engine.be.generate.model.bo.BeApi;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class SpringDocUtil {

    // 读取：接口的地址
    //    private static final String SPRING_DOC_ENDPOINT = "http://43.154.37.130:10001/v3/api-docs/be";
    private static final String SPRING_DOC_ENDPOINT = "http://127.0.0.1:10001/v3/api-docs/be";

    private static final String BE_API_SCHEMA_MAP_KEY = "beApiSchemaMapKey";
    private static final StrMatcher BE_API_SCHEMA_MAP_KEY_STR_MATCHER =
        new StrMatcher("#/components/schemas/${" + BE_API_SCHEMA_MAP_KEY + "}");

    public static void main(String[] args) {

        // 执行获取
        HashMap<String, BeApi> result = get(SPRING_DOC_ENDPOINT);

        //        System.out.println(result);

    }

    /**
     * 执行获取，key是 path
     */
    public static HashMap<String, BeApi> get(String springDocEndpoint) {

        String bodyStr = HttpRequest.get(springDocEndpoint).execute().body();

        JSONObject jsonObject = JSONUtil.parseObj(bodyStr);

        System.out.println(jsonObject);

        JSONObject paths = jsonObject.getJSONObject("paths");

        HashMap<String, BeApi> result = MapUtil.newHashMap(paths.size()); // 本方法的返回值

        JSONObject components = jsonObject.getJSONObject("components");

        JSONObject schemas = components.getJSONObject("schemas");

        HashMap<String, BeApi.BeApiSchema> beApiSchemaMap = MapUtil.newHashMap(schemas.size()); // 所有的对象，key是对象的名称

        List<BeApi.BeApiSchema> todoHandleBeApiSchemaList = new ArrayList<>(); // 待处理的：对象类型集合

        // 处理：所有的对象
        handleBeApiSchemaMap(schemas, beApiSchemaMap, todoHandleBeApiSchemaList);

        // 处理：待处理的：对象类型集合
        handleTodoHandleBeApiSchemaList(beApiSchemaMap, todoHandleBeApiSchemaList);

        for (Map.Entry<String, Object> item : paths.entrySet()) {

            JSONObject value = (JSONObject)item.getValue();

            JSONObject post = value.getJSONObject("post");
            JSONObject get = value.getJSONObject("get");

            BeApi beApi = new BeApi();
            beApi.setPath(item.getKey());
            //            beApi.setMethod();
            //            beApi.setTag();
            //            beApi.setSummary();
            //            beApi.setContentType();
            //            beApi.setRequestBody();
            //            beApi.setParameter();
            //            beApi.setResponse();

            result.put(item.getKey(), beApi); // 添加到返回值里

        }

        return result;

    }

    /**
     * 处理：待处理的：对象类型集合
     */
    private static void handleTodoHandleBeApiSchemaList(HashMap<String, BeApi.BeApiSchema> beApiSchemaMap,
        List<BeApi.BeApiSchema> todoHandleBeApiSchemaList) {

        for (BeApi.BeApiSchema item : todoHandleBeApiSchemaList) {

            for (Map.Entry<String, BeApi.BeApiField> subItem : item.getFieldMap().entrySet()) {

                if (subItem.getValue() != null) {
                    continue;
                }

                BeApi.BeApiSchema propertiesBeApiSchema = beApiSchemaMap.get(subItem.getKey());

                if (propertiesBeApiSchema == null) {

                    log.info("处理失败：name：{}，key：{}", item.getName(), subItem.getKey());

                } else {

                    subItem.setValue(propertiesBeApiSchema);

                }

            }

        }

    }

    /**
     * 处理：所有的对象
     */
    private static void handleBeApiSchemaMap(JSONObject schemas, HashMap<String, BeApi.BeApiSchema> beApiSchemaMap,
        List<BeApi.BeApiSchema> todoHandleBeApiSchemaList) {

        for (Map.Entry<String, Object> item : schemas.entrySet()) {

            JSONObject value = (JSONObject)item.getValue();

            BeApi.BeApiSchema apiSchema = new BeApi.BeApiSchema();
            apiSchema.setType(value.getStr("type"));

            JSONObject properties = value.getJSONObject("properties");

            if (properties != null) {

                HashMap<String, BeApi.BeApiField> fieldMap = MapUtil.newHashMap();
                apiSchema.setFieldMap(fieldMap);

                for (Map.Entry<String, Object> subItem : properties.entrySet()) {

                    JSONObject propertiesValue = (JSONObject)item.getValue();

                    String refStr = propertiesValue.getStr("$ref");

                    if (StrUtil.isBlank(refStr)) { // 为空则表示是：一般类型

                        BeApi.BeApiParameter beApiParameter = new BeApi.BeApiParameter();
                        beApiParameter.setName(subItem.getKey());
                        beApiParameter.setType(propertiesValue.getStr("type"));
                        beApiParameter.setFormat(propertiesValue.getStr("format"));
                        beApiParameter.setRequired(propertiesValue.getBool("required"));
                        beApiParameter.setPattern(propertiesValue.getStr("pattern"));
                        beApiParameter.setDescription(propertiesValue.getStr("description"));

                        fieldMap.put(subItem.getKey(), beApiParameter);

                    } else { // 否则是：对象类型

                        BeApi.BeApiSchema beApiSchema = new BeApi.BeApiSchema();
                        beApiSchema.setName(subItem.getKey());

                        Map<String, String> match = BE_API_SCHEMA_MAP_KEY_STR_MATCHER.match(refStr);

                        String beApiSchemaMapKey = match.get(BE_API_SCHEMA_MAP_KEY);

                        BeApi.BeApiSchema propertiesBeApiSchema =
                            beApiSchemaMap.get(beApiSchemaMapKey); // 看这个对象是否存在于 map中

                        if (propertiesBeApiSchema == null) { // 如果不存在

                            todoHandleBeApiSchemaList.add(beApiSchema); // 添加到集合里，稍后会重新赋值

                        }

                        HashMap<String, BeApi.BeApiField> propertiesFieldMap = MapUtil.newHashMap();
                        beApiSchema.setFieldMap(propertiesFieldMap);

                        propertiesFieldMap.put(beApiSchemaMapKey, propertiesBeApiSchema); // 添加到对象类型的，字段 map里

                        fieldMap.put(subItem.getKey(), beApiSchema);

                    }

                }

            }

            apiSchema.setRequired(value.getBool("required"));
            apiSchema.setDescription(value.getStr("description"));
            apiSchema.setRequiredFieldName(value.getBeanList("required", String.class));

            beApiSchemaMap.put(item.getKey(), apiSchema); // 添加到所有的对象里

        }

    }

}
