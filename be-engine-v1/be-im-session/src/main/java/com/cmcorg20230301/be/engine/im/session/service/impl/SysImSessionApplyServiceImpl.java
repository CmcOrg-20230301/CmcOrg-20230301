package com.cmcorg20230301.be.engine.im.session.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.cmcorg20230301.be.engine.im.session.mapper.SysImSessionApplyMapper;
import com.cmcorg20230301.be.engine.im.session.model.dto.SysImSessionApplyPrivateChatApplyDTO;
import com.cmcorg20230301.be.engine.im.session.model.dto.SysImSessionApplyPrivateChatRejectDTO;
import com.cmcorg20230301.be.engine.im.session.model.dto.SysImSessionInsertOrUpdateDTO;
import com.cmcorg20230301.be.engine.im.session.model.entity.SysImSessionApplyDO;
import com.cmcorg20230301.be.engine.im.session.model.entity.SysImSessionRefUserDO;
import com.cmcorg20230301.be.engine.im.session.model.enums.SysImSessionApplyStatusEnum;
import com.cmcorg20230301.be.engine.im.session.model.enums.SysImSessionTypeEnum;
import com.cmcorg20230301.be.engine.im.session.service.SysImSessionApplyService;
import com.cmcorg20230301.be.engine.im.session.service.SysImSessionRefUserService;
import com.cmcorg20230301.be.engine.im.session.service.SysImSessionService;
import com.cmcorg20230301.be.engine.model.model.constant.BaseConstant;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullIdAndNotEmptyLongSet;
import com.cmcorg20230301.be.engine.redisson.model.enums.BaseRedisKeyEnum;
import com.cmcorg20230301.be.engine.redisson.util.IdGeneratorUtil;
import com.cmcorg20230301.be.engine.redisson.util.RedissonUtil;
import com.cmcorg20230301.be.engine.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.be.engine.security.mapper.SysUserMapper;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntity;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoIdSuper;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.security.util.MyEntityUtil;
import com.cmcorg20230301.be.engine.security.util.UserUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class SysImSessionApplyServiceImpl extends ServiceImpl<SysImSessionApplyMapper, SysImSessionApplyDO>
        implements SysImSessionApplyService {

    @Resource
    SysImSessionService sysImSessionService;

    @Resource
    SysImSessionRefUserService sysImSessionRefUserService;

    @Resource
    SysUserMapper sysUserMapper;

    /**
     * 私聊：申请添加
     */
    @Override
    public String privateChatApply(SysImSessionApplyPrivateChatApplyDTO dto) {

        Long tenantId = UserUtil.getCurrentTenantIdDefault();

        Long userId = UserUtil.getCurrentUserId();

        Long targetUserId = dto.getId();

        // 判断：目标用户主键 id是否合法
        boolean exists = ChainWrappers.lambdaQueryChain(sysUserMapper).eq(BaseEntity::getId, targetUserId).eq(BaseEntityNoIdSuper::getTenantId, tenantId).exists();

        if (exists) {
            ApiResultVO.error("操作失败：目标用户不存在", targetUserId);
        }

        String key = getPrivateChatApplyKey(userId, targetUserId);

        return RedissonUtil.doLock(key, () -> {

            SysImSessionApplyDO sysImSessionApplyDO = lambdaQuery().eq(SysImSessionApplyDO::getUserId, userId).eq(BaseEntityNoIdSuper::getTenantId, tenantId).eq(SysImSessionApplyDO::getPrivateChatApplyTargetUserId, targetUserId).select(SysImSessionApplyDO::getId, SysImSessionApplyDO::getStatus).one();

            if (sysImSessionApplyDO == null) { // 如果是：第一次申请

                sysImSessionApplyDO = new SysImSessionApplyDO();

                Long id = IdGeneratorUtil.nextId();

                sysImSessionApplyDO.setId(id);
                sysImSessionApplyDO.setUserId(userId);
                sysImSessionApplyDO.setSessionId(BaseConstant.NEGATIVE_ONE);
                sysImSessionApplyDO.setPrivateChatApplyTargetUserId(targetUserId);
                sysImSessionApplyDO.setEnableFlag(true);
                sysImSessionApplyDO.setDelFlag(false);
                sysImSessionApplyDO.setRemark(MyEntityUtil.getNotNullStr(dto.getApplyReason()));
                sysImSessionApplyDO.setSessionType(SysImSessionTypeEnum.PRIVATE_CHAT.getCode());
                sysImSessionApplyDO.setStatus(SysImSessionApplyStatusEnum.APPLYING);
                sysImSessionApplyDO.setShowFlag(true);
                sysImSessionApplyDO.setRejectReason("");
                sysImSessionApplyDO.setTenantId(tenantId);

                sysImSessionApplyDO.setBlockPreStatus(SysImSessionApplyStatusEnum.APPLYING);

                save(sysImSessionApplyDO); // 保存到数据库

            } else { // 如果：已经存在申请

                if (SysImSessionApplyStatusEnum.BLOCKED.equals(sysImSessionApplyDO.getStatus())) {
                    ApiResultVO.error("操作失败：您已被拉黑，无法发送申请", targetUserId);
                }

                if (SysImSessionApplyStatusEnum.PASSED.equals(sysImSessionApplyDO.getStatus())) {
                    ApiResultVO.error("操作失败：对方已经是您的好友，无法发送申请", targetUserId);
                }

                sysImSessionApplyDO.setUpdateTime(null);

                sysImSessionApplyDO.setShowFlag(true);

                sysImSessionApplyDO.setStatus(SysImSessionApplyStatusEnum.APPLYING);

                sysImSessionApplyDO.setRemark(MyEntityUtil.getNotNullStr(dto.getApplyReason()));

                updateById(sysImSessionApplyDO);

            }

            return BaseBizCodeEnum.OK;

        });

    }

    /**
     * 获取：私聊的锁 key
     * <p>
     * userId 和 targetUserId 可以任意传递，原因：因为小的值会在前面
     */
    public static String getPrivateChatApplyKey(Long userId, Long targetUserId) {

        return BaseRedisKeyEnum.PRE_SYS_IM_SESSION_APPLY_PRIVATE_CHAT.name() + Math.min(userId, targetUserId) + Math.max(userId, targetUserId);

    }

    /**
     * 私聊：同意添加
     */
    @Override
    @DSTransactional
    public String privateChatAgree(NotEmptyIdSet notEmptyIdSet) {

        Long tenantId = UserUtil.getCurrentTenantIdDefault();

        Long userId = UserUtil.getCurrentUserId();

        Set<Long> applyUserIdSet = notEmptyIdSet.getIdSet();

        Long count = lambdaQuery().eq(SysImSessionApplyDO::getPrivateChatApplyTargetUserId, userId).eq(BaseEntityNoIdSuper::getTenantId, tenantId).in(SysImSessionApplyDO::getUserId, applyUserIdSet).count();

        if (count != applyUserIdSet.size()) {
            ApiResultVO.error(BaseBizCodeEnum.ILLEGAL_REQUEST);
        }

        Set<String> ketSet = new HashSet<>();

        for (Long item : applyUserIdSet) {

            ketSet.add(getPrivateChatApplyKey(userId, item));

        }

        Date date = new Date();

        return RedissonUtil.doMultiLock("", ketSet, () -> {

            // 处理
            return handlePrivateChatAgree(userId, tenantId, applyUserIdSet, date);

        });

    }

    /**
     * 处理
     */
    @NotNull
    private String handlePrivateChatAgree(Long userId, Long tenantId, Set<Long> applyUserIdSet, Date date) {

        List<SysImSessionApplyDO> sysImSessionApplyDOList = lambdaQuery().eq(SysImSessionApplyDO::getPrivateChatApplyTargetUserId, userId).eq(BaseEntityNoIdSuper::getTenantId, tenantId).in(SysImSessionApplyDO::getUserId, applyUserIdSet).eq(SysImSessionApplyDO::getStatus, SysImSessionApplyStatusEnum.APPLYING).select(SysImSessionApplyDO::getId, SysImSessionApplyDO::getSessionId, SysImSessionApplyDO::getUserId).list();

        if (CollUtil.isEmpty(sysImSessionApplyDOList)) {
            ApiResultVO.error("操作失败：申请状态已发生改变，请刷新重试", applyUserIdSet);
        }

        // 会话主键 id集合，目的：让会话关联的用户恢复可用状态
        Set<Long> enabelSessionIdSet = new HashSet<>();

        Set<Long> userIdSet = new HashSet<>();

        for (SysImSessionApplyDO item : sysImSessionApplyDOList) {

            item.setShowFlag(true);
            item.setUpdateTime(date);
            item.setStatus(SysImSessionApplyStatusEnum.PASSED);

            userIdSet.add(item.getUserId());

            if (item.getSessionId() == -1) {

                SysImSessionInsertOrUpdateDTO sysImSessionInsertOrUpdateDTO = new SysImSessionInsertOrUpdateDTO();

                sysImSessionInsertOrUpdateDTO.setType(SysImSessionTypeEnum.PRIVATE_CHAT.getCode());

                // 新建一个会话
                Long sessionId = sysImSessionService.insertOrUpdate(sysImSessionInsertOrUpdateDTO);

                NotNullIdAndNotEmptyLongSet notNullIdAndNotEmptyLongSet = new NotNullIdAndNotEmptyLongSet();

                notNullIdAndNotEmptyLongSet.setValueSet(CollUtil.newHashSet(userId, item.getUserId()));

                notNullIdAndNotEmptyLongSet.setId(sessionId);

                // 加入该会话
                sysImSessionRefUserService.joinUserIdSet(notNullIdAndNotEmptyLongSet);

                item.setSessionId(sessionId); // 设置：关联的 sessionId

            } else {

                enabelSessionIdSet.add(item.getSessionId());

            }

        }

        // 如果：也给申请人发送了好友申请，则处理该数据也为通过
        lambdaUpdate().eq(SysImSessionApplyDO::getUserId, userId).in(SysImSessionApplyDO::getPrivateChatApplyTargetUserId, userIdSet).eq(SysImSessionApplyDO::getStatus, SysImSessionApplyStatusEnum.APPLYING).set(SysImSessionApplyDO::getShowFlag, true).eq(SysImSessionApplyDO::getStatus, SysImSessionApplyStatusEnum.PASSED).set(BaseEntityNoIdSuper::getUpdateTime, date).update();

        // 更新为：已通过
        updateBatchById(sysImSessionApplyDOList);

        if (CollUtil.isNotEmpty(enabelSessionIdSet)) {

            // 批量：让会话关联的用户恢复可用状态
            sysImSessionRefUserService.lambdaUpdate().in(SysImSessionRefUserDO::getSessionId, enabelSessionIdSet).set(SysImSessionRefUserDO::getShowFlag, true).set(SysImSessionRefUserDO::getEnableFlag, true).set(SysImSessionRefUserDO::getBlockFlag, false).set(BaseEntityNoIdSuper::getUpdateTime, date).update();

        }

        return BaseBizCodeEnum.OK;

    }

    /**
     * 私聊：拒绝添加
     */
    @Override
    public String privateChatReject(SysImSessionApplyPrivateChatRejectDTO dto) {

        Long tenantId = UserUtil.getCurrentTenantIdDefault();

        Long userId = UserUtil.getCurrentUserId();

        Long applyUserId = dto.getId();

        String key = getPrivateChatApplyKey(userId, applyUserId);

        String rejectReason = MyEntityUtil.getNotNullStr(dto.getRejectReason());

        return RedissonUtil.doLock(key, () -> {

            boolean update = lambdaUpdate().eq(SysImSessionApplyDO::getPrivateChatApplyTargetUserId, userId).eq(BaseEntityNoIdSuper::getTenantId, tenantId).eq(SysImSessionApplyDO::getUserId, applyUserId).eq(SysImSessionApplyDO::getStatus, SysImSessionApplyStatusEnum.APPLYING).set(SysImSessionApplyDO::getShowFlag, true).set(SysImSessionApplyDO::getStatus, SysImSessionApplyStatusEnum.REJECTED).set(BaseEntityNoIdSuper::getUpdateTime, new Date()).set(SysImSessionApplyDO::getRejectReason, rejectReason).update();

            if (!update) {
                ApiResultVO.error("操作失败：申请状态已发生改变，请刷新重试", applyUserId);
            }

            return BaseBizCodeEnum.OK;

        });

    }

    /**
     * 私聊：拉黑
     */
    @Override
    public String privateChatBlock(NotNullId notNullId) {

        Long tenantId = UserUtil.getCurrentTenantIdDefault();

        Long userId = UserUtil.getCurrentUserId();

        Long applyUserId = notNullId.getId();

        String key = getPrivateChatApplyKey(userId, applyUserId);

        return RedissonUtil.doLock(key, () -> {

            SysImSessionApplyDO sysImSessionApplyDO = lambdaQuery().eq(SysImSessionApplyDO::getPrivateChatApplyTargetUserId, userId).eq(BaseEntityNoIdSuper::getTenantId, tenantId).eq(SysImSessionApplyDO::getUserId, applyUserId).select(SysImSessionApplyDO::getId, SysImSessionApplyDO::getStatus).one();

            if (sysImSessionApplyDO == null) {
                return BaseBizCodeEnum.OK;
            }

            lambdaUpdate().eq(SysImSessionApplyDO::getId, sysImSessionApplyDO.getId()).set(SysImSessionApplyDO::getStatus, SysImSessionApplyStatusEnum.BLOCKED).set(BaseEntityNoIdSuper::getUpdateTime, new Date()).set(SysImSessionApplyDO::getBlockPreStatus, sysImSessionApplyDO.getStatus()).update();

            return BaseBizCodeEnum.OK;

        });

    }

    /**
     * 私聊：拉黑取消
     */
    @Override
    public String privateChatBlockCancel(NotEmptyIdSet notEmptyIdSet) {

        Long tenantId = UserUtil.getCurrentTenantIdDefault();

        Long userId = UserUtil.getCurrentUserId();

        Set<Long> applyUserIdSet = notEmptyIdSet.getIdSet();

        Set<String> ketSet = new HashSet<>();

        for (Long item : applyUserIdSet) {

            ketSet.add(getPrivateChatApplyKey(userId, item));

        }

        Date date = new Date();

        return RedissonUtil.doMultiLock("", ketSet, () -> {


            return BaseBizCodeEnum.OK;

        });

    }

    /**
     * 私聊：申请取消
     */
    @Override
    public String privateChatApplyCancel(NotNullId notNullId) {
        return null;
    }

    /**
     * 私聊：申请隐藏
     */
    @Override
    public String privateChatApplyHidden(NotNullId notNullId) {
        return null;
    }

    /**
     * 私聊删除
     */
    @Override
    public String privateChatDelete(NotNullId notNullId) {
        return null;
    }

}
