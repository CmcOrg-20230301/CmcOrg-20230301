package com.cmcorg20230301.engine.be.generate.util.generate;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.StrMatcher;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.cmcorg20230301.engine.be.generate.model.bo.BeApi;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 获取所有请求
 */
@Slf4j
public class SpringDocUtil {

    // 读取：接口的地址
    private static final String SPRING_DOC_ENDPOINT = "http://43.154.37.130:10001/v3/api-docs/be";
    //    private static final String SPRING_DOC_ENDPOINT = "http://127.0.0.1:10001/v3/api-docs/be";

    private static final String BE_API_SCHEMA_MAP_KEY = "beApiSchemaMapKey";

    private static final StrMatcher BE_API_SCHEMA_MAP_KEY_STR_MATCHER =
        new StrMatcher("#/components/schemas/${" + BE_API_SCHEMA_MAP_KEY + "}");

    public static void main(String[] args) {

        // 执行获取
        HashMap<String, HashMap<String, BeApi>> result = get(SPRING_DOC_ENDPOINT);

        System.out.println(JSONUtil.toJsonStr(result));

    }

    /**
     * 执行获取，大key是分组，小key是 path
     */
    public static HashMap<String, HashMap<String, BeApi>> get(String springDocEndpoint) {

        log.info("开始：获取所有请求");

        String bodyStr = HttpRequest.get(springDocEndpoint).execute().body();

        JSONObject jsonObject = JSONUtil.parseObj(bodyStr);

        JSONObject paths = jsonObject.getJSONObject("paths");

        HashMap<String, HashMap<String, BeApi>> result = MapUtil.newHashMap(); // 本方法的返回值

        JSONObject components = jsonObject.getJSONObject("components");

        JSONObject schemas = components.getJSONObject("schemas");

        HashMap<String, BeApi.BeApiSchema> beApiSchemaMap = MapUtil.newHashMap(schemas.size()); // 所有的对象，key是对象的名称

        List<BeApi.BeApiSchema> todoHandleBeApiSchemaList = new ArrayList<>(); // 待处理的：对象类型集合

        // 处理：所有的对象
        handleBeApiSchemaMap(schemas, beApiSchemaMap, todoHandleBeApiSchemaList);

        // 处理：待处理的：对象类型集合
        handleTodoHandleBeApiSchemaList(beApiSchemaMap, todoHandleBeApiSchemaList);

        // 处理：返回值
        handleResult(paths, result, beApiSchemaMap);

        log.info("完成：获取所有请求，总共：{}个", paths.size());

        return result;

    }

    /**
     * 处理：返回值
     */
    private static void handleResult(JSONObject paths, HashMap<String, HashMap<String, BeApi>> result,
        HashMap<String, BeApi.BeApiSchema> beApiSchemaMap) {

        for (Map.Entry<String, Object> item : paths.entrySet()) {

            JSONObject value = (JSONObject)item.getValue();

            String[] valueKeyArr = value.keySet().toArray(new String[0]);

            String methodStr;

            if (valueKeyArr.length > 1) {
                methodStr = "post"; // @RequestMapping，会有多个，只取 post的那个
            } else {
                methodStr = valueKeyArr[0];
            }

            JSONObject method = value.getJSONObject(methodStr);

            BeApi beApi = new BeApi();
            beApi.setPath(item.getKey());
            beApi.setMethod(methodStr);

            List<String> tagList = method.getBeanList("tags", String.class);

            String tag = "未分组";

            if (CollUtil.isNotEmpty(tagList)) {
                tag = tagList.get(0);
            }

            beApi.setTag(tag); // 设置：分组

            beApi.setSummary(method.getStr("summary"));

            JSONObject requestBody = method.getJSONObject("requestBody");

            if (requestBody != null) {

                // 处理：requestBody
                handleResultRequestBody(beApiSchemaMap, item, beApi, requestBody);

            }

            JSONArray parameters = method.getJSONArray("parameters");

            if (parameters != null) {

                // 处理：parameters
                handleResultParameters(beApiSchemaMap, item, beApi, parameters);

            }

            JSONObject responses = method.getJSONObject("responses");

            if (responses != null) {

                // 处理：responses
                handleResultResponses(beApiSchemaMap, item, beApi, responses);

            }

            String group = getGroup(beApi); // 获取：group

            beApi.setGroup(group);

            HashMap<String, BeApi> secondMap = result.computeIfAbsent(group, k -> new HashMap<>());

            secondMap.put(item.getKey(), beApi); // 添加到返回值里

        }

    }

