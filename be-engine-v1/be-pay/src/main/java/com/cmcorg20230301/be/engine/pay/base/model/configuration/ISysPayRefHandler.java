package com.cmcorg20230301.be.engine.pay.base.model.configuration;

import com.cmcorg20230301.be.engine.pay.base.model.entity.SysPayDO;
import com.cmcorg20230301.be.engine.pay.base.model.interfaces.ISysPayRefType;

public interface ISysPayRefHandler {

    /**
     * 关联的类型
     */
    ISysPayRefType getSysPayRefType();

    /**
     * 执行处理，注意：不建议改变 sysPayDO对象里面的属性值
     */
    void handle(final SysPayDO sysPayDO);

}
