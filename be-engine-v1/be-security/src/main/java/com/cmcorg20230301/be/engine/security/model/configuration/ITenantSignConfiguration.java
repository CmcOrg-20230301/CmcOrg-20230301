package com.cmcorg20230301.be.engine.security.model.configuration;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * 租户：新增或者删除时的额外操作
 */
public interface ITenantSignConfiguration {

    /**
     * 租户新增时，额外的操作
     * 用于：添加额外的租户数据
     */
    void signUp(@NotNull Long tenantId);

    /**
     * 租户注销时，额外的操作
     * 用于：移除：租户相关的数据，备注：菜单表不用移除，因为会在移除租户的时候移除
     */
    void delete(final Set<Long> tenantIdSet);

}
