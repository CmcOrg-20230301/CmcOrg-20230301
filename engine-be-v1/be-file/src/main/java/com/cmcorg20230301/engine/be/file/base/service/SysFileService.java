package com.cmcorg20230301.engine.be.file.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cmcorg20230301.engine.be.file.base.model.entity.SysFile;
import com.cmcorg20230301.engine.be.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.engine.be.model.model.dto.NotNullId;
import com.cmcorg20230301.engine.be.security.model.dto.SysFileUploadDTO;

import javax.servlet.http.HttpServletResponse;

public interface SysFileService extends IService<SysFile> {

    String upload(SysFileUploadDTO dto);

    void privateDownload(NotNullId notNullId, HttpServletResponse response);

    String removeByFileIdSet(NotEmptyIdSet notEmptyIdSet);

}
