package com.cmcorg20230301.engine.be.request.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cmcorg20230301.engine.be.request.model.dto.SysRequestPageDTO;
import com.cmcorg20230301.engine.be.request.model.vo.SysRequestAllAvgVO;
import com.cmcorg20230301.engine.be.security.model.entity.SysRequestDO;
import org.apache.ibatis.annotations.Param;

public interface SysRequestMapper extends BaseMapper<SysRequestDO> {

    // 所有请求的平均耗时-增强：增加筛选项
    SysRequestAllAvgVO allAvgPro(@Param("dto") SysRequestPageDTO dto);

    // 所有请求的平均耗时
    SysRequestAllAvgVO allAvg();

}
