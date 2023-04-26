package com.cmcorg20230301.engine.be.sign.email.configuration;

import com.cmcorg20230301.engine.be.sign.helper.configuration.AbstractSignHelperSecurityPermitAllConfiguration;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SignEmailSecurityPermitAllConfiguration extends AbstractSignHelperSecurityPermitAllConfiguration {

    @Override
    protected String getSignPreUri() {
        return "email";
    }

}
