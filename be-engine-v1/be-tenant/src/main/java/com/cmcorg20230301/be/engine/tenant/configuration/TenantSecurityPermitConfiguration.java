package com.cmcorg20230301.be.engine.tenant.configuration;

import cn.hutool.core.collection.CollUtil;
import com.cmcorg20230301.be.engine.model.model.configuration.ISecurityPermitConfiguration;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Configuration
public class TenantSecurityPermitConfiguration implements ISecurityPermitConfiguration {

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
        return CollUtil.newHashSet("/sys/tenant/getNameById");
    }

}
