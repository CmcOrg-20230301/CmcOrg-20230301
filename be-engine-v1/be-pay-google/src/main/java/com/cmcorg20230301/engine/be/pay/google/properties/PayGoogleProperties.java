package com.cmcorg20230301.engine.be.pay.google.properties;

import com.cmcorg20230301.engine.be.model.model.constant.PropertiesPrefixConstant;
import com.cmcorg20230301.engine.be.model.properties.SysPayBaseProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@EqualsAndHashCode(callSuper = true)
@Data
@Component
@ConfigurationProperties(prefix = PropertiesPrefixConstant.PAY_GOOGLE)
public class PayGoogleProperties extends SysPayBaseProperties {

}
