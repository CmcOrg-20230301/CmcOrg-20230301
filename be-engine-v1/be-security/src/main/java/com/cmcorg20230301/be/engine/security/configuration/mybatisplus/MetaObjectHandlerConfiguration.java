package com.cmcorg20230301.be.engine.security.configuration.mybatisplus;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.cmcorg20230301.be.engine.security.util.UserUtil;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class MetaObjectHandlerConfiguration implements MetaObjectHandler {

    /**
     * 新增时
     */
    @Override
    public void insertFill(MetaObject metaObject) {

        Date date = new Date();

        Long currentUserIdDefault = UserUtil.getCurrentUserIdDefault();

        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        // 实体类有值时，这里不会生效
        strictInsertFill(metaObject, "tenantId", Long.class, currentTenantIdDefault);

        strictInsertFill(metaObject, "createId", Long.class, currentUserIdDefault);
        strictInsertFill(metaObject, "createTime", Date.class, date);

        strictInsertFill(metaObject, "updateId", Long.class, currentUserIdDefault);
        strictInsertFill(metaObject, "updateTime", Date.class, date);

        strictInsertFill(metaObject, "version", Integer.class, 0);

        strictInsertFill(metaObject, "uuid", String.class, IdUtil.simpleUUID());

    }

    /**
     * 修改时
     */
    @Override
    public void updateFill(MetaObject metaObject) {

        Date date = new Date();

        Long currentUserIdDefault = UserUtil.getCurrentUserIdDefault();

        // 实体类有值时，这里不会生效
        strictUpdateFill(metaObject, "updateTime", Date.class, date);
        strictUpdateFill(metaObject, "updateId", Long.class, currentUserIdDefault);

    }

}
