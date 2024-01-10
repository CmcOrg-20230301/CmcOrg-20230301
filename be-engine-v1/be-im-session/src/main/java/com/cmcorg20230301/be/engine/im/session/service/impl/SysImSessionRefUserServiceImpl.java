package com.cmcorg20230301.be.engine.im.session.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.cmcorg20230301.be.engine.file.base.service.SysFileService;
import com.cmcorg20230301.be.engine.im.session.mapper.SysImSessionMapper;
import com.cmcorg20230301.be.engine.im.session.mapper.SysImSessionRefUserMapper;
import com.cmcorg20230301.be.engine.im.session.model.entity.SysImSessionDO;
import com.cmcorg20230301.be.engine.im.session.model.entity.SysImSessionRefUserDO;
import com.cmcorg20230301.be.engine.im.session.model.vo.SysImSessionRefUserQueryRefUserInfoMapVO;
import com.cmcorg20230301.be.engine.im.session.service.SysImSessionRefUserService;
import com.cmcorg20230301.be.engine.kafka.util.KafkaUtil;
import com.cmcorg20230301.be.engine.model.model.constant.BaseConstant;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullIdAndLongSet;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullIdAndNotEmptyLongSet;
import com.cmcorg20230301.be.engine.model.model.vo.LongObjectMapVO;
import com.cmcorg20230301.be.engine.redisson.model.enums.BaseRedisKeyEnum;
import com.cmcorg20230301.be.engine.redisson.util.RedissonUtil;
import com.cmcorg20230301.be.engine.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.be.engine.security.model.bo.SysWebSocketEventBO;
import com.cmcorg20230301.be.engine.security.model.dto.WebSocketMessageDTO;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntity;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoIdSuper;
import com.cmcorg20230301.be.engine.security.model.entity.SysUserInfoDO;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.security.util.SysTenantUtil;
import com.cmcorg20230301.be.engine.security.util.SysUserInfoUtil;
import com.cmcorg20230301.be.engine.security.util.UserUtil;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SysImSessionRefUserServiceImpl extends ServiceImpl<SysImSessionRefUserMapper, SysImSessionRefUserDO>
        implements SysImSessionRefUserService {

    @Resource
    SysImSessionMapper sysImSessionMapper;

    @Resource
    SysFileService sysFileService;

    /**
     * 加入新用户
     */
    @Override
    @DSTransactional
    public String joinUserIdSet(NotNullIdAndNotEmptyLongSet notNullIdAndNotEmptyLongSet) {

        Set<Long> userIdSet = notNullIdAndNotEmptyLongSet.getValueSet();

        // 检查：用户 idSet，是否属于当前租户
        SysTenantUtil.checkUserIdSetBelongCurrentTenant(userIdSet);

        Long sessionId = notNullIdAndNotEmptyLongSet.getId();

        Long tenantId = UserUtil.getCurrentTenantIdDefault();

        // 检查：sessionId，是否属于当前租户
        SysImSessionDO sysImSessionDO = ChainWrappers.lambdaQueryChain(sysImSessionMapper).eq(BaseEntityNoIdSuper::getTenantId, tenantId).eq(BaseEntity::getId, sessionId).select(SysImSessionDO::getType).one();

        if (sysImSessionDO == null) {
            ApiResultVO.error(BaseBizCodeEnum.ILLEGAL_REQUEST, sessionId);
        }

        return RedissonUtil.doLock(BaseRedisKeyEnum.PRE_SYS_IM_SESSION_ID + sessionId.toString(), () -> {

            // 查询出：已经存在该会话的用户数据
            List<SysImSessionRefUserDO> sysImSessionRefUserDOList = lambdaQuery().in(SysImSessionRefUserDO::getUserId, userIdSet).eq(BaseEntityNoIdSuper::getTenantId, tenantId).eq(SysImSessionRefUserDO::getSessionId, sessionId).select(SysImSessionRefUserDO::getUserId).list();

            Set<Long> existUserIdSet = sysImSessionRefUserDOList.stream().map(SysImSessionRefUserDO::getUserId).collect(Collectors.toSet());

            if (CollUtil.isNotEmpty(existUserIdSet)) {

                userIdSet.removeAll(existUserIdSet);

            }

            if (CollUtil.isEmpty(userIdSet)) {

                return BaseBizCodeEnum.OK;

            }

            List<SysImSessionRefUserDO> insertList = new ArrayList<>();

            for (Long item : userIdSet) {

                SysImSessionRefUserDO sysImSessionRefUserDO = new SysImSessionRefUserDO();

                sysImSessionRefUserDO.setUserId(item);

                sysImSessionRefUserDO.setSessionId(sessionId);

                sysImSessionRefUserDO.setEnableFlag(true);

                sysImSessionRefUserDO.setDelFlag(false);

                sysImSessionRefUserDO.setRemark("");

                sysImSessionRefUserDO.setPrivateChatRefUserId(BaseConstant.NEGATIVE_ONE_LONG);

                sysImSessionRefUserDO.setSessionNickname("");

                insertList.add(sysImSessionRefUserDO);

            }

            saveBatch(insertList);

            SysWebSocketEventBO<Set<Long>> sysWebSocketEventBO = new SysWebSocketEventBO<>();

            sysWebSocketEventBO.setUserIdSet(userIdSet);

            WebSocketMessageDTO<Set<Long>> webSocketMessageDTO = WebSocketMessageDTO.okData("/sys/im/session/refUser/join/userIdSet", userIdSet);

            sysWebSocketEventBO.setWebSocketMessageDTO(webSocketMessageDTO);

            // 发送：webSocket事件
            KafkaUtil.sendSysWebSocketEventTopic(sysWebSocketEventBO);

            return BaseBizCodeEnum.OK;

        });

    }

    /**
     * 查询：当前会话的用户信息，map
     */
    @SneakyThrows
    @Override
    public LongObjectMapVO<SysImSessionRefUserQueryRefUserInfoMapVO> queryRefUserInfoMap(NotNullIdAndLongSet notNullIdAndLongSet) {

        // 检查：sessionId是否合法
        Long sessionId = SysImSessionContentServiceImpl.checkSessionId(notNullIdAndLongSet.getId(), false);

        Long tenantId = UserUtil.getCurrentTenantIdDefault();

        LongObjectMapVO<SysImSessionRefUserQueryRefUserInfoMapVO> vo = new LongObjectMapVO<>();

        HashMap<Long, SysImSessionRefUserQueryRefUserInfoMapVO> map = MapUtil.newHashMap();

        vo.setMap(map);

        Set<Long> valueSet = notNullIdAndLongSet.getValueSet();

        // 查询出：已经存在该会话的用户数据
        List<SysImSessionRefUserDO> sysImSessionRefUserDOList = lambdaQuery().in(CollUtil.isNotEmpty(valueSet), SysImSessionRefUserDO::getUserId, valueSet).eq(BaseEntityNoIdSuper::getTenantId, tenantId).eq(SysImSessionRefUserDO::getSessionId, sessionId).select(SysImSessionRefUserDO::getUserId, SysImSessionRefUserDO::getSessionNickname).list();

        if (CollUtil.isEmpty(sysImSessionRefUserDOList)) {
            return vo;
        }

        Set<Long> userIdSet = sysImSessionRefUserDOList.stream().map(SysImSessionRefUserDO::getUserId).collect(Collectors.toSet());

        // 查询：用户信息
        List<SysUserInfoDO> sysUserInfoDOList = SysUserInfoUtil.getUserInfoDOList(userIdSet, true);

        Map<Long, SysUserInfoDO> userInfoMap = new HashMap<>(sysUserInfoDOList.size());

        Set<Long> avatarFileIdSet = new HashSet<>();

        for (SysUserInfoDO item : sysUserInfoDOList) {

            userInfoMap.put(item.getId(), item);

            if (item.getAvatarFileId() != -1) {

                avatarFileIdSet.add(item.getAvatarFileId());

            }

        }

        Map<Long, String> avatarUrlMap = MapUtil.newHashMap();

        if (CollUtil.isNotEmpty(avatarFileIdSet)) {

            avatarUrlMap = sysFileService.getPublicUrl(new NotEmptyIdSet(avatarFileIdSet)).getMap();

        }

        // 执行：组装
        queryRefUserInfoMapHandle(sysImSessionRefUserDOList, userInfoMap, avatarUrlMap, map);

        return vo;

    }

    /**
     * 查询：当前会话的用户信息，map，组装
     */
    private static void queryRefUserInfoMapHandle(List<SysImSessionRefUserDO> sysImSessionRefUserDOList, Map<Long, SysUserInfoDO> userInfoMap, Map<Long, String> avatarUrlMap, HashMap<Long, SysImSessionRefUserQueryRefUserInfoMapVO> map) {

        for (SysImSessionRefUserDO item : sysImSessionRefUserDOList) {

            SysUserInfoDO sysUserInfoDO = userInfoMap.get(item.getUserId());

            String sessionNickname = item.getSessionNickname();

            if (StrUtil.isBlank(sessionNickname)) {

                if (sysUserInfoDO != null) {
                    sessionNickname = sysUserInfoDO.getNickname();
                }

            }

            SysImSessionRefUserQueryRefUserInfoMapVO userInfoVO = new SysImSessionRefUserQueryRefUserInfoMapVO();

            userInfoVO.setSessionNickname(sessionNickname);

            if (sysUserInfoDO != null && sysUserInfoDO.getAvatarFileId() != -1) {

                String avatarUrl = avatarUrlMap.get(sysUserInfoDO.getAvatarFileId());

                userInfoVO.setSessionAvatarUrl(avatarUrl);

            }

            map.put(item.getUserId(), userInfoVO);

        }

    }

}
