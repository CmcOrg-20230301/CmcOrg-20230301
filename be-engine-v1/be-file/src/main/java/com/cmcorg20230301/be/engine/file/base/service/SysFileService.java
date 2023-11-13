package com.cmcorg20230301.be.engine.file.base.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cmcorg20230301.be.engine.file.base.model.dto.SysFilePageDTO;
import com.cmcorg20230301.be.engine.file.base.model.dto.SysFilePageSelfDTO;
import com.cmcorg20230301.be.engine.file.base.model.dto.SysFileUploadDTO;
import com.cmcorg20230301.be.engine.file.base.model.entity.SysFileDO;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.model.model.vo.LongObjectMapVO;

import javax.servlet.http.HttpServletResponse;

public interface SysFileService extends IService<SysFileDO> {

    Long upload(SysFileUploadDTO dto);

    void privateDownload(NotNullId notNullId, HttpServletResponse response);

    String removeByFileIdSet(NotEmptyIdSet notEmptyIdSet);

    LongObjectMapVO<String> getPublicUrl(NotEmptyIdSet notEmptyIdSet);

    Page<SysFileDO> myPage(SysFilePageDTO dto);

    Page<SysFileDO> myPageSelf(SysFilePageSelfDTO dto);

    Page<SysFileDO> myPageTenant(SysFilePageSelfDTO dto);

}
