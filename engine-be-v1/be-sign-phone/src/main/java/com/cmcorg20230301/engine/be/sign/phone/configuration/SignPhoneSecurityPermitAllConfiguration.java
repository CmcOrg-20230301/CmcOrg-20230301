package com.cmcorg20230301.engine.be.sign.phone.configuration;

import com.cmcorg20230301.engine.be.sign.helper.configuration.AbstractSignHelperSecurityPermitAllConfiguration;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SignPhoneSecurityPermitAllConfiguration extends AbstractSignHelperSecurityPermitAllConfiguration {

    @Override
    protected String getSignPreUri() {
        return "phone";
    }

}
