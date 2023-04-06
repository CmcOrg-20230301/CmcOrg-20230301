package com.cmcorg20230301.engine.be.knife4j.configuration;

import cn.hutool.core.collection.CollUtil;
import com.cmcorg20230301.engine.be.model.model.configuration.ISecurityPermitAllConfiguration;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Configuration
public class Knife4jSecurityPermitAllConfiguration implements ISecurityPermitAllConfiguration {

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
        return CollUtil.newHashSet("/v3/api-docs/**", "/webjars/**", "/doc.html/**", "/favicon.ico");
    }

}
