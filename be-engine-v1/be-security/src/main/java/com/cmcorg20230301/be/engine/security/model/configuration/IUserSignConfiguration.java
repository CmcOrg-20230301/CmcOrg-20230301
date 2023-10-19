package com.cmcorg20230301.be.engine.security.model.configuration;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * 用户注册，注销时，额外的操作
 */
public interface IUserSignConfiguration {

    /**
     * 用户新增时，额外的操作
     * 用于：添加额外的用户数据
     *
     * @return 可以返回 null
     */
    Object signUp(@NotNull Long userId, @NotNull Long tenantId);

    /**
     * 用户注销时，额外的操作
     * 用于：移除：用户相关的数据，备注：基础绑定信息不用移除，因为会在移除用户的时候移除
     */
    void delete(final Set<Long> userIdSet);

}
