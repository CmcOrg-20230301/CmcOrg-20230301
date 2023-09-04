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
        this.strictInsertFill(metaObject, "tenantId", Long.class, currentTenantIdDefault);

        this.strictInsertFill(metaObject, "createId", Long.class, currentUserIdDefault);
        this.strictInsertFill(metaObject, "createTime", Date.class, date);

        this.strictInsertFill(metaObject, "updateId", Long.class, currentUserIdDefault);
        this.strictInsertFill(metaObject, "updateTime", Date.class, date);

        this.strictInsertFill(metaObject, "version", Integer.class, 0);

        this.strictInsertFill(metaObject, "uuid", String.class, IdUtil.simpleUUID());

    }

    /**
     * 修改时
     */
    @Override
    public void updateFill(MetaObject metaObject) {

        Date date = new Date();

        Long currentUserIdDefault = UserUtil.getCurrentUserIdDefault();

        // 实体类有值时，这里不会生效
        this.strictUpdateFill(metaObject, "updateTime", Date.class, date);
        this.strictUpdateFill(metaObject, "updateId", Long.class, currentUserIdDefault);

    }

}
