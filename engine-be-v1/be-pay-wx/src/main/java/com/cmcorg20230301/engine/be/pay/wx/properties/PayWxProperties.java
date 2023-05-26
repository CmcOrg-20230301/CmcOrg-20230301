package com.cmcorg20230301.engine.be.pay.wx.properties;

import com.cmcorg20230301.engine.be.model.model.constant.PropertiesPrefixConstant;
import com.cmcorg20230301.engine.be.model.properties.SysPayBaseProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@EqualsAndHashCode(callSuper = true)
@Data
@Component
@ConfigurationProperties(prefix = PropertiesPrefixConstant.PAY_WX)
public class PayWxProperties extends SysPayBaseProperties {

    /**
     * 商户号
     */
    private String merchantId;

    /**
     * 商户证书序列号
     */
    private String merchantSerialNumber;

    /**
     * 商户APIV3密钥
     */
    private String apiV3Key;

}
