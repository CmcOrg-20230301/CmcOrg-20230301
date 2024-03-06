package com.cmcorg20230301.be.engine.sign.helper.configuration;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.cmcorg20230301.be.engine.model.model.configuration.ISecurityPermitConfiguration;

import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractSignHelperSecurityPermitConfiguration implements
    ISecurityPermitConfiguration {

    private static final String BASE_PRE_URI = "/sign/";

    private static final Set<String> URI_TEMP_SET =
        CollUtil.newHashSet("/sign/up/**", "/sign/in/**", "/forgetPassword/**");

    protected abstract String getSignPreUri();

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

        if (StrUtil.isBlank(getSignPreUri())) {
            return null;
        }

        return URI_TEMP_SET.stream().map(it -> BASE_PRE_URI + getSignPreUri() + it)
            .collect(Collectors.toSet());

    }

}
