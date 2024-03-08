package com.cmcorg20230301.be.engine.sign.wx.work.configuration;

import org.springframework.context.annotation.Configuration;

import com.cmcorg20230301.be.engine.sign.helper.configuration.AbstractSignHelperSecurityPermitConfiguration;

@Configuration
public class SignWxWorkSecurityPermitConfiguration extends AbstractSignHelperSecurityPermitConfiguration {

    @Override
    protected String getSignPreUri() {
        return "wxWork";
    }

}
