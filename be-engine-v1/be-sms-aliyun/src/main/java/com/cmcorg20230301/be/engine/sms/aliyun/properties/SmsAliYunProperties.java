package com.cmcorg20230301.be.engine.sms.aliyun.properties;

import com.cmcorg20230301.be.engine.model.model.constant.PropertiesPrefixConstant;
import com.cmcorg20230301.be.engine.model.properties.SysSmsBaseProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@EqualsAndHashCode(callSuper = true)
@Data
@Component
@ConfigurationProperties(prefix = PropertiesPrefixConstant.SMS_ALI_YUN)
@RefreshScope
public class SmsAliYunProperties extends SysSmsBaseProperties {

}
