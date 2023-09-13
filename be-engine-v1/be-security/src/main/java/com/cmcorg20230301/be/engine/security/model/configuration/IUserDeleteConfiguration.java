package com.cmcorg20230301.be.engine.security.model.configuration;

import java.util.Set;

public interface IUserDeleteConfiguration {

    /**
     * 移除：用户相关的数据，备注：基础绑定信息不用移除，因为会在移除用户的时候移除
     */
    void handle(final Set<Long> userIdSet);

}
