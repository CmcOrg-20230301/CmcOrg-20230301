package com.cmcorg20230301.be.engine.im.session.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.cmcorg20230301.be.engine.im.session.mapper.SysImSessionContentMapper;
import com.cmcorg20230301.be.engine.im.session.mapper.SysImSessionMapper;
import com.cmcorg20230301.be.engine.im.session.model.dto.*;
import com.cmcorg20230301.be.engine.im.session.model.entity.SysImSessionContentDO;
import com.cmcorg20230301.be.engine.im.session.model.entity.SysImSessionDO;
import com.cmcorg20230301.be.engine.im.session.model.entity.SysImSessionRefUserDO;
import com.cmcorg20230301.be.engine.im.session.model.enums.SysImSessionTypeEnum;
import com.cmcorg20230301.be.engine.im.session.service.SysImSessionRefUserService;
import com.cmcorg20230301.be.engine.im.session.service.SysImSessionService;
import com.cmcorg20230301.be.engine.redisson.model.enums.BaseRedisKeyEnum;
import com.cmcorg20230301.be.engine.redisson.util.RedissonUtil;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntity;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoId;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoIdSuper;
import com.cmcorg20230301.be.engine.security.util.*;
import com.cmcorg20230301.be.engine.util.util.NicknameUtil;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.func.Func1;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import lombok.SneakyThrows;

