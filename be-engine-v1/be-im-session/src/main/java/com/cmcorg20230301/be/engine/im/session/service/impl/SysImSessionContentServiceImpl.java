package com.cmcorg20230301.be.engine.im.session.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.cmcorg20230301.be.engine.im.session.mapper.SysImSessionContentMapper;
import com.cmcorg20230301.be.engine.im.session.mapper.SysImSessionMapper;
import com.cmcorg20230301.be.engine.im.session.mapper.SysImSessionRefUserMapper;
import com.cmcorg20230301.be.engine.im.session.model.dto.SysImSessionContentListDTO;
import com.cmcorg20230301.be.engine.im.session.model.dto.SysImSessionContentSendTextDTO;
import com.cmcorg20230301.be.engine.im.session.model.dto.SysImSessionContentSendTextListDTO;
import com.cmcorg20230301.be.engine.im.session.model.entity.SysImSessionContentDO;
import com.cmcorg20230301.be.engine.im.session.model.entity.SysImSessionDO;
import com.cmcorg20230301.be.engine.im.session.model.entity.SysImSessionRefUserDO;
import com.cmcorg20230301.be.engine.im.session.model.enums.SysImSessionContentTypeEnum;
import com.cmcorg20230301.be.engine.im.session.service.SysImSessionContentService;
import com.cmcorg20230301.be.engine.kafka.util.KafkaUtil;
import com.cmcorg20230301.be.engine.model.model.constant.BaseConstant;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullIdAndNotEmptyLongSet;
import com.cmcorg20230301.be.engine.model.model.enums.BaseWebSocketUriEnum;
import com.cmcorg20230301.be.engine.redisson.util.IdGeneratorUtil;
import com.cmcorg20230301.be.engine.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.be.engine.security.model.bo.SysWebSocketEventBO;
import com.cmcorg20230301.be.engine.security.model.dto.WebSocketMessageDTO;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntity;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoIdSuper;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.security.util.MyPageUtil;
import com.cmcorg20230301.be.engine.security.util.UserUtil;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;

