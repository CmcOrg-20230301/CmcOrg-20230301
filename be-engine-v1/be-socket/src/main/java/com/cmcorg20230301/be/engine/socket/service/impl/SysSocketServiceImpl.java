package com.cmcorg20230301.be.engine.socket.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmcorg20230301.be.engine.kafka.util.KafkaUtil;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.be.engine.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntity;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoId;
import com.cmcorg20230301.be.engine.socket.mapper.SysSocketMapper;
import com.cmcorg20230301.be.engine.socket.model.dto.SysSocketPageDTO;
import com.cmcorg20230301.be.engine.socket.model.entity.SysSocketDO;
import com.cmcorg20230301.be.engine.socket.service.SysSocketService;
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
            .eq(dto.getId() != null, BaseEntity::getId, dto.getId())
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

    /**
     * 批量：启用socket
     */
    @Override
    public String enableByIdSet(NotEmptyIdSet notEmptyIdSet) {

        lambdaUpdate().in(BaseEntity::getId, notEmptyIdSet.getIdSet()).set(BaseEntityNoId::getEnableFlag, true)
            .update();

        // 发送消息：socket启用的 topic
        KafkaUtil.sendSocketEnableTopic(notEmptyIdSet.getIdSet());

        return BaseBizCodeEnum.OK;

    }

}




