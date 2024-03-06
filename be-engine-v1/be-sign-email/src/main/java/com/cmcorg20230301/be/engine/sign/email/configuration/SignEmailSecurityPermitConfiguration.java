package com.cmcorg20230301.be.engine.sign.email.configuration;

import com.cmcorg20230301.be.engine.sign.helper.configuration.AbstractSignHelperSecurityPermitConfiguration;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SignEmailSecurityPermitConfiguration extends
    AbstractSignHelperSecurityPermitConfiguration {

    @Override
    protected String getSignPreUri() {
        return "email";
    }

}