@Service
public class SysImSessionContentServiceImpl extends ServiceImpl<SysImSessionContentMapper, SysImSessionContentDO>
    implements SysImSessionContentService {

    private static SysImSessionMapper sysImSessionMapper;

    @Resource
    public void setSysImSessionMapper(SysImSessionMapper sysImSessionMapper) {
        SysImSessionContentServiceImpl.sysImSessionMapper = sysImSessionMapper;
    }

    private static SysImSessionRefUserMapper sysImSessionRefUserMapper;

    @Resource
    public void setSysImSessionRefUserService(SysImSessionRefUserMapper sysImSessionRefUserMapper) {
        SysImSessionContentServiceImpl.sysImSessionRefUserMapper = sysImSessionRefUserMapper;
    }

    /**
     * 发送内容-文字-用户自我
     *
     * @return createTsSet
     */
    @Override
    public NotNullIdAndNotEmptyLongSet sendTextUserSelf(SysImSessionContentSendTextListDTO dto) {

        int maxLength = 2500;

        // 检查：内容长度
        boolean anyMatch = dto.getContentSet().stream().anyMatch(item -> {

            return item.getContent().length() > maxLength;

        });

        if (anyMatch) {

            ApiResultVO.error("操作失败：字数长度不能超过 " + maxLength, dto.getSessionId());

        }

        // 检查：sessionId是否合法
        Long sessionId = checkSessionId(dto.getSessionId(), true);

        Long userId = UserUtil.getCurrentUserId();

        Long tenantId = UserUtil.getCurrentTenantIdDefault();

        // 处理消息：防止重复添加消息
        handleSendTextUserSelfDTO(dto, userId, tenantId);

        // 执行
        doSendTextUserSelf(dto, sessionId, userId, tenantId);

        Set<Long> createTsSet =
            dto.getContentSet().stream().map(SysImSessionContentSendTextDTO::getCreateTs).collect(Collectors.toSet());

        return new NotNullIdAndNotEmptyLongSet(sessionId, createTsSet);

    }

    /**
     * 处理消息：防止重复添加消息
     */
    private void handleSendTextUserSelfDTO(SysImSessionContentSendTextListDTO dto, Long userId, Long tenantId) {

        Set<Long> createTsSet =
            dto.getContentSet().stream().map(SysImSessionContentSendTextDTO::getCreateTs).collect(Collectors.toSet());

        List<SysImSessionContentDO> sysImSessionContentDOList =
            lambdaQuery().eq(SysImSessionContentDO::getSessionId, dto.getSessionId())
                .eq(BaseEntityNoIdSuper::getCreateId, userId).eq(BaseEntityNoIdSuper::getTenantId, tenantId)
                .in(SysImSessionContentDO::getCreateTs, createTsSet).select(SysImSessionContentDO::getCreateTs).list();

        if (CollUtil.isEmpty(sysImSessionContentDOList)) {
            return;
        }

        // 已经存在数据库里面的，创建时间的时间戳
        Set<Long> existCreateTsSet =
            sysImSessionContentDOList.stream().map(SysImSessionContentDO::getCreateTs).collect(Collectors.toSet());

        Iterator<SysImSessionContentSendTextDTO> iterator = dto.getContentSet().iterator();

        while (iterator.hasNext() && existCreateTsSet.size() != 0) {

            SysImSessionContentSendTextDTO item = iterator.next();

            if (existCreateTsSet.contains(item.getCreateTs())) {

                iterator.remove();

                existCreateTsSet.remove(item.getCreateTs());

            }

        }

    }

    /**
     * 执行：发送内容
     */
    private void doSendTextUserSelf(SysImSessionContentSendTextListDTO dto, Long sessionId, Long userId,
        Long tenantId) {

        if (CollUtil.isEmpty(dto.getContentSet())) {
            return;
        }

        // 获取：该会话里面的所有用户主键 idSet
        List<SysImSessionRefUserDO> sysImSessionRefUserDOList =
            ChainWrappers.lambdaQueryChain(sysImSessionRefUserMapper).eq(SysImSessionRefUserDO::getSessionId, sessionId)
                .eq(BaseEntityNoIdSuper::getTenantId, tenantId).select(SysImSessionRefUserDO::getUserId).list();

        Set<Long> userIdSet =
            sysImSessionRefUserDOList.stream().map(SysImSessionRefUserDO::getUserId).collect(Collectors.toSet());

        List<SysImSessionContentDO> insertList = new ArrayList<>();

        int type = SysImSessionContentTypeEnum.TEXT.getCode();

        long currentTimeMillis = System.currentTimeMillis();

        // 创建时间不能低于该值
        long checkTs = currentTimeMillis - BaseConstant.YEAR_30_EXPIRE_TIME;

        // insertList
        handleSendTextUserSelfInsertList(dto, sessionId, userId, checkTs, type, userIdSet, insertList);

        saveBatch(insertList);

        // 更新：该会话，最后一次接受到消息的时间
        ChainWrappers.lambdaUpdateChain(sysImSessionMapper).eq(BaseEntity::getId, sessionId)
            .set(SysImSessionDO::getLastReceiveContentTs, currentTimeMillis).update();

    }

    /**
     * 处理：insertList
     */
    private static void handleSendTextUserSelfInsertList(SysImSessionContentSendTextListDTO dto, Long sessionId,
        Long userId, long checkTs, int type, Set<Long> userIdSet, List<SysImSessionContentDO> insertList) {

        for (SysImSessionContentSendTextDTO item : dto.getContentSet()) {

            if (StrUtil.isBlank(item.getContent())) {
                continue;
            }

            if (item.getCreateTs() == null || item.getCreateTs() < checkTs) {
                continue;
            }

            Date date = new Date(item.getCreateTs());

            SysImSessionContentDO sysImSessionContentDO = new SysImSessionContentDO();

            sysImSessionContentDO.setId(IdGeneratorUtil.nextId());

            sysImSessionContentDO.setSessionId(sessionId);

            sysImSessionContentDO.setContent(item.getContent());

            sysImSessionContentDO.setShowFlag(true);

            sysImSessionContentDO.setType(type);

            sysImSessionContentDO.setCreateId(userId);

            sysImSessionContentDO.setCreateTime(date);

            sysImSessionContentDO.setCreateTs(item.getCreateTs());

            if (CollUtil.isNotEmpty(userIdSet)) {

                SysWebSocketEventBO<SysImSessionContentDO> sysWebSocketEventBO = new SysWebSocketEventBO<>();

                sysWebSocketEventBO.setUserIdSet(userIdSet);

                WebSocketMessageDTO<SysImSessionContentDO> webSocketMessageDTO =
                    WebSocketMessageDTO.okData(BaseWebSocketUriEnum.SYS_IM_SESSION_CONTENT_SEND, sysImSessionContentDO);

                sysWebSocketEventBO.setWebSocketMessageDTO(webSocketMessageDTO);

                // 发送：webSocket事件
                KafkaUtil.sendSysWebSocketEventTopic(sysWebSocketEventBO);

            }

            sysImSessionContentDO.setEnableFlag(true);

            sysImSessionContentDO.setDelFlag(false);

            sysImSessionContentDO.setRemark("");

            sysImSessionContentDO.setUpdateId(userId);

            sysImSessionContentDO.setUpdateTime(date);

            insertList.add(sysImSessionContentDO);

        }

    }

    /**
     * 检查：sessionId是否合法
     */
    @NotNull
    public static Long checkSessionId(Long sessionId, boolean checkEnableFlag) {

        if (sessionId == null) {
            ApiResultVO.error(BaseBizCodeEnum.ILLEGAL_REQUEST, null);
        }

        if (UserUtil.getCurrentUserAdminFlag()) {
            return sessionId;
        }

        Long tenantId = UserUtil.getCurrentTenantIdDefault();

        Long userId = UserUtil.getCurrentUserId();

        // 检查：sessionId，是否属于当前租户
        boolean exists = ChainWrappers.lambdaQueryChain(sysImSessionMapper)
            .eq(BaseEntityNoIdSuper::getTenantId, tenantId).eq(BaseEntity::getId, sessionId).exists();

        if (!exists) {
            ApiResultVO.error(BaseBizCodeEnum.ILLEGAL_REQUEST, sessionId);
        }

        // 检查：用户是否在该会话中
        SysImSessionRefUserDO sysImSessionRefUserDO = ChainWrappers.lambdaQueryChain(sysImSessionRefUserMapper)
            .eq(BaseEntityNoIdSuper::getTenantId, tenantId).eq(SysImSessionRefUserDO::getSessionId, sessionId)
            .eq(SysImSessionRefUserDO::getUserId, userId).select(SysImSessionRefUserDO::getEnableFlag,
                SysImSessionRefUserDO::getPrivateChatRefUserId, SysImSessionRefUserDO::getBlockFlag)
            .one();

        if (sysImSessionRefUserDO == null) {
            ApiResultVO.error("操作失败：您已不在本次会话中", sessionId);
        }

        if (checkEnableFlag) {

            if (BooleanUtil.isFalse(sysImSessionRefUserDO.getEnableFlag())) {

                if (sysImSessionRefUserDO.getPrivateChatRefUserId() == BaseConstant.NEGATIVE_ONE) {

                    ApiResultVO.error("操作失败：会话已被删除，无法发送消息", sessionId);

                } else {

                    ApiResultVO.error("操作失败：您已被禁言", sessionId);

                }

            }

            if (BooleanUtil.isTrue(sysImSessionRefUserDO.getBlockFlag())) {

                ApiResultVO.error("操作失败：您已被拉黑，无法发送消息", sessionId);

            }

        }

        return sessionId;

    }

    /**
     * 查询会话内容-用户自我
     */
    @Override
    public Page<SysImSessionContentDO> scrollPageUserSelf(SysImSessionContentListDTO dto) {

        // 检查：sessionId是否合法
        Long sessionId = checkSessionId(dto.getSessionId(), false);

        Long id = dto.getId();

        boolean backwardFlag = BooleanUtil.isTrue(dto.getBackwardFlag());

        if (id == null) {

            if (backwardFlag) { // 最小的 id

                id = Long.MIN_VALUE;

            } else { // 最大的 id

                id = Long.MAX_VALUE;

            }

        }

        if (backwardFlag) { // 往后查询

            return lambdaQuery().gt(SysImSessionContentDO::getCreateTs, id)
                .eq(SysImSessionContentDO::getSessionId, sessionId).orderByAsc(SysImSessionContentDO::getCreateTs)
                .select(SysImSessionContentDO::getCreateTs, BaseEntityNoIdSuper::getCreateId,
                    SysImSessionContentDO::getId, SysImSessionContentDO::getType, SysImSessionContentDO::getContent,
                    SysImSessionContentDO::getShowFlag)
                .page(MyPageUtil.getScrollPage(dto.getPageSize()));

        } else { // 往前查询

            return lambdaQuery().lt(SysImSessionContentDO::getCreateTs, id)
                .eq(SysImSessionContentDO::getSessionId, sessionId).orderByDesc(SysImSessionContentDO::getCreateTs)
                .select(SysImSessionContentDO::getCreateTs, BaseEntityNoIdSuper::getCreateId,
                    SysImSessionContentDO::getId, SysImSessionContentDO::getType, SysImSessionContentDO::getContent,
                    SysImSessionContentDO::getShowFlag)
                .page(MyPageUtil.getScrollPage(dto.getPageSize()));

        }

    }

}
