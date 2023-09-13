package com.cmcorg20230301.be.engine.security.model.configuration;

import java.util.Set;

public interface ITenantDeleteConfiguration {

    /**
     * 移除：租户相关的数据，备注：菜单表不用移除，因为会在移除租户的时候移除
     */
    void handle(final Set<Long> tenantIdSet);

}
