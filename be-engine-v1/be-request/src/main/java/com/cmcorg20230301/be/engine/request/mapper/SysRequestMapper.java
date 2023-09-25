package com.cmcorg20230301.be.engine.request.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cmcorg20230301.be.engine.model.model.constant.BaseDsValueConstant;
import com.cmcorg20230301.be.engine.request.model.dto.SysRequestPageDTO;
import com.cmcorg20230301.be.engine.request.model.vo.SysRequestAllAvgVO;
import com.cmcorg20230301.be.engine.security.model.entity.SysRequestDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
@DS(value = BaseDsValueConstant.BE_DORIS_V_1)
public interface SysRequestMapper extends BaseMapper<SysRequestDO> {

    // 所有请求的平均耗时-增强：增加筛选项
    SysRequestAllAvgVO allAvgPro(@Param("dto") SysRequestPageDTO dto);

}
