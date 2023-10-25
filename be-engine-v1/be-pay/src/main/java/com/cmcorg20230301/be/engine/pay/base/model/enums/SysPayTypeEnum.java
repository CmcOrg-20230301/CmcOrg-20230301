package com.cmcorg20230301.be.engine.pay.base.model.enums;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.cmcorg20230301.be.engine.pay.base.model.dto.SysPayConfigurationInsertOrUpdateDTO;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.Consumer;

/**
 * 支付方式类型：枚举类
 */
@AllArgsConstructor
@Getter
public enum SysPayTypeEnum {

    DEFAULT(1, null), // 默认，注意：这个类型不要存储到数据库里，这里是代码调用支付时使用，用于表示使用默认支付

    ALI(101, dto -> {

        Assert.notBlank(dto.getServerUrl(), "网关地址不能为空");
        Assert.notBlank(dto.getPlatformPublicKey(), "平台公钥不能为空");
        Assert.notBlank(dto.getNotifyUrl(), "异步接收地址不能为空");

    }), // 支付宝

    WX_NATIVE(201, getWxCheckSysPayConfigurationInsertOrUpdateDtoConsumer()), // 微信-native

    WX_JSAPI(202, getWxCheckSysPayConfigurationInsertOrUpdateDtoConsumer()), // 微信-jsApi

    UNION(301, dto -> {

        Assert.notBlank(dto.getPlatformPublicKey(), "平台公钥不能为空");
        Assert.notBlank(dto.getNotifyUrl(), "异步接收地址不能为空");

    }), // 云闪付

    GOOGLE(401, dto -> {

        Assert.notBlank(dto.getPlatformPublicKey(), "平台公钥不能为空");

    }), // 谷歌

    ;

    @EnumValue
    @JsonValue
    private final int code; // 类型编码

    // 检查：SysPayConfigurationInsertOrUpdateDTO
    private final Consumer<SysPayConfigurationInsertOrUpdateDTO> checkSysPayConfigurationInsertOrUpdateDTOConsumer;

    /**
     * 获取：微信的，检查 SysPayConfigurationInsertOrUpdateDTO对象的 Consumer
     */
    private static Consumer<SysPayConfigurationInsertOrUpdateDTO> getWxCheckSysPayConfigurationInsertOrUpdateDtoConsumer() {

        return dto -> {

            Assert.notBlank(dto.getNotifyUrl(), "异步接收地址不能为空");
            Assert.notBlank(dto.getMerchantId(), "商户号不能为空");
            Assert.notBlank(dto.getMerchantSerialNumber(), "商户证书序列号不能为空");
            Assert.notBlank(dto.getApiV3Key(), "apiV3Key不能为空");

        };

    }

}