@Service
public class SysImSessionServiceImpl extends ServiceImpl<SysImSessionMapper, SysImSessionDO>
    implements SysImSessionService {

    private static SysImSessionRefUserService sysImSessionRefUserService;

    @Resource
    public void setSysImSessionRefUserService(SysImSessionRefUserService sysImSessionRefUserService) {
        SysImSessionServiceImpl.sysImSessionRefUserService = sysImSessionRefUserService;
    }

    @Resource
    SysImSessionContentMapper sysImSessionContentMapper;

    /**
     * 新增/修改
     */
    @Override
    public Long insertOrUpdate(SysImSessionInsertOrUpdateDTO dto) {

        // 处理：BaseTenantInsertOrUpdateDTO
        SysTenantUtil.handleBaseTenantInsertOrUpdateDTO(dto, getCheckIllegalFunc1(CollUtil.newHashSet(dto.getId())),
            getTenantIdBaseEntityFunc1());

        SysImSessionDO sysImSessionDO = new SysImSessionDO();

        sysImSessionDO.setName(MyEntityUtil.getNotNullStr(dto.getName(), NicknameUtil.getDateTimeNickname("会话-")));

        if (dto.getId() == null) { // 只有：新增时才有效

            sysImSessionDO.setType(dto.getType());

            sysImSessionDO.setBelongId(UserUtil.getCurrentUserId());

            sysImSessionDO.setLastReceiveContentTs(-1L);

            sysImSessionDO.setAvatarFileId(MyEntityUtil.getNotNullLong(dto.getAvatarFileId()));

        }

        sysImSessionDO.setId(dto.getId());

        sysImSessionDO.setEnableFlag(true);
        sysImSessionDO.setDelFlag(false);

        sysImSessionDO.setRemark(MyEntityUtil.getNotNullStr(dto.getRemark()));

        saveOrUpdate(sysImSessionDO);

        return sysImSessionDO.getId();

    }

    /**
     * 分页排序查询
     *
     * @param queryNoJoinSessionContentFlag 是否查询：未加入的会话的会话内容
     */
    @SneakyThrows
    @Override
    public Page<SysImSessionDO> myPage(SysImSessionPageDTO dto, boolean queryNoJoinSessionContentFlag) {

        // 处理：MyTenantPageDTO
        SysTenantUtil.handleMyTenantPageDTO(dto, true);

        Page<SysImSessionDO> page =
            lambdaQuery().like(StrUtil.isNotBlank(dto.getName()), SysImSessionDO::getName, dto.getName())
                .eq(dto.getType() != null, SysImSessionDO::getType, dto.getType())
                .in(BaseEntityNoId::getTenantId, dto.getTenantIdSet()) //
                .page(dto.updateTimeDescDefaultOrderPage(true));

        if (CollUtil.isEmpty(page.getRecords())) {
            return page;
        }

        if (!BooleanUtil.isTrue(dto.getQueryContentInfoFlag())) {
            return page;
        }

        // 查询：会话的消息相关信息
        myPageQueryContentInfo(queryNoJoinSessionContentFlag, page.getRecords(), null);

        return page;

    }

    /**
     * 查询：会话的消息相关信息
     *
     * @param queryNoJoinSessionContentFlag 是否查询：未加入会话的聊天内容，一般为 false
     */
    @SneakyThrows
    private void myPageQueryContentInfo(boolean queryNoJoinSessionContentFlag, List<SysImSessionDO> sysImSessionDOList,
        @Nullable Map<Long, Long> lastOpenTsMap) {

        if (CollUtil.isEmpty(sysImSessionDOList)) {
            return;
        }

        // 查询：未读的消息数量和最后一条未读的消息内容
        Map<Long, SysImSessionDO> sessionMap =
            sysImSessionDOList.stream().collect(Collectors.toMap(BaseEntity::getId, it -> it));

        Long currentUserId = UserUtil.getCurrentUserId();

        List<SysImSessionRefUserDO> sysImSessionRefUserDOList =
            sysImSessionRefUserService.lambdaQuery().eq(SysImSessionRefUserDO::getUserId, currentUserId)
                .in(SysImSessionRefUserDO::getSessionId, sessionMap.keySet())
                .select(SysImSessionRefUserDO::getSessionId, SysImSessionRefUserDO::getLastOpenTs).list();

        if (CollUtil.isEmpty(sysImSessionRefUserDOList) && !queryNoJoinSessionContentFlag) {
            return;
        }

        if (lastOpenTsMap == null) {

            // 获取：打开每个会话的最近时间 map
            lastOpenTsMap = getLastOpenTsMap(queryNoJoinSessionContentFlag, currentUserId, sessionMap);

        }

        if (lastOpenTsMap == null) {
            return;
        }

        CountDownLatch countDownLatch = ThreadUtil.newCountDownLatch(sysImSessionDOList.size());

        for (SysImSessionDO item : sysImSessionDOList) {

            Long sessionId = item.getId();

            // 最后一次：打开该会话的时间戳
            Long lastOpenTs = lastOpenTsMap.get(sessionId);

            if (lastOpenTs == null) {

                if (queryNoJoinSessionContentFlag) {

                    lastOpenTs = 0L;

                } else {

                    countDownLatch.countDown();
                    continue;

                }

            }

            Long finalLastOpenTs = lastOpenTs;

            MyThreadUtil.execute(() -> {

                // 未读消息的数量
                Long unreadContentTotal = ChainWrappers.lambdaQueryChain(sysImSessionContentMapper)
                    .eq(SysImSessionContentDO::getSessionId, sessionId).eq(SysImSessionContentDO::getShowFlag, true)
                    .gt(SysImSessionContentDO::getCreateTs, finalLastOpenTs).count();

                if (unreadContentTotal > 99) {

                    item.setUnreadContentTotal(100);

                } else {

                    item.setUnreadContentTotal(unreadContentTotal.intValue());

                }

                // 查询出：最后一条消息
                Page<SysImSessionContentDO> sessionContentPage =
                    ChainWrappers.lambdaQueryChain(sysImSessionContentMapper)
                        .eq(SysImSessionContentDO::getSessionId, sessionId).eq(SysImSessionContentDO::getShowFlag, true)
                        .select(SysImSessionContentDO::getContent, SysImSessionContentDO::getCreateTs,
                            SysImSessionContentDO::getType)
                        .orderByDesc(SysImSessionContentDO::getCreateTs).page(MyPageUtil.getLimit1Page());

                if (CollUtil.isEmpty(sessionContentPage.getRecords())) {
                    return;
                }

                SysImSessionContentDO sysImSessionContentDO = sessionContentPage.getRecords().get(0);

                item.setLastContent(sysImSessionContentDO.getContent());

                item.setLastContentType(sysImSessionContentDO.getType());

                item.setLastContentCreateTs(sysImSessionContentDO.getCreateTs());

            }, countDownLatch);

        }

        countDownLatch.await();

    }

    /**
     * 获取：打开每个会话的最近时间 map
     */
    @Nullable
    private Map<Long, Long> getLastOpenTsMap(boolean queryNoJoinSessionContentFlag, Long currentUserId,
        Map<Long, SysImSessionDO> sessionMap) {

        List<SysImSessionRefUserDO> sysImSessionRefUserDOList =
            sysImSessionRefUserService.lambdaQuery().eq(SysImSessionRefUserDO::getUserId, currentUserId)
                .in(SysImSessionRefUserDO::getSessionId, sessionMap.keySet())
                .select(SysImSessionRefUserDO::getSessionId, SysImSessionRefUserDO::getLastOpenTs).list();

        if (CollUtil.isEmpty(sysImSessionRefUserDOList) && !queryNoJoinSessionContentFlag) {
            return null;
        }

        return sysImSessionRefUserDOList.stream()
            .collect(Collectors.toMap(SysImSessionRefUserDO::getSessionId, SysImSessionRefUserDO::getLastOpenTs));

    }

    /**
     * 查询：用户自我，所属客服会话的主键 id
     */
    @Override
    @DSTransactional
    public Long queryCustomerSessionIdUserSelf(SysImSessionQueryCustomerSessionIdUserSelfDTO dto) {

        Long userId = UserUtil.getCurrentUserId();

        Long tenantId = UserUtil.getCurrentTenantIdDefault();

        if (StrUtil.isBlank(dto.getName())) {
            dto.setName("客服");
        }

        if (dto.getType() == null) {
            dto.setType(SysImSessionTypeEnum.CUSTOMER.getCode());
        }

        return RedissonUtil.doLock(BaseRedisKeyEnum.PRE_SYS_IM_SESSION_CUSTOMER.name() + userId, () -> {

            SysImSessionDO sysImSessionDO =
                lambdaQuery().eq(SysImSessionDO::getType, dto.getType()).eq(SysImSessionDO::getBelongId, userId)
                    .eq(BaseEntityNoIdSuper::getTenantId, tenantId).select(BaseEntity::getId).one();

            Long sessionId;

            if (sysImSessionDO == null) {

                SysImSessionInsertOrUpdateDTO sysImSessionInsertOrUpdateDTO = new SysImSessionInsertOrUpdateDTO();

                sysImSessionInsertOrUpdateDTO.setType(dto.getType());

                sysImSessionInsertOrUpdateDTO.setName(dto.getName());

                sessionId = insertOrUpdate(sysImSessionInsertOrUpdateDTO); // 新增会话

                // 加入会话
                queryCustomerSessionIdUserSelfJoinSession(userId, sessionId);

                return sessionId;

            }

            sessionId = sysImSessionDO.getId();

            return RedissonUtil.doLock(BaseRedisKeyEnum.PRE_SYS_IM_SESSION_REF_USER.name() + sessionId + userId, () -> {

                // 查询出：是否已经存在该会话中
                boolean exists = sysImSessionRefUserService.lambdaQuery().eq(BaseEntityNoIdSuper::getTenantId, tenantId)
                    .eq(SysImSessionRefUserDO::getSessionId, sessionId).eq(SysImSessionRefUserDO::getUserId, userId)
                    .exists();

                if (exists) {
                    return sessionId;
                }

                // 加入会话
                queryCustomerSessionIdUserSelfJoinSession(userId, sessionId);

                return sessionId;

            });

        });

    }

    /**
     * 加入会话
     */
    private void queryCustomerSessionIdUserSelfJoinSession(Long userId, Long sessionId) {

        SysImSessionRefUserJoinUserIdSetDTO sysImSessionRefUserJoinUserIdSetDTO =
            new SysImSessionRefUserJoinUserIdSetDTO();

        sysImSessionRefUserJoinUserIdSetDTO.setValueSet(CollUtil.newHashSet(userId));

        sysImSessionRefUserJoinUserIdSetDTO.setId(sessionId);

        // 把当前用户，加入会话中
        sysImSessionRefUserService.joinUserIdSet(sysImSessionRefUserJoinUserIdSetDTO);

    }

    /**
     * 分页排序查询-会话列表-自我
     */
    @Override
    public Page<SysImSessionDO> myPageSelf(SysImSessionSelfPageDTO dto) {

        Long userId = UserUtil.getCurrentUserId();

        Long tenantId = UserUtil.getCurrentTenantIdDefault();

        dto.setUserId(userId);

        dto.setTenantId(tenantId);

        Page<SysImSessionDO> page = baseMapper.myPageSelf(dto.page(), dto);

        Map<Long, Long> lastOpenTsMap =
            page.getRecords().stream().collect(Collectors.toMap(BaseEntity::getId, SysImSessionDO::getLastOpenTs));

        // 查询：会话的消息相关信息
        myPageQueryContentInfo(false, page.getRecords(), lastOpenTsMap);

        return page;

    }

    /**
     * 获取：检查：是否非法操作的 getCheckIllegalFunc1
     */
    @NotNull
    private Func1<Set<Long>, Long> getCheckIllegalFunc1(Set<Long> idSet) {

        return tenantIdSet -> lambdaQuery().in(BaseEntity::getId, idSet).in(BaseEntityNoId::getTenantId, tenantIdSet)
            .count();

    }

    /**
     * 获取：检查：是否非法操作的 getTenantIdBaseEntityFunc1
     */
    @NotNull
    private Func1<Long, BaseEntity> getTenantIdBaseEntityFunc1() {

        return id -> lambdaQuery().eq(BaseEntity::getId, id).select(BaseEntity::getTenantId).one();

    }

}
