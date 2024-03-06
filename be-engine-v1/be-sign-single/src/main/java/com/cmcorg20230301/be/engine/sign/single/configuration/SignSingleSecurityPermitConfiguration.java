package com.cmcorg20230301.be.engine.sign.single.configuration;

import com.cmcorg20230301.be.engine.sign.helper.configuration.AbstractSignHelperSecurityPermitConfiguration;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SignSingleSecurityPermitConfiguration extends
    AbstractSignHelperSecurityPermitConfiguration {

    @Override
    protected String getSignPreUri() {
        return "single";
    }

}
