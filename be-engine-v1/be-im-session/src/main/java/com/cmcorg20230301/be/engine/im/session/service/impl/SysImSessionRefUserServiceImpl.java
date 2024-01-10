package com.cmcorg20230301.be.engine.im.session.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.thread.ThreadUtil;
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
import com.cmcorg20230301.be.engine.model.model.constant.BaseConstant;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullIdAndNotEmptyLongSet;
import com.cmcorg20230301.be.engine.model.model.vo.LongObjectMapVO;
import com.cmcorg20230301.be.engine.redisson.model.enums.BaseRedisKeyEnum;
import com.cmcorg20230301.be.engine.redisson.util.RedissonUtil;
import com.cmcorg20230301.be.engine.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntity;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoIdSuper;
import com.cmcorg20230301.be.engine.security.model.entity.SysUserInfoDO;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.security.util.MyThreadUtil;
import com.cmcorg20230301.be.engine.security.util.SysTenantUtil;
import com.cmcorg20230301.be.engine.security.util.SysUserInfoUtil;
import com.cmcorg20230301.be.engine.security.util.UserUtil;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
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

        return RedissonUtil.doMultiLock(BaseRedisKeyEnum.PRE_SYS_IM_SESSION_REF_USER_ID + sessionId.toString(), userIdSet, () -> {

            // 查询出：已经存在该会话的用户数据
            List<SysImSessionRefUserDO> sysImSessionRefUserDOList = lambdaQuery().in(SysImSessionRefUserDO::getUserId, userIdSet).eq(BaseEntityNoIdSuper::getTenantId, tenantId).eq(SysImSessionRefUserDO::getSessionId, sessionId).select(SysImSessionRefUserDO::getUserId).list();

            if (CollUtil.isNotEmpty(sysImSessionRefUserDOList)) {

                Set<Long> existUserIdSet = sysImSessionRefUserDOList.stream().map(SysImSessionRefUserDO::getUserId).collect(Collectors.toSet());

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

            return BaseBizCodeEnum.OK;

        });

    }

    /**
     * 查询：当前会话的用户信息，map
     */
    @SneakyThrows
    @Override
    public LongObjectMapVO<SysImSessionRefUserQueryRefUserInfoMapVO> queryRefUserInfoMap(NotNullId notNullId) {

        // 检查：sessionId是否合法
        Long sessionId = SysImSessionContentServiceImpl.checkSessionId(notNullId.getId(), false);

        Long tenantId = UserUtil.getCurrentTenantIdDefault();

        LongObjectMapVO<SysImSessionRefUserQueryRefUserInfoMapVO> vo = new LongObjectMapVO<>();

        HashMap<Long, SysImSessionRefUserQueryRefUserInfoMapVO> map = MapUtil.newHashMap();

        vo.setMap(map);

        // 查询出：已经存在该会话的用户数据
        List<SysImSessionRefUserDO> sysImSessionRefUserDOList = lambdaQuery().eq(BaseEntityNoIdSuper::getTenantId, tenantId).eq(SysImSessionRefUserDO::getSessionId, sessionId).select(SysImSessionRefUserDO::getUserId, SysImSessionRefUserDO::getSessionNickname).list();

        if (CollUtil.isEmpty(sysImSessionRefUserDOList)) {
            return vo;
        }

        Set<Long> userIdSet = sysImSessionRefUserDOList.stream().map(SysImSessionRefUserDO::getUserId).collect(Collectors.toSet());

        AtomicReference<Map<Long, SysUserInfoDO>> userInfoMap = new AtomicReference<>(MapUtil.newHashMap());

        AtomicReference<Map<Long, String>> avatarUrlMap = new AtomicReference<>(MapUtil.newHashMap());

        CountDownLatch countDownLatch = ThreadUtil.newCountDownLatch(2);

        MyThreadUtil.execute(() -> {

            userInfoMap.set(SysUserInfoUtil.getUserInfoDoMap(userIdSet, true));

        }, countDownLatch);

        MyThreadUtil.execute(() -> {

            avatarUrlMap.set(sysFileService.getPublicUrl(new NotEmptyIdSet(userIdSet)).getMap());

        }, countDownLatch);

        countDownLatch.await();

        for (SysImSessionRefUserDO item : sysImSessionRefUserDOList) {

            String sessionNickname = item.getSessionNickname();

            if (StrUtil.isBlank(sessionNickname)) {

                SysUserInfoDO sysUserInfoDO = userInfoMap.get().get(item.getUserId());

                if (sysUserInfoDO != null) {
                    sessionNickname = sysUserInfoDO.getNickname();
                }

            }

            String avatarUrl = avatarUrlMap.get().get(item.getUserId());

            SysImSessionRefUserQueryRefUserInfoMapVO userInfoVO = new SysImSessionRefUserQueryRefUserInfoMapVO();

            userInfoVO.setSessionNickname(sessionNickname);

            userInfoVO.setSessionAvatarUrl(avatarUrl);

            map.put(item.getUserId(), userInfoVO);

        }

        return vo;

    }

}
