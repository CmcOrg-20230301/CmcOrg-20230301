package com.cmcorg20230301.be.engine.other.app.baidu.util;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import com.cmcorg20230301.be.engine.cache.util.CacheRedisKafkaLocalUtil;
import com.cmcorg20230301.be.engine.cache.util.MyCacheUtil;
import com.cmcorg20230301.be.engine.model.model.constant.BaseConstant;
import com.cmcorg20230301.be.engine.model.model.constant.LogTopicConstant;
import com.cmcorg20230301.be.engine.other.app.model.entity.SysOtherAppDO;
import com.cmcorg20230301.be.engine.other.app.model.enums.SysOtherAppTypeEnum;
import com.cmcorg20230301.be.engine.other.app.service.SysOtherAppService;
import com.cmcorg20230301.be.engine.redisson.model.enums.BaseRedisKeyEnum;
import com.cmcorg20230301.be.engine.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoId;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoIdSuper;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.util.util.MyNumberUtil;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.func.Func1;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = LogTopicConstant.BAI_DU)
@Component
public class BaiDuUtil {

    private static SysOtherAppService sysOtherAppService;

    public BaiDuUtil(SysOtherAppService sysOtherAppService) {

        BaiDuUtil.sysOtherAppService = sysOtherAppService;

    }

    /**
     * 文本翻译-通用版
     */
    public static String texttransDstZh(@Nullable Long tenantId, @Nullable String appId, String q) {

        return texttransDst(tenantId, appId, null, "zh", q);

    }

    /**
     * 文本翻译-通用版
     */
    public static String texttransDstEn(@Nullable Long tenantId, @Nullable String appId, String q) {

        return texttransDst(tenantId, appId, null, "en", q);

    }

    /**
     * 文本翻译-通用版
     */
    public static String texttransDst(@Nullable Long tenantId, @Nullable String appId, @Nullable String from, String to,
        String q) {

        List<JSONObject> jsonObjectList = texttrans(tenantId, appId, from, to, q);

        StrBuilder strBuilder = StrBuilder.create();

        for (JSONObject jsonObject : jsonObjectList) {

            strBuilder.append(jsonObject.getStr("dst"));

        }

        return strBuilder.toString();

    }

    /**
     * 文本翻译-通用版
     *
     * @return dst 译文 src 原文
     */
    public static List<JSONObject> texttrans(@Nullable Long tenantId, @Nullable String appId, @Nullable String from,
        String to, String q) {

        String accessToken = getAccessToken(tenantId, appId);

        if (StrUtil.isBlank(from)) {
            from = "auto";
        }

        JSONObject formJson = JSONUtil.createObj();

        formJson.set("from", from);

        formJson.set("to", to);

        formJson.set("q", q);

        String body = JSONUtil.toJsonStr(formJson);

        log.info("texttrans-formJson：{}", body);

        String resultStr =
            HttpRequest.post("https://aip.baidubce.com/rpc/2.0/mt/texttrans/v1?access_token=" + accessToken).body(body)
                .execute().body();

        log.info("texttrans-result：{}", resultStr);

        JSONObject jsonObject = JSONUtil.parseObj(resultStr);

        String errorMsg = jsonObject.getStr("error_msg");

        if (StrUtil.isNotBlank(errorMsg)) {

            ApiResultVO.errorMsg(errorMsg);

        }

        JSONObject result = jsonObject.getJSONObject("result");

        return result.getBeanList("trans_result", JSONObject.class);

    }

    /**
     * 查询音频转写任务
     *
     * @return 任务 id
     */
    public static JSONObject aasrQuery(@Nullable Long tenantId, @Nullable String appId, String taskId) {

        String accessToken = getAccessToken(tenantId, appId);

        JSONObject formJson = JSONUtil.createObj();

        formJson.set("task_ids", CollUtil.newArrayList(taskId));

        String body = JSONUtil.toJsonStr(formJson);

        log.info("aasrQuery-formJson：{}", body);

        String result = HttpRequest.post("https://aip.baidubce.com/rpc/2.0/aasr/v1/query?access_token=" + accessToken)
            .body(body).execute().body();

        log.info("aasrQuery-result：{}", result);

        return JSONUtil.parseObj(result);

    }

