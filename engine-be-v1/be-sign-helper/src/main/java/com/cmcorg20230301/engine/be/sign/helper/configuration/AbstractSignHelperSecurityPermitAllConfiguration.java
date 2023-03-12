package com.cmcorg20230301.engine.be.sign.helper.configuration;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.cmcorg20230301.engine.be.model.model.configuration.ISecurityPermitAllConfiguration;

import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractSignHelperSecurityPermitAllConfiguration implements ISecurityPermitAllConfiguration {

    private static final String BASE_PRE_URI = "/sign/";

    private static final Set<String> URI_TEMP_SET =
        CollUtil.newHashSet("/sign/up/**", "/sign/in/**", "/forgetPassword/**");

    protected abstract String getSignPreUri();

    // 等级：低等级的一些操作，会被禁用，比如注销
    public abstract int getSignLevel();

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

        return URI_TEMP_SET.stream().map(it -> BASE_PRE_URI + getSignPreUri() + it).collect(Collectors.toSet());

    }

}
