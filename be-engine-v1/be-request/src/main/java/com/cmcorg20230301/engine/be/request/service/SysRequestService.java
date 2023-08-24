package com.cmcorg20230301.engine.be.request.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cmcorg20230301.engine.be.request.model.dto.SysRequestPageDTO;
import com.cmcorg20230301.engine.be.request.model.dto.SysRequestSelfLoginRecordPageDTO;
import com.cmcorg20230301.engine.be.request.model.vo.SysRequestAllAvgVO;
import com.cmcorg20230301.engine.be.security.model.entity.SysRequestDO;

public interface SysRequestService extends IService<SysRequestDO> {

    Page<SysRequestDO> myPage(SysRequestPageDTO dto);

    SysRequestAllAvgVO allAvgPro(SysRequestPageDTO dto);

    SysRequestAllAvgVO allAvg();

    Page<SysRequestDO> selfLoginRecord(SysRequestSelfLoginRecordPageDTO dto);

}