    /**
     * 获取：group
     */
    @Nullable
    private static String getGroup(BeApi beApi) {

        List<String> splitList = StrUtil.splitTrim(beApi.getPath(), "/");

        String group;

        if (splitList.size() <= 2) {

            group = StrUtil.upperFirst(splitList.get(0));

        } else {

            group = StrUtil.upperFirst(splitList.get(0)) + StrUtil.upperFirst(splitList.get(1));

        }

        return group;

    }

    /**
     * 处理：responses
     */
    private static void handleResultResponses(HashMap<String, BeApi.BeApiSchema> beApiSchemaMap,
        Map.Entry<String, Object> item, BeApi beApi, JSONObject responses) {

        JSONObject jsonObject200 = responses.getJSONObject("200");

        if (jsonObject200 == null) {

            log.info("处理返回值失败：key：{}，", item.getKey());
            return;

        }

        JSONObject content = jsonObject200.getJSONObject("content");

        if (content == null) {

            log.info("没有返回值：key：{}，", item.getKey());
            return; // 没有返回值

        }

        JSONObject jsonObject = content.getJSONObject("*/*");

        JSONObject schema = jsonObject.getJSONObject("schema");

        String refStr = schema.getStr("$ref");

        BeApi.BeApiSchema beApiSchema = new BeApi.BeApiSchema();

        Map<String, String> match = BE_API_SCHEMA_MAP_KEY_STR_MATCHER.match(refStr);

        String beApiSchemaMapKey = match.get(BE_API_SCHEMA_MAP_KEY);

        beApiSchema.setClassName(beApiSchemaMapKey);

        BeApi.BeApiSchema propertiesBeApiSchema = beApiSchemaMap.get(beApiSchemaMapKey); // 从 map中获取对象

        if (propertiesBeApiSchema == null) { // 如果不存在

            log.info("处理返回值失败：未找到引用类：key：{}，beApiSchemaMapKey；{}", item.getKey(), beApiSchemaMapKey);
            return;

        }

        HashMap<String, BeApi.BeApiField> propertiesFieldMap = MapUtil.newHashMap();
        beApiSchema.setFieldMap(propertiesFieldMap);

        propertiesFieldMap.put(beApiSchemaMapKey, propertiesBeApiSchema); // 添加到对象类型的，字段 map里

        beApi.setResponse(beApiSchema); // 设置：返回值

    }

