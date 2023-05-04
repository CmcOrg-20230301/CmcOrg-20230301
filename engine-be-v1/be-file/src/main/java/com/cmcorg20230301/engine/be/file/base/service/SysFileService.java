package com.cmcorg20230301.engine.be.file.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cmcorg20230301.engine.be.file.base.model.entity.SysFileDO;
import com.cmcorg20230301.engine.be.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.engine.be.model.model.dto.NotNullId;
import com.cmcorg20230301.engine.be.model.model.vo.LongObjectMapVO;
import com.cmcorg20230301.engine.be.security.model.dto.SysFileUploadDTO;

import javax.servlet.http.HttpServletResponse;

public interface SysFileService extends IService<SysFileDO> {

    Long upload(SysFileUploadDTO dto);

    void privateDownload(NotNullId notNullId, HttpServletResponse response);

    String removeByFileIdSet(NotEmptyIdSet notEmptyIdSet);

    LongObjectMapVO<String> getPublicUrl(NotEmptyIdSet notEmptyIdSet);

}
