package com.cmcorg20230301.engine.be.socket.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmcorg20230301.engine.be.security.model.entity.BaseEntityNoId;
import com.cmcorg20230301.engine.be.socket.mapper.SysSocketRefUserMapper;
import com.cmcorg20230301.engine.be.socket.model.dto.SysSocketRefUserPageDTO;
import com.cmcorg20230301.engine.be.socket.model.entity.SysSocketRefUserDO;
import com.cmcorg20230301.engine.be.socket.service.SysSocketRefUserService;
import org.springframework.stereotype.Service;

@Service
public class SysSocketRefUserServiceImpl extends ServiceImpl<SysSocketRefUserMapper, SysSocketRefUserDO>
    implements SysSocketRefUserService {

    /**
     * 分页排序查询
     */
    @Override
    public Page<SysSocketRefUserDO> myPage(SysSocketRefUserPageDTO dto) {

        return lambdaQuery().eq(dto.getUserId() != null, SysSocketRefUserDO::getUserId, dto.getUserId())
            .eq(dto.getSocketId() != null, SysSocketRefUserDO::getSocketId, dto.getSocketId())
            .like(StrUtil.isNotBlank(dto.getNickname()), SysSocketRefUserDO::getNickname, dto.getNickname())
            .like(StrUtil.isNotBlank(dto.getScheme()), SysSocketRefUserDO::getScheme, dto.getScheme())
            .like(StrUtil.isNotBlank(dto.getHost()), SysSocketRefUserDO::getHost, dto.getHost())
            .eq(dto.getPort() != null, SysSocketRefUserDO::getPort, dto.getPort())
            .eq(dto.getType() != null, SysSocketRefUserDO::getType, dto.getType())
            .like(StrUtil.isNotBlank(dto.getRemark()), SysSocketRefUserDO::getRemark, dto.getRemark())
            .orderByDesc(BaseEntityNoId::getUpdateTime).page(dto.page(true));

    }

}




