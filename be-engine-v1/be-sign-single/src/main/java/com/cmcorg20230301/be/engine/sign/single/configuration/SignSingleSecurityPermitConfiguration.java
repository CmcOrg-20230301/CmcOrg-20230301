package com.cmcorg20230301.be.engine.sign.single.configuration;

import org.springframework.context.annotation.Configuration;

import com.cmcorg20230301.be.engine.sign.helper.configuration.AbstractSignHelperSecurityPermitConfiguration;

@Configuration
public class SignSingleSecurityPermitConfiguration extends AbstractSignHelperSecurityPermitConfiguration {

    @Override
    protected String getSignPreUri() {
        return "single";
    }

}
