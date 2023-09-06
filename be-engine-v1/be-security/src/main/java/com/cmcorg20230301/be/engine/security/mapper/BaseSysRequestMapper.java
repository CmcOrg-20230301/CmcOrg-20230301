package com.cmcorg20230301.be.engine.security.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cmcorg20230301.be.engine.security.model.entity.SysRequestDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

@Mapper
public interface BaseSysRequestMapper extends BaseMapper<SysRequestDO> {

    // 查询：最后活跃时间
    List<SysRequestDO> selectLastActiveTime(@Param("userIdSet") Set<Long> userIdSet);

}
