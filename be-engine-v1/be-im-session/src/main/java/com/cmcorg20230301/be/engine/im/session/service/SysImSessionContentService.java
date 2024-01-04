package com.cmcorg20230301.be.engine.im.session.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cmcorg20230301.be.engine.im.session.model.dto.SysImSessionContentListDTO;
import com.cmcorg20230301.be.engine.im.session.model.dto.SysImSessionContentSendTextListDTO;
import com.cmcorg20230301.be.engine.im.session.model.entity.SysImSessionContentDO;

import javax.validation.Valid;

public interface SysImSessionContentService extends IService<SysImSessionContentDO> {

    String sendText(@Valid SysImSessionContentSendTextListDTO dto);

    Page<SysImSessionContentDO> scrollPage(SysImSessionContentListDTO dto);

}
