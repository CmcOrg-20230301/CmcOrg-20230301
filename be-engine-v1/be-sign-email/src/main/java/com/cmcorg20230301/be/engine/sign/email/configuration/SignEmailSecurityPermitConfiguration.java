package com.cmcorg20230301.be.engine.sign.email.configuration;

import org.springframework.context.annotation.Configuration;

import com.cmcorg20230301.be.engine.sign.helper.configuration.AbstractSignHelperSecurityPermitConfiguration;

@Configuration
public class SignEmailSecurityPermitConfiguration extends AbstractSignHelperSecurityPermitConfiguration {

    @Override
    protected String getSignPreUri() {
        return "email";
    }

}
