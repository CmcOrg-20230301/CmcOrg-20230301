package com.cmcorg20230301.engine.be.sign.wx.configuration;

import com.cmcorg20230301.engine.be.sign.helper.configuration.AbstractSignHelperSecurityPermitAllConfiguration;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SignWxSecurityPermitAllConfiguration extends AbstractSignHelperSecurityPermitAllConfiguration {

    public final static int SIGN_LEVEL = 0;

    @Override
    protected String getSignPreUri() {
        return "wx";
    }

    @Override
    public int getSignLevel() {
        return SIGN_LEVEL;
    }

}
