package com.cmcorg20230301.be.engine.pay.base.model.enums;

import java.util.function.Consumer;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.cmcorg20230301.be.engine.pay.base.model.dto.SysPayConfigurationInsertOrUpdateDTO;
import com.cmcorg20230301.be.engine.pay.base.model.interfaces.ISysPayType;
import com.fasterxml.jackson.annotation.JsonValue;

import cn.hutool.core.lang.Assert;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 支付方式类型：枚举类
 */
@AllArgsConstructor
@Getter
public enum SysPayTypeEnum implements ISysPayType {

    ALI_QR_CODE(101, getAliCheckSysPayConfigurationInsertOrUpdateDtoConsumer()), // 支付宝-扫码付款

    ALI_APP(102, getAliCheckSysPayConfigurationInsertOrUpdateDtoConsumer()), // 支付宝-手机支付

    ALI_WEB_PC(103, getAliCheckSysPayConfigurationInsertOrUpdateDtoConsumer()), // 支付宝-电脑网站支付

    ALI_WEB_APP(104, getAliCheckSysPayConfigurationInsertOrUpdateDtoConsumer()), // 支付宝-手机网站支付

    WX_NATIVE(201, getWxCheckSysPayConfigurationInsertOrUpdateDtoConsumer()), // 微信-native，备注：就是扫码付款

    WX_JSAPI(202, getWxCheckSysPayConfigurationInsertOrUpdateDtoConsumer()), // 微信-jsApi：微信内嵌网页，调用支付

    WX_H5(203, getWxCheckSysPayConfigurationInsertOrUpdateDtoConsumer()), // 微信-h5：网页跳转，调用支付

    UNION(301, getUnionCheckSysPayConfigurationInsertOrUpdateDtoConsumer()), // 云闪付

    GOOGLE(401, dto -> {

        Assert.notBlank(dto.getAppId(), "应用id不能为空");
        Assert.notBlank(dto.getPrivateKey(), "私钥不能为空");
        Assert.notBlank(dto.getPlatformPublicKey(), "平台公钥不能为空");

    }), // 谷歌

    APPLY(501, null), // 苹果

    ;

    @EnumValue
    @JsonValue
    private final int code; // 类型编码

    // 检查：SysPayConfigurationInsertOrUpdateDTO
    private final Consumer<SysPayConfigurationInsertOrUpdateDTO> checkSysPayConfigurationInsertOrUpdateDtoConsumer;

    /**
     * 获取：云闪付的，检查 SysPayConfigurationInsertOrUpdateDTO对象的 Consumer
     */
    private static Consumer<SysPayConfigurationInsertOrUpdateDTO>
        getUnionCheckSysPayConfigurationInsertOrUpdateDtoConsumer() {

        return dto -> {

            Assert.notBlank(dto.getAppId(), "应用id不能为空");
            Assert.notBlank(dto.getPrivateKey(), "私钥不能为空");
            Assert.notBlank(dto.getPlatformPublicKey(), "平台公钥不能为空");
            Assert.notBlank(dto.getNotifyUrl(), "异步接收地址不能为空");

        };

    }

    /**
     * 获取：支付宝的，检查 SysPayConfigurationInsertOrUpdateDTO对象的 Consumer
     */
    private static Consumer<SysPayConfigurationInsertOrUpdateDTO>
        getAliCheckSysPayConfigurationInsertOrUpdateDtoConsumer() {

        return dto -> {

            Assert.notBlank(dto.getAppId(), "应用id不能为空");
            Assert.notBlank(dto.getPrivateKey(), "私钥不能为空");
            Assert.notBlank(dto.getServerUrl(), "网关地址不能为空");
            Assert.notBlank(dto.getPlatformPublicKey(), "平台公钥不能为空");
            Assert.notBlank(dto.getNotifyUrl(), "异步接收地址不能为空");

        };

    }

    /**
     * 获取：微信的，检查 SysPayConfigurationInsertOrUpdateDTO对象的 Consumer
     */
    private static Consumer<SysPayConfigurationInsertOrUpdateDTO>
        getWxCheckSysPayConfigurationInsertOrUpdateDtoConsumer() {

        return dto -> {

            Assert.notBlank(dto.getAppId(), "应用id不能为空");
            Assert.notBlank(dto.getPrivateKey(), "私钥不能为空");
            Assert.notBlank(dto.getNotifyUrl(), "异步接收地址不能为空");
            Assert.notBlank(dto.getMerchantId(), "商户号不能为空");
            Assert.notBlank(dto.getMerchantSerialNumber(), "商户证书序列号不能为空");
            Assert.notBlank(dto.getApiV3Key(), "apiV3Key不能为空");

        };

    }

}
