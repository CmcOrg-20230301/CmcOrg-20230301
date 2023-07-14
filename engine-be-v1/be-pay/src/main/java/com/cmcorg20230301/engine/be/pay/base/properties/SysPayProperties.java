package com.cmcorg20230301.engine.be.pay.base.properties;

import com.cmcorg20230301.engine.be.model.model.constant.PropertiesPrefixConstant;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = PropertiesPrefixConstant.PAY)
@RefreshScope
public class SysPayProperties {

    /**
     * 基础支付方式：101 支付宝 201 微信 301 云闪付
     */
    private Integer basePayType;

}
