package com.cmcorg20230301.be.engine.sign.wx.work.configuration;

import com.cmcorg20230301.be.engine.sign.helper.configuration.AbstractSignHelperSecurityPermitConfiguration;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SignWxWorkSecurityPermitConfiguration extends AbstractSignHelperSecurityPermitConfiguration {

    @Override
    protected String getSignPreUri() {
        return "wxWork";
    }

}
