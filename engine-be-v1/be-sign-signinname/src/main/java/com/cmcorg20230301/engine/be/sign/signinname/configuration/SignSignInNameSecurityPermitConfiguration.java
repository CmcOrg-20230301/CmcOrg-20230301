package com.cmcorg20230301.engine.be.sign.signinname.configuration;

import com.cmcorg20230301.engine.be.sign.helper.configuration.AbstractSignHelperSecurityPermitConfiguration;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SignSignInNameSecurityPermitConfiguration extends AbstractSignHelperSecurityPermitConfiguration {

    @Override
    protected String getSignPreUri() {
        return "signInName";
    }

}
