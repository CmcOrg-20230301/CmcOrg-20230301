package com.cmcorg20230301.be.engine.im.session.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cmcorg20230301.be.engine.im.session.model.dto.SysImSessionContentListDTO;
import com.cmcorg20230301.be.engine.im.session.model.dto.SysImSessionContentSendListDTO;
import com.cmcorg20230301.be.engine.im.session.model.entity.SysImSessionContentDO;

import javax.validation.Valid;
import java.util.List;

public interface SysImSessionContentService extends IService<SysImSessionContentDO> {

    String send(@Valid SysImSessionContentSendListDTO dto);

    List<SysImSessionContentDO> myList(SysImSessionContentListDTO dto);

}
