package com.cmcorg20230301.engine.be.pay.wx.configuration;

import cn.hutool.core.collection.CollUtil;
import com.cmcorg20230301.engine.be.model.model.configuration.ISecurityPermitConfiguration;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Configuration
public class PayWxSecurityPermitConfiguration implements ISecurityPermitConfiguration {

    @Override
    public Set<String> devPermitAllSet() {
        return null;
    }

    @Override
    public Set<String> prodPermitAllSet() {
        return null;
    }

    @Override
    public Set<String> anyPermitAllSet() {
        return CollUtil.newHashSet("/sys/pay/wx/notifyCallBack/**");
    }

}
