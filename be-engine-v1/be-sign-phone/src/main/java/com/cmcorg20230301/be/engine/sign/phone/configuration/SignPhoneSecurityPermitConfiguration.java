package com.cmcorg20230301.be.engine.sign.phone.configuration;

import com.cmcorg20230301.be.engine.sign.helper.configuration.AbstractSignHelperSecurityPermitConfiguration;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SignPhoneSecurityPermitConfiguration extends
    AbstractSignHelperSecurityPermitConfiguration {

    @Override
    protected String getSignPreUri() {
        return "phone";
    }

}
