package com.cmcorg20230301.engine.be.pay.base.model.configuration;

import com.cmcorg20230301.engine.be.pay.base.model.entity.SysPayDO;
import com.cmcorg20230301.engine.be.pay.base.model.enums.SysPayRefTypeEnum;

public interface ISysPayRefHandler {

    /**
     * 关联的类型
     */
    SysPayRefTypeEnum getSysPayRefType();

    /**
     * 执行处理，注意：不建议改变 sysPayDO对象里面的属性值
     */
    void handle(final SysPayDO sysPayDO);

}