    /**
     * 处理：parameters
     */
    private static void handleResultParameters(HashMap<String, BeApi.BeApiSchema> beApiSchemaMap,
        Map.Entry<String, Object> item, BeApi beApi, JSONArray parameters) {

        Map<String, BeApi.BeApiField> parameterMap = MapUtil.newHashMap();
        beApi.setParameter(parameterMap);

        for (Object subItem : parameters) {

            JSONObject parameter = (JSONObject)subItem;

            JSONObject schema = parameter.getJSONObject("schema");

            String name = parameter.getStr("name");

            if (schema == null) { // 如果是：一般类型

                // 设置：一般类型到 map里
                handleResultParametersParameter(parameterMap, parameter, name);

            } else { // 如果是：对象类型

                String refStr = schema.getStr("$ref");

                if (StrUtil.isBlank(refStr)) {

                    // 组装键值对到：parameter里
                    parameter.putAll(schema);

                    // 设置：一般类型到 map里
                    handleResultParametersParameter(parameterMap, parameter, name);

                } else { // 如果是：对象类型

                    BeApi.BeApiSchema beApiSchema = new BeApi.BeApiSchema();
                    beApiSchema.setName(name);

                    Map<String, String> match = BE_API_SCHEMA_MAP_KEY_STR_MATCHER.match(refStr);

                    String beApiSchemaMapKey = match.get(BE_API_SCHEMA_MAP_KEY);

                    beApiSchema.setClassName(beApiSchemaMapKey);

                    BeApi.BeApiSchema propertiesBeApiSchema = beApiSchemaMap.get(beApiSchemaMapKey); // 从 map中获取对象

                    if (propertiesBeApiSchema == null) { // 如果不存在

                        log.info("未找到引用类：key：{}，beApiSchemaMapKey；{}", item.getKey(), beApiSchemaMapKey);
                        continue;

                    }

                    HashMap<String, BeApi.BeApiField> propertiesFieldMap = MapUtil.newHashMap();
                    beApiSchema.setFieldMap(propertiesFieldMap);

                    propertiesFieldMap.put(beApiSchemaMapKey, propertiesBeApiSchema); // 添加到对象类型的，字段 map里

                    parameterMap.put(beApiSchema.getName(), beApiSchema);

                }

            }

        }

    }

    /**
     * 设置：一般类型到 map里
     */
    private static void handleResultParametersParameter(Map<String, BeApi.BeApiField> parameterMap,
        JSONObject parameter, String name) {

        BeApi.BeApiParameter beApiParameter = new BeApi.BeApiParameter();
        beApiParameter.setName(name);

        // 处理：beApiParameter的属性
        handleBeApiParameter(parameter, beApiParameter);

        parameterMap.put(beApiParameter.getName(), beApiParameter); // 设置：一般类型到 map里

    }

    /**
     * 处理：beApiParameter的属性
     */
    private static void handleBeApiParameter(JSONObject jsonObject, BeApi.BeApiParameter beApiParameter) {

        beApiParameter.setType(jsonObject.getStr("type"));
        beApiParameter.setRequired(jsonObject.getBool("required"));
        beApiParameter.setDescription(jsonObject.getStr("description"));
        beApiParameter.setFormat(jsonObject.getStr("format"));
        beApiParameter.setPattern(jsonObject.getStr("pattern"));
        beApiParameter.setMaxLength(jsonObject.getInt("maxLength"));
        beApiParameter.setMinLength(jsonObject.getInt("minLength"));

    }

