package com.cmcorg20230301.engine.be.sign.signinname.configuration;

import com.cmcorg20230301.engine.be.sign.helper.configuration.AbstractSignHelperSecurityPermitAllConfiguration;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SignSignInNameSecurityPermitAllConfiguration extends AbstractSignHelperSecurityPermitAllConfiguration {

    @Override
    protected String getSignPreUri() {
        return "signInName";
    }

}