    /**
     * 获取：srt格式的字符串
     * 
     * @return null 表示转写任务未完成
     */
    @SneakyThrows
    public static String getSrtStr(@Nullable Long tenantId, @Nullable String appId, String taskId,
        @Nullable Func1<String, String> voidFunc1) {

        // 查询：任务是否完成
        JSONObject jsonObject = BaiDuUtil.aasrQuery(tenantId, appId, taskId);

        List<JSONObject> tasksInfoList = jsonObject.getBeanList("tasks_info", JSONObject.class);

        boolean anyMatch = tasksInfoList.stream().anyMatch(it -> "Running".equals(it.getStr("task_status")));

        if (anyMatch) {
            return null;
        }

        StrBuilder strBuilder = StrBuilder.create();

        int index = 0;

        int maxLength = 10;

        List<JSONObject> resultList = new ArrayList<>();

        for (JSONObject item : tasksInfoList) {

            if (!"Success".equals(item.getStr("task_status"))) {
                continue;
            }

            JSONObject taskResult = item.getJSONObject("task_result");

            List<JSONObject> detailedResultList = taskResult.getBeanList("detailed_result", JSONObject.class);

            resultList.addAll(detailedResultList);

        }

        for (int i = 0; i < resultList.size(); i++) {

            JSONObject item = resultList.get(i);

            List<String> resList = item.getBeanList("res", String.class);

            Integer beginTime = item.getInt("begin_time");

            Integer endTime;

            if (i == resultList.size() - 1) { // 如果是：最后一个

                endTime = item.getInt("end_time");

            } else {

                // 获取：下一个的起始时间
                endTime = resultList.get(i + 1).getInt("begin_time");

            }

            String text = CollUtil.join(resList, "");

            if (voidFunc1 != null) {

                text = voidFunc1.call(text);

            }

            int length = text.length() / maxLength;

            if (text.length() % maxLength != 0) {

                length = length + 1;

            }

            if (length == 0) {

                // 添加字幕
                index = appendSrt(strBuilder, index, beginTime, endTime, text);

            } else { // 如果字数超了

                int gap = (endTime - beginTime) / length;

                for (int j = 0; j < length; j++) {

                    String subStr = StrUtil.subWithLength(text, j * maxLength, maxLength);

                    int newBeginTime = beginTime + (j * gap);

                    int newEndTime = newBeginTime + gap;

                    // 添加字幕
                    index = appendSrt(strBuilder, index, newBeginTime, newEndTime, subStr);

                }

            }

        }

        return strBuilder.toString();

    }

    /**
     * 添加字幕
     */
    private static int appendSrt(StrBuilder strBuilder, int index, Integer beginTime, Integer endTime, String text) {

        // 添加：序号
        strBuilder.append(++index).append("\n");

        String beginTimeStr = MyNumberUtil.formatMilliseconds(beginTime);

        String endTimeStr = MyNumberUtil.formatMilliseconds(endTime);

        // 添加：时间
        strBuilder.append(beginTimeStr).append(" --> ").append(endTimeStr).append("\n");

        strBuilder.append(text).append("\n\n");

        return index;

    }

    /**
     * 创建音频转写任务
     *
     * @param pid 80001 中文 1737 英文
     * 
     * @return 任务 id
     */
    public static String aasrCreate(@Nullable Long tenantId, @Nullable String appId, String speechUrl,
        @Nullable String format, @Nullable Integer pid, @Nullable Integer rate) {

        String accessToken = getAccessToken(tenantId, appId);

        if (StrUtil.isBlank(format)) {
            format = "mp3";
        }

        if (pid == null) {
            pid = 80001;
        }

        if (rate == null) {
            rate = 16000;
        }

        JSONObject formJson = JSONUtil.createObj();

        formJson.set("speech_url", speechUrl);
        formJson.set("format", format);
        formJson.set("pid", pid);
        formJson.set("rate", rate);

        String body = JSONUtil.toJsonStr(formJson);

        log.info("aasrCreate-formJson：{}", body);

        String result = HttpRequest.post("https://aip.baidubce.com/rpc/2.0/aasr/v1/create?access_token=" + accessToken)
            .body(body).execute().body();

        log.info("aasrCreate-result：{}", result);

        JSONObject jsonObject = JSONUtil.parseObj(result);

        String errorMsg = jsonObject.getStr("error_msg");

        if (StrUtil.isNotBlank(errorMsg)) {

            ApiResultVO.error(errorMsg, speechUrl);

        }

        return jsonObject.getStr("task_id");

    }

