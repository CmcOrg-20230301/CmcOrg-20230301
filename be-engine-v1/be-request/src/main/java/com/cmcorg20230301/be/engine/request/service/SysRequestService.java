package com.cmcorg20230301.be.engine.request.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cmcorg20230301.be.engine.request.model.dto.SysRequestPageDTO;
import com.cmcorg20230301.be.engine.request.model.dto.SysRequestSelfLoginRecordPageDTO;
import com.cmcorg20230301.be.engine.request.model.vo.SysRequestAllAvgVO;
import com.cmcorg20230301.be.engine.security.model.entity.SysRequestDO;

public interface SysRequestService extends IService<SysRequestDO> {

    Page<SysRequestDO> myPage(SysRequestPageDTO dto);

    SysRequestAllAvgVO allAvgPro(SysRequestPageDTO dto);

    SysRequestAllAvgVO allAvg();

    Page<SysRequestDO> selfLoginRecord(SysRequestSelfLoginRecordPageDTO dto);

}
