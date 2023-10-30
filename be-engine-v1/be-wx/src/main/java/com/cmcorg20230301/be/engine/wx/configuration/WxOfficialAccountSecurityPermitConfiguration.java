package com.cmcorg20230301.be.engine.wx.configuration;

import cn.hutool.core.collection.CollUtil;
import com.cmcorg20230301.be.engine.model.model.configuration.ISecurityPermitConfiguration;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

/**
 * 微信公众号相关的配置
 */
@Configuration
public class WxOfficialAccountSecurityPermitConfiguration implements ISecurityPermitConfiguration {

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
        return CollUtil.newHashSet("/sys/wx/officialAccount/**");
    }

}
