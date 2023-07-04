package com.cmcorg20230301.engine.be.socket.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cmcorg20230301.engine.be.socket.model.entity.SysSocketDO;
import org.apache.ibatis.annotations.Param;

public interface SysSocketMapper extends BaseMapper<SysSocketDO> {

    // 获取：最小连接数的 socket对象
    SysSocketDO getSocketDOOfMinConnectNumber(@Param("sysSocketDO") SysSocketDO sysSocketDO);

}




