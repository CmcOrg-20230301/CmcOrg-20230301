package com.cmcorg20230301.be.engine.security.configuration.security;

import java.util.Set;

import org.springframework.context.annotation.Configuration;

import com.cmcorg20230301.be.engine.model.model.configuration.ISecurityPermitConfiguration;

import cn.hutool.core.collection.CollUtil;

@Configuration
public class LogSecurityPermitConfiguration implements ISecurityPermitConfiguration {

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
        return CollUtil.newHashSet("/sys/log/**");
    }

}