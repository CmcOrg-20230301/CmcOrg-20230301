package com.cmcorg20230301.be.engine.socket.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmcorg20230301.be.engine.cache.util.CacheRedisKafkaLocalUtil;
import com.cmcorg20230301.be.engine.kafka.util.KafkaUtil;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullIdAndNotEmptyLongSet;
import com.cmcorg20230301.be.engine.model.model.enums.BaseWebSocketUriEnum;
import com.cmcorg20230301.be.engine.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.be.engine.security.model.bo.SysWebSocketEventBO;
import com.cmcorg20230301.be.engine.security.model.dto.WebSocketMessageDTO;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntity;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoId;
import com.cmcorg20230301.be.engine.security.util.SysTenantUtil;
import com.cmcorg20230301.be.engine.socket.mapper.SysSocketRefUserMapper;
import com.cmcorg20230301.be.engine.socket.model.dto.SysSocketRefUserPageDTO;
import com.cmcorg20230301.be.engine.socket.model.entity.SysSocketRefUserDO;
import com.cmcorg20230301.be.engine.socket.service.SysSocketRefUserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SysSocketRefUserServiceImpl extends ServiceImpl<SysSocketRefUserMapper, SysSocketRefUserDO>
        implements SysSocketRefUserService {

    /**
     * 分页排序查询
     */
    @Override
    public Page<SysSocketRefUserDO> myPage(SysSocketRefUserPageDTO dto) {

        // 处理：MyTenantPageDTO
        SysTenantUtil.handleMyTenantPageDTO(dto, false);

        return lambdaQuery().eq(dto.getUserId() != null, SysSocketRefUserDO::getUserId, dto.getUserId())
                .eq(dto.getSocketId() != null, SysSocketRefUserDO::getSocketId, dto.getSocketId())
                .like(StrUtil.isNotBlank(dto.getScheme()), SysSocketRefUserDO::getScheme, dto.getScheme())
                .like(StrUtil.isNotBlank(dto.getHost()), SysSocketRefUserDO::getHost, dto.getHost())
                .eq(dto.getPort() != null, SysSocketRefUserDO::getPort, dto.getPort())
                .eq(dto.getType() != null, SysSocketRefUserDO::getType, dto.getType())
                .eq(dto.getId() != null, BaseEntity::getId, dto.getId())
                .eq(dto.getOnlineType() != null, SysSocketRefUserDO::getOnlineType, dto.getOnlineType())
                .like(StrUtil.isNotBlank(dto.getIp()), SysSocketRefUserDO::getIp, dto.getIp())
                .like(StrUtil.isNotBlank(dto.getRegion()), SysSocketRefUserDO::getRegion, dto.getRegion())
                .like(StrUtil.isNotBlank(dto.getRemark()), SysSocketRefUserDO::getRemark, dto.getRemark())
                .in(BaseEntityNoId::getTenantId, dto.getTenantIdSet()) //
                .page(dto.page(true));

    }

    /**
     * 批量：下线用户
     */
    @Override
    public String offlineByIdSet(NotEmptyIdSet notEmptyIdSet) {

        if (CollUtil.isEmpty(notEmptyIdSet.getIdSet())) {
            return BaseBizCodeEnum.OK;
        }

        // 检查：是否非法操作
        SysTenantUtil.checkIllegal(notEmptyIdSet.getIdSet(),
                tenantIdSet -> lambdaQuery().in(BaseEntity::getId, notEmptyIdSet.getIdSet())
                        .in(BaseEntityNoId::getTenantId, tenantIdSet).count());

        List<SysSocketRefUserDO> sysSocketRefUserDOList = lambdaQuery().in(BaseEntity::getId, notEmptyIdSet.getIdSet())
                .select(SysSocketRefUserDO::getJwtHash, SysSocketRefUserDO::getJwtHashExpireTs).list();

        if (CollUtil.isNotEmpty(sysSocketRefUserDOList)) {

            for (SysSocketRefUserDO sysSocketRefUserDO : sysSocketRefUserDOList) {

                CacheRedisKafkaLocalUtil
                        .put(sysSocketRefUserDO.getJwtHash(), sysSocketRefUserDO.getJwtHashExpireTs(), () -> "不可用的 jwt：下线");

            }

            lambdaUpdate().in(BaseEntity::getId, notEmptyIdSet.getIdSet()).remove();

        }

        return BaseBizCodeEnum.OK;

    }

    /**
     * 批量：打开控制台
     */
    @Override
    public String changeConsoleFlagByIdSet(NotEmptyIdSet notEmptyIdSet) {

        List<SysSocketRefUserDO> sysSocketRefUserDOList = lambdaQuery().in(BaseEntity::getId, notEmptyIdSet.getIdSet()).select(SysSocketRefUserDO::getUserId).list();

        Set<Long> userIdSet = sysSocketRefUserDOList.stream().map(SysSocketRefUserDO::getUserId).collect(Collectors.toSet());

        if (CollUtil.isEmpty(userIdSet)) {
            return BaseBizCodeEnum.OK;
        }

        SysWebSocketEventBO<NotNullIdAndNotEmptyLongSet> sysWebSocketEventBO = new SysWebSocketEventBO<>();

        sysWebSocketEventBO.setUserIdSet(userIdSet);

        sysWebSocketEventBO.setSysSocketRefUserIdSet(notEmptyIdSet.getIdSet());

        WebSocketMessageDTO<NotNullIdAndNotEmptyLongSet> webSocketMessageDTO = WebSocketMessageDTO.okData(BaseWebSocketUriEnum.SYS_SOCKET_REF_USER_CHANGE_CONSOLE_FLAG_BY_ID_SET, null);

        sysWebSocketEventBO.setWebSocketMessageDTO(webSocketMessageDTO);

        // 发送：webSocket事件
        KafkaUtil.sendSysWebSocketEventTopic(sysWebSocketEventBO);

        return BaseBizCodeEnum.OK;

    }

}
