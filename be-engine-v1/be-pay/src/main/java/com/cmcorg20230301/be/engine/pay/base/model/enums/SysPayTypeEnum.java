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

    ALI(101, dto -> {

        Assert.notBlank(dto.getPlatformPublicKey());
        Assert.notBlank(dto.getNotifyUrl());

    }), // 支付宝

    WX_NATIVE(201, getWxCheckSysPayConfigurationInsertOrUpdateDtoConsumer()), // 微信-native

    WX_JSAPI(202, getWxCheckSysPayConfigurationInsertOrUpdateDtoConsumer()), // 微信-JsApi

    UNION(301, dto -> {

        Assert.notBlank(dto.getPlatformPublicKey());
        Assert.notBlank(dto.getNotifyUrl());

    }), // 云闪付

    GOOGLE(401, dto -> {

        Assert.notBlank(dto.getPlatformPublicKey());

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

            Assert.notBlank(dto.getPlatformPublicKey());
            Assert.notBlank(dto.getNotifyUrl());
            Assert.notBlank(dto.getMerchantId());
            Assert.notBlank(dto.getMerchantSerialNumber());
            Assert.notBlank(dto.getApiV3Key());

        };

    }

}
