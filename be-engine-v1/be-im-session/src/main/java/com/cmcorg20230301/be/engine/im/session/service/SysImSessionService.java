package com.cmcorg20230301.be.engine.im.session.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cmcorg20230301.be.engine.im.session.model.dto.SysImSessionInsertOrUpdateDTO;
import com.cmcorg20230301.be.engine.im.session.model.dto.SysImSessionPageDTO;
import com.cmcorg20230301.be.engine.im.session.model.dto.SysImSessionQueryCustomerSessionIdUserSelfDTO;
import com.cmcorg20230301.be.engine.im.session.model.entity.SysImSessionDO;

public interface SysImSessionService extends IService<SysImSessionDO> {

    Long insertOrUpdate(SysImSessionInsertOrUpdateDTO dto);

    Page<SysImSessionDO> myPage(SysImSessionPageDTO dto);

    Long queryCustomerSessionIdUserSelf(SysImSessionQueryCustomerSessionIdUserSelfDTO dto);

}
