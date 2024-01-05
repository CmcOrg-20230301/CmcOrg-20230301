package com.cmcorg20230301.be.engine.im.session.service.impl;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
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
import com.cmcorg20230301.be.engine.im.session.model.entity.SysImSessionRefUserDO;
import com.cmcorg20230301.be.engine.im.session.model.enums.SysImSessionContentTypeEnum;
import com.cmcorg20230301.be.engine.im.session.service.SysImSessionContentService;
import com.cmcorg20230301.be.engine.kafka.util.KafkaUtil;
import com.cmcorg20230301.be.engine.model.model.constant.BaseConstant;
import com.cmcorg20230301.be.engine.redisson.util.IdGeneratorUtil;
import com.cmcorg20230301.be.engine.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.be.engine.security.model.bo.SysWebSocketEventBO;
import com.cmcorg20230301.be.engine.security.model.dto.WebSocketMessageDTO;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntity;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoIdSuper;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.security.util.MyPageUtil;
import com.cmcorg20230301.be.engine.security.util.MyThreadUtil;
import com.cmcorg20230301.be.engine.security.util.UserUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
     * 发送内容
     */
    @Override
    public String sendTextUserSelf(SysImSessionContentSendTextListDTO dto) {

        // 检查：sessionId是否合法
        Long sessionId = checkSessionId(dto.getSessionId(), true);

        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        // 获取：该会话里面的所有用户主键 idSet
        List<SysImSessionRefUserDO> sysImSessionRefUserDOList = ChainWrappers.lambdaQueryChain(sysImSessionRefUserMapper).eq(SysImSessionRefUserDO::getSessionId, sessionId).eq(BaseEntityNoIdSuper::getTenantId, currentTenantIdDefault).select(SysImSessionRefUserDO::getUserId).list();

        Set<Long> userIdSet = sysImSessionRefUserDOList.stream().map(SysImSessionRefUserDO::getUserId).collect(Collectors.toSet());

        List<SysImSessionContentDO> insertList = new ArrayList<>();

        int type = SysImSessionContentTypeEnum.TEXT.getCode();

        // 创建时间不能低于该值
        long checkTs = System.currentTimeMillis() - BaseConstant.YEAR_30_EXPIRE_TIME;

        for (SysImSessionContentSendTextDTO item : dto.getContentList()) {

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

            sysImSessionContentDO.setEnableFlag(true);

            sysImSessionContentDO.setDelFlag(false);

            sysImSessionContentDO.setRemark("");

            sysImSessionContentDO.setCreateTime(date);

            sysImSessionContentDO.setUpdateTime(date);

            sysImSessionContentDO.setCreateTs(item.getCreateTs());

            MyThreadUtil.execute(() -> {

                SysWebSocketEventBO<SysImSessionContentDO> sysWebSocketEventBO = new SysWebSocketEventBO<>();

                sysWebSocketEventBO.setUserIdSet(userIdSet);

                WebSocketMessageDTO<SysImSessionContentDO> webSocketMessageDTO = WebSocketMessageDTO.okData("/sys/im/session/content/send", sysImSessionContentDO);

                sysWebSocketEventBO.setWebSocketMessageDTO(webSocketMessageDTO);

                // 发送：webSocket事件
                KafkaUtil.sendSysWebSocketEventTopic(sysWebSocketEventBO);

            });

            insertList.add(sysImSessionContentDO);

        }

        saveBatch(insertList);

        return BaseBizCodeEnum.OK;

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
        boolean exists = ChainWrappers.lambdaQueryChain(sysImSessionMapper).eq(BaseEntityNoIdSuper::getTenantId, tenantId).eq(BaseEntity::getId, sessionId).exists();

        if (!exists) {
            ApiResultVO.error(BaseBizCodeEnum.ILLEGAL_REQUEST, sessionId);
        }

        // 检查：用户是否在该会话中
        SysImSessionRefUserDO sysImSessionRefUserDO = ChainWrappers.lambdaQueryChain(sysImSessionRefUserMapper).eq(BaseEntityNoIdSuper::getTenantId, tenantId).eq(SysImSessionRefUserDO::getSessionId, sessionId).eq(SysImSessionRefUserDO::getUserId, userId).select(SysImSessionRefUserDO::getEnableFlag).one();

        if (sysImSessionRefUserDO == null) {
            ApiResultVO.error("操作失败：您已不在本次会话中", sessionId);
        }

        if (checkEnableFlag) {

            if (BooleanUtil.isFalse(sysImSessionRefUserDO.getEnableFlag())) {
                ApiResultVO.error("操作失败：您已被禁言", sessionId);
            }

        }

        return sessionId;

    }

    /**
     * 查询会话内容
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

            return lambdaQuery().gt(BaseEntity::getId, id).eq(SysImSessionContentDO::getSessionId, sessionId).page(MyPageUtil.getScrollPage(dto.getPageSize()));

        } else { // 往前查询

            return lambdaQuery().lt(BaseEntity::getId, id).eq(SysImSessionContentDO::getSessionId, sessionId).page(MyPageUtil.getScrollPage(dto.getPageSize()));

        }

    }

}
