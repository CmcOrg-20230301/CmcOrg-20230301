package com.cmcorg20230301.be.engine.pay.google.util;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.cmcorg20230301.be.engine.cache.util.CacheRedisKafkaLocalUtil;
import com.cmcorg20230301.be.engine.cache.util.MyCacheUtil;
import com.cmcorg20230301.be.engine.pay.base.model.bo.SysPayReturnBO;
import com.cmcorg20230301.be.engine.pay.base.model.bo.SysPayTradeNotifyBO;
import com.cmcorg20230301.be.engine.pay.base.model.dto.PayDTO;
import com.cmcorg20230301.be.engine.pay.base.model.entity.SysPayConfigurationDO;
import com.cmcorg20230301.be.engine.pay.base.model.entity.SysPayDO;
import com.cmcorg20230301.be.engine.pay.base.model.enums.SysPayTradeStatusEnum;
import com.cmcorg20230301.be.engine.pay.base.model.enums.SysPayTypeEnum;
import com.cmcorg20230301.be.engine.pay.base.service.SysPayService;
import com.cmcorg20230301.be.engine.pay.base.util.PayHelper;
import com.cmcorg20230301.be.engine.pay.google.model.bo.SysPayGooglePurchasesBO;
import com.cmcorg20230301.be.engine.redisson.model.enums.BaseRedisKeyEnum;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 支付：谷歌工具类
 */
@Component
public class PayGoogleUtil {

    private static SysPayService sysPayService;

    @Resource
    public void setSysPayService(SysPayService sysPayService) {
        PayGoogleUtil.sysPayService = sysPayService;
    }

    /**
     * 支付
     */
    @SneakyThrows
    @NotNull
    public static SysPayReturnBO pay(PayDTO dto) {

        SysPayConfigurationDO sysPayConfigurationDO;

        if (dto.getSysPayConfigurationDoTemp() == null) {

            sysPayConfigurationDO = PayHelper
                .getSysPayConfigurationDO(dto.getTenantId(), SysPayTypeEnum.GOOGLE.getCode(),
                    dto.getUseParentTenantPayFlag());

        } else {

            sysPayConfigurationDO = dto.getSysPayConfigurationDoTemp();

        }

        dto.setSysPayConfigurationDoTemp(sysPayConfigurationDO, false);

        return new SysPayReturnBO(sysPayConfigurationDO.getId().toString(), sysPayConfigurationDO.getAppId());

    }

    /**
     * 查询订单状态
     *
     * @param outTradeNo 本系统的支付主键 id，必填
     */
    @SneakyThrows
    @NotNull
    public static SysPayTradeStatusEnum query(String outTradeNo, @Nullable SysPayTradeNotifyBO sysPayTradeNotifyBO,
        SysPayConfigurationDO sysPayConfigurationDoTemp) {

        Assert.notBlank(outTradeNo);

        SysPayDO sysPayDO = sysPayService.lambdaQuery().eq(SysPayDO::getId, outTradeNo)
            .select(SysPayDO::getPackageName, SysPayDO::getProductId, SysPayDO::getToken, SysPayDO::getOriginalPrice)
            .one();

        if (sysPayDO == null) {
            ApiResultVO.error("谷歌支付查询失败：本系统不存在该支付", outTradeNo);
        }

        // 获取：accessToken
        String accessToken = getAccessToken(sysPayConfigurationDoTemp);

        // 查询：谷歌那边的订单状态，文档地址：https://developers.google.com/android-publisher/api-ref/rest/v3/purchases.products/get?hl=zh-cn
        // https://androidpublisher.googleapis.com/androidpublisher/v3/applications/{packageName}/purchases/products/{productId}/tokens/{token}
        String url = StrUtil.format(
            "https://androidpublisher.googleapis.com/androidpublisher/v3/applications/{}/purchases/products/{}/tokens/{}",
            sysPayDO.getPackageName(), sysPayDO.getProductId(), sysPayDO.getToken());

        String body = HttpRequest.get(url).header("Authorization", "Bearer " + accessToken).execute().body();

        SysPayGooglePurchasesBO sysPayGooglePurchasesBO = JSONUtil.toBean(body, SysPayGooglePurchasesBO.class);

        String orderId = sysPayGooglePurchasesBO.getOrderId();

        if (StrUtil.isBlank(orderId)) {
            ApiResultVO.error("谷歌支付查询失败：订单不存在", outTradeNo);
        }

        if (sysPayTradeNotifyBO != null) {

            sysPayTradeNotifyBO.setTradeNo(orderId);
            sysPayTradeNotifyBO.setTotalAmount(sysPayDO.getOriginalPrice().toPlainString()); // 备注：官方暂时没有返回实际支付金额的字段
            sysPayTradeNotifyBO.setPayCurrency("");

        }

        if (sysPayGooglePurchasesBO.getConsumptionState() != null
            && sysPayGooglePurchasesBO.getConsumptionState() == 1) {

            return SysPayTradeStatusEnum.TRADE_FINISHED;

        }

        if (sysPayGooglePurchasesBO.getPurchaseState() != null && sysPayGooglePurchasesBO.getPurchaseState() == 0) {

            return SysPayTradeStatusEnum.WAIT_BUYER_CONSUME;

        }

        return SysPayTradeStatusEnum.WAIT_BUYER_PAY;

    }

    /**
     * 获取：google接口调用凭据
     */
    public static String getAccessToken(SysPayConfigurationDO sysPayConfigurationDO) {

        long sysPayConfigurationId = sysPayConfigurationDO.getId();

        String sufKey = sysPayConfigurationDO.getTenantId() + ":" + sysPayConfigurationId;

        String accessToken = MyCacheUtil.onlyGet(BaseRedisKeyEnum.GOOGLE_ACCESS_TOKEN_CACHE, sufKey);

        if (StrUtil.isNotBlank(accessToken)) {
            return accessToken;
        }

        JSONObject formJson = JSONUtil.createObj();

        formJson.set("grant_type", "refresh_token");
        formJson.set("client_id", sysPayConfigurationDO.getAppId());
        formJson.set("client_secret", sysPayConfigurationDO.getPrivateKey());
        formJson.set("refresh_token", sysPayConfigurationDO.getPlatformPublicKey());

        String jsonStr = HttpUtil.post("https://accounts.google.com/o/oauth2/token", formJson);

        JSONObject jsonObject = JSONUtil.parseObj(jsonStr);

        String accessTokenResult = jsonObject.getStr("access_token");

        if (StrUtil.isBlank(accessTokenResult)) {

            ApiResultVO.error("谷歌：获取【access_token】失败，请联系管理员", jsonStr);

        }

        Integer expiresIn = jsonObject.getInt("expires_in"); // 这里的单位是：秒

        CacheRedisKafkaLocalUtil
            .put(BaseRedisKeyEnum.GOOGLE_ACCESS_TOKEN_CACHE, sufKey, null, expiresIn * 1000, () -> accessTokenResult);

        return accessTokenResult;

    }

}