    /**
     * 通用物体和场景识别
     *
     * @return 识别的结果
     */
    public static JSONObject advancedGeneral(@Nullable Long tenantId, @Nullable String appId, String imageUrl) {

        String accessToken = getAccessToken(tenantId, appId);

        JSONObject formJson = JSONUtil.createObj();

        formJson.set("url", imageUrl);
        formJson.set("baike_num", 1);

        log.info("advancedGeneral-formJson：{}", JSONUtil.toJsonStr(formJson));

        String result = HttpRequest
            .post("https://aip.baidubce.com/rest/2.0/image-classify/v2/advanced_general?access_token=" + accessToken)
            .form(formJson).execute().body();

        log.info("advancedGeneral-result：{}", result);

        return JSONUtil.parseObj(result);

    }

    /**
     * 获取：百度全局唯一后台接口调用凭据
     */
    @NotNull
    public static String getAccessToken(@Nullable Long tenantId, @Nullable String appId) {

        if (tenantId == null) {
            tenantId = BaseConstant.TOP_TENANT_ID;
        }

        if (StrUtil.isBlank(appId)) {

            List<SysOtherAppDO> sysOtherAppDOList = sysOtherAppService.lambdaQuery()
                .eq(BaseEntityNoIdSuper::getTenantId, tenantId).select(SysOtherAppDO::getAppId)
                .eq(SysOtherAppDO::getType, SysOtherAppTypeEnum.BAI_DU).eq(BaseEntityNoId::getEnableFlag, true).list();

            if (CollUtil.isEmpty(sysOtherAppDOList)) {
                ApiResultVO.error("操作失败：未找到百度相关配置", tenantId);
            }

            // 取第一个，作为本次的 appId
            appId = CollUtil.getFirst(sysOtherAppDOList).getAppId();

        }

        String sufKey = tenantId + ":" + appId;

        String accessToken = MyCacheUtil.onlyGet(BaseRedisKeyEnum.BAI_DU_ACCESS_TOKEN_CACHE, sufKey);

        if (StrUtil.isNotBlank(accessToken)) {
            return accessToken;
        }

        SysOtherAppDO sysOtherAppDO = sysOtherAppService.lambdaQuery().eq(BaseEntityNoIdSuper::getTenantId, tenantId)
            .eq(SysOtherAppDO::getAppId, appId).select(SysOtherAppDO::getSecret)
            .eq(SysOtherAppDO::getType, SysOtherAppTypeEnum.BAI_DU).eq(BaseEntityNoId::getEnableFlag, true).one();

        String errorMessageStr = "accessToken";

        if (sysOtherAppDO == null) {
            ApiResultVO.error(BaseBizCodeEnum.ILLEGAL_REQUEST.getMsg(), errorMessageStr);
        }

        String jsonStr = HttpRequest.post("https://aip.baidubce.com/oauth/2.0/token?client_id=" + appId
            + "&client_secret=" + sysOtherAppDO.getSecret() + "&grant_type=client_credentials").execute().body();

        log.info("getAccessToken，jsonStr：{}", jsonStr);

        JSONObject jsonObject = JSONUtil.parseObj(jsonStr);

        String accessTokenResult = jsonObject.getStr("access_token");

        if (StrUtil.isBlank(accessTokenResult)) {

            ApiResultVO.error("百度：获取【access_token】失败，请联系管理员", jsonStr);

        }

        Long expiresIn = jsonObject.getLong("expires_in"); // 这里的单位是：秒

        CacheRedisKafkaLocalUtil.put(BaseRedisKeyEnum.BAI_DU_ACCESS_TOKEN_CACHE, sufKey, null, expiresIn * 1000,
            () -> accessTokenResult);

        return accessTokenResult;

    }

}
