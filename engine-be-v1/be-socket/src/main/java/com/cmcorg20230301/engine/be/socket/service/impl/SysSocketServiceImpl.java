package com.cmcorg20230301.engine.be.socket.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmcorg20230301.engine.be.kafka.util.KafkaUtil;
import com.cmcorg20230301.engine.be.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.engine.be.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.engine.be.security.model.entity.BaseEntity;
import com.cmcorg20230301.engine.be.security.model.entity.BaseEntityNoId;
import com.cmcorg20230301.engine.be.socket.mapper.SysSocketMapper;
import com.cmcorg20230301.engine.be.socket.model.dto.SysSocketPageDTO;
import com.cmcorg20230301.engine.be.socket.model.entity.SysSocketDO;
import com.cmcorg20230301.engine.be.socket.service.SysSocketService;
import org.springframework.stereotype.Service;

@Service
public class SysSocketServiceImpl extends ServiceImpl<SysSocketMapper, SysSocketDO> implements SysSocketService {

    /**
     * 分页排序查询
     */
    @Override
    public Page<SysSocketDO> myPage(SysSocketPageDTO dto) {

        return lambdaQuery().like(StrUtil.isNotBlank(dto.getScheme()), SysSocketDO::getScheme, dto.getScheme())
            .like(StrUtil.isNotBlank(dto.getHost()), SysSocketDO::getHost, dto.getHost())
            .eq(dto.getPort() != null, SysSocketDO::getPort, dto.getPort())
            .eq(dto.getType() != null, SysSocketDO::getType, dto.getType())
            .eq(dto.getEnableFlag() != null, BaseEntity::getEnableFlag, dto.getEnableFlag())
            .like(StrUtil.isNotBlank(dto.getRemark()), SysSocketDO::getRemark, dto.getRemark()).page(dto.page(true));

    }

    /**
     * 批量：禁用socket
     */
    @Override
    public String disableByIdSet(NotEmptyIdSet notEmptyIdSet) {

        lambdaUpdate().in(BaseEntity::getId, notEmptyIdSet.getIdSet()).set(BaseEntityNoId::getEnableFlag, false)
            .update();

        // 发送消息：socket禁用的 topic
        KafkaUtil.sendSocketDisableTopic(notEmptyIdSet.getIdSet());

        return BaseBizCodeEnum.OK;

    }

}




