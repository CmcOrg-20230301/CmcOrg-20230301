package com.cmcorg20230301.be.engine.other.app.baidu.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
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
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoIdFather;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j(topic = LogTopicConstant.BAI_DU)
@Component
public class BaiDuUtil {

    private static SysOtherAppService sysOtherAppService;

    public BaiDuUtil(SysOtherAppService sysOtherAppService) {

        BaiDuUtil.sysOtherAppService = sysOtherAppService;

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

        log.info("advancedGeneral-result：{}", JSONUtil.toJsonStr(result));

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

            List<SysOtherAppDO> sysOtherAppDOList =
                sysOtherAppService.lambdaQuery().eq(BaseEntityNoIdFather::getTenantId, tenantId)
                    .select(SysOtherAppDO::getAppId).eq(SysOtherAppDO::getType, SysOtherAppTypeEnum.BAI_DU)
                    .eq(BaseEntityNoId::getEnableFlag, true).list();

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

        SysOtherAppDO sysOtherAppDO = sysOtherAppService.lambdaQuery().eq(BaseEntityNoIdFather::getTenantId, tenantId)
            .eq(SysOtherAppDO::getAppId, appId).select(SysOtherAppDO::getSecret)
            .eq(SysOtherAppDO::getType, SysOtherAppTypeEnum.BAI_DU).eq(BaseEntityNoId::getEnableFlag, true).one();

        String errorMessageStr = "accessToken";

        if (sysOtherAppDO == null) {
            ApiResultVO.error(BaseBizCodeEnum.ILLEGAL_REQUEST.getMsg(), errorMessageStr);
        }

        String jsonStr = HttpRequest.post(
            "https://aip.baidubce.com/oauth/2.0/token?client_id=" + appId + "&client_secret=" + sysOtherAppDO
                .getSecret() + "&grant_type=client_credentials").execute().body();

        JSONObject jsonObject = JSONUtil.parseObj(jsonStr);

        String accessTokenResult = jsonObject.getStr("access_token");

        if (StrUtil.isBlank(accessTokenResult)) {

            ApiResultVO.error("百度：获取【access_token】失败，请联系管理员", jsonStr);

        }

        Integer expiresIn = jsonObject.getInt("expires_in"); // 这里的单位是：秒

        CacheRedisKafkaLocalUtil
            .put(BaseRedisKeyEnum.GOOGLE_ACCESS_TOKEN_CACHE, sufKey, null, expiresIn * 1000, () -> accessTokenResult);

        return accessTokenResult;

    }

}