    /**
     * 处理：requestBody
     */
    private static void handleResultRequestBody(HashMap<String, BeApi.BeApiSchema> beApiSchemaMap,
        Map.Entry<String, Object> item, BeApi beApi, JSONObject requestBody) {

        JSONObject content = requestBody.getJSONObject("content");

        String[] contentKeyArr = content.keySet().toArray(new String[0]);

        String contentTypeStr = contentKeyArr[0];

        beApi.setContentType(contentTypeStr);

        JSONObject contentType = content.getJSONObject(contentTypeStr);

        JSONObject schema = contentType.getJSONObject("schema");

        String refStr = schema.getStr("$ref");

        if (StrUtil.isBlank(refStr)) {

            log.info("暂时不支持：key：{}，", item.getKey());
            return;

        }

        Map<String, String> match = BE_API_SCHEMA_MAP_KEY_STR_MATCHER.match(refStr);

        String beApiSchemaMapKey = match.get(BE_API_SCHEMA_MAP_KEY);

        BeApi.BeApiSchema beApiSchema = beApiSchemaMap.get(beApiSchemaMapKey); // 从 map中获取对象

        if (beApiSchema == null) { // 如果不存在

            log.info("获取失败：key：{}，beApiSchemaMapKey：{}", item.getKey(), beApiSchemaMapKey);

        } else {

            beApi.setRequestBody(beApiSchema);

        }

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
            apiSchema.setClassName(item.getKey());
            apiSchema.setType(value.getStr("type"));

            apiSchema.setRequiredFieldName(value.getBeanList("required", String.class));

            JSONObject properties = value.getJSONObject("properties");

            if (properties != null) {

                // 处理：所有的对象，处理：字段
                handleBeApiSchemaMapProperties(beApiSchemaMap, todoHandleBeApiSchemaList, apiSchema, properties);

            }

            apiSchema.setRequired(value.getBool("required"));
            apiSchema.setDescription(value.getStr("description"));

            beApiSchemaMap.put(item.getKey(), apiSchema); // 添加到所有的对象里

        }

    }

    /**
     * 处理：所有的对象，处理：字段
     */
    private static void handleBeApiSchemaMapProperties(HashMap<String, BeApi.BeApiSchema> beApiSchemaMap,
        List<BeApi.BeApiSchema> todoHandleBeApiSchemaList, BeApi.BeApiSchema apiSchema, JSONObject properties) {

        HashMap<String, BeApi.BeApiField> fieldMap = MapUtil.newHashMap();
        apiSchema.setFieldMap(fieldMap);

        for (Map.Entry<String, Object> item : properties.entrySet()) {

            JSONObject propertiesValue = (JSONObject)item.getValue();

            String refStr = propertiesValue.getStr("$ref");

            Boolean arrFlag = null;

            if (StrUtil.isBlank(refStr)) {

                JSONObject items = propertiesValue.getJSONObject("items"); // 如果是：数组

                if (items != null) {

                    arrFlag = true;

                    refStr = items.getStr("$ref");

                    if (StrUtil.isBlank(refStr)) {
                        propertiesValue = items; // 替换为：数组里面元素的类型
                    }

                }

            }

            if (StrUtil.isBlank(refStr)) { // 为空则表示是：一般类型

                BeApi.BeApiParameter beApiParameter = new BeApi.BeApiParameter();
                beApiParameter.setName(item.getKey());

                // 处理：beApiParameter的属性
                handleBeApiParameter(propertiesValue, beApiParameter);

                beApiParameter.setArrFlag(arrFlag);

                fieldMap.put(item.getKey(), beApiParameter);

                if (CollUtil.isNotEmpty(apiSchema.getRequiredFieldName()) && apiSchema.getRequiredFieldName()
                    .contains(item.getKey())) {

                    beApiParameter.setRequired(true);

                }

            } else { // 否则是：对象类型

                BeApi.BeApiSchema beApiSchema = new BeApi.BeApiSchema();
                beApiSchema.setName(item.getKey());

                Map<String, String> match = BE_API_SCHEMA_MAP_KEY_STR_MATCHER.match(refStr);

                String beApiSchemaMapKey = match.get(BE_API_SCHEMA_MAP_KEY);

                beApiSchema.setClassName(beApiSchemaMapKey);
                beApiSchema.setArrFlag(arrFlag);
                beApiSchema.setDescription(propertiesValue.getStr("description"));

                if (apiSchema.getClassName().equals(beApiSchemaMapKey)) { // 这里防止递归，然后内存溢出

                    fieldMap.put(item.getKey(), beApiSchema);
                    continue;

                }

                BeApi.BeApiSchema propertiesBeApiSchema = beApiSchemaMap.get(beApiSchemaMapKey); // 看这个对象是否存在于 map中

                if (propertiesBeApiSchema == null) { // 如果不存在

                    todoHandleBeApiSchemaList.add(beApiSchema); // 添加到集合里，稍后会重新赋值

                }

                HashMap<String, BeApi.BeApiField> propertiesFieldMap = MapUtil.newHashMap();
                beApiSchema.setFieldMap(propertiesFieldMap);

                propertiesFieldMap.put(beApiSchemaMapKey, propertiesBeApiSchema); // 添加到对象类型的，字段 map里

                fieldMap.put(item.getKey(), beApiSchema);

                if (CollUtil.isNotEmpty(apiSchema.getRequiredFieldName()) && apiSchema.getRequiredFieldName()
                    .contains(item.getKey())) {

                    beApiSchema.setRequired(true);

                }

            }

        }

    }

}
