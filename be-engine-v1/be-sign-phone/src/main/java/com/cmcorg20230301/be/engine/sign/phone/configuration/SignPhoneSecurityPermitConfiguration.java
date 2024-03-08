package com.cmcorg20230301.be.engine.sign.phone.configuration;

import org.springframework.context.annotation.Configuration;

import com.cmcorg20230301.be.engine.sign.helper.configuration.AbstractSignHelperSecurityPermitConfiguration;

@Configuration
public class SignPhoneSecurityPermitConfiguration extends AbstractSignHelperSecurityPermitConfiguration {

    @Override
    protected String getSignPreUri() {
        return "phone";
    }

}
