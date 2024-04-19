package com.cmcorg20230301.be.engine.other.app.volcengine.util;

import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import com.cmcorg20230301.be.engine.model.model.constant.BaseConstant;
import com.cmcorg20230301.be.engine.model.model.constant.LogTopicConstant;
import com.cmcorg20230301.be.engine.other.app.model.entity.SysOtherAppDO;
import com.cmcorg20230301.be.engine.other.app.model.enums.SysOtherAppTypeEnum;
import com.cmcorg20230301.be.engine.other.app.service.SysOtherAppService;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoId;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoIdSuper;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.security.util.MyExceptionUtil;
import com.volcengine.model.request.translate.TranslateTextRequest;
import com.volcengine.model.response.translate.TranslateTextResponse;
import com.volcengine.service.translate.ITranslateService;
import com.volcengine.service.translate.impl.TranslateServiceImpl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 火山引擎工具类
 */
@Slf4j(topic = LogTopicConstant.VOLC_ENGINE)
@Component
public class VolcEngineUtil {

    private static SysOtherAppService sysOtherAppService;

    public VolcEngineUtil(SysOtherAppService sysOtherAppService) {

        VolcEngineUtil.sysOtherAppService = sysOtherAppService;

    }

    /**
     * 执行：翻译为英文
     */
    public static String translationEn(@Nullable Long tenantId, @Nullable String appId,
        @Nullable ITranslateService translateService, String q) {

        return translation(tenantId, appId, translateService, q, "en");

    }

    /**
     * 执行：翻译为中文
     */
    public static String translationZh(@Nullable Long tenantId, @Nullable String appId,
        @Nullable ITranslateService translateService, String q) {

        return translation(tenantId, appId, translateService, q, "zh");

    }

    /**
     * 执行翻译
     *
     * @param targetLanguage：en 英文 zh 中文
     */
    public static String translation(@Nullable Long tenantId, @Nullable String appId,
        @Nullable ITranslateService translateService, String q, String targetLanguage) {

        if (translateService == null) {

            // 获取：火山引擎接口调用凭据
            translateService = getTranslateService(tenantId, appId);

        }

        TranslateTextRequest translateTextRequest = new TranslateTextRequest();

        translateTextRequest.setTargetLanguage(targetLanguage);
        translateTextRequest.setTextList(Collections.singletonList(q));

        try {

            TranslateTextResponse translateTextResponse = translateService.translateText(translateTextRequest);

            String result = translateTextResponse.getTranslationList().get(0).getTranslation();

            log.info("火山翻译，源：{}，结果：{}", q, result);

            return result;

        } catch (Exception e) {

            MyExceptionUtil.printError(e);

            return q;

        }

    }

    /**
     * 获取：火山引擎接口调用凭据
     */
    @NotNull
    public static ITranslateService getTranslateService(@Nullable Long tenantId, @Nullable String appId) {

        if (tenantId == null) {
            tenantId = BaseConstant.TOP_TENANT_ID;
        }

        if (StrUtil.isBlank(appId)) {

            List<SysOtherAppDO> sysOtherAppDOList =
                sysOtherAppService.lambdaQuery().eq(BaseEntityNoIdSuper::getTenantId, tenantId)
                    .select(SysOtherAppDO::getAppId).eq(SysOtherAppDO::getType, SysOtherAppTypeEnum.VOLC_ENGINE)
                    .eq(BaseEntityNoId::getEnableFlag, true).list();

            if (CollUtil.isEmpty(sysOtherAppDOList)) {
                ApiResultVO.error("操作失败：未找到火山引擎相关配置", tenantId);
            }

            // 取第一个，作为本次的 appId
            appId = CollUtil.getFirst(sysOtherAppDOList).getAppId();

        }

        SysOtherAppDO sysOtherAppDO = sysOtherAppService.lambdaQuery().eq(BaseEntityNoIdSuper::getTenantId, tenantId)
            .eq(SysOtherAppDO::getAppId, appId).select(SysOtherAppDO::getSecret)
            .eq(SysOtherAppDO::getType, SysOtherAppTypeEnum.VOLC_ENGINE).eq(BaseEntityNoId::getEnableFlag, true).one();

        ITranslateService translateService = TranslateServiceImpl.getInstance();

        translateService.setAccessKey(appId);
        translateService.setSecretKey(sysOtherAppDO.getSecret());

        return translateService;

    }

}
