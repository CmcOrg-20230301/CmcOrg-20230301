package com.cmcorg20230301.be.engine.im.session.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.cmcorg20230301.be.engine.im.session.mapper.SysImSessionApplyMapper;
import com.cmcorg20230301.be.engine.im.session.model.dto.SysImSessionApplyPrivateChatApplyDTO;
import com.cmcorg20230301.be.engine.im.session.model.dto.SysImSessionApplyPrivateChatRejectDTO;
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
import java.util.stream.Collectors;

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

        Set<Long> applyIdSet = notEmptyIdSet.getIdSet();

        Long count = lambdaQuery().eq(SysImSessionApplyDO::getPrivateChatApplyTargetUserId, userId).eq(BaseEntityNoIdSuper::getTenantId, tenantId).in(SysImSessionApplyDO::getId, applyIdSet).count();

        if (count != applyIdSet.size()) {
            ApiResultVO.error(BaseBizCodeEnum.ILLEGAL_REQUEST);
        }

        Set<String> ketSet = new HashSet<>();

        for (Long item : applyIdSet) {

            ketSet.add(getPrivateChatApplyKey(userId, item));

        }

        Date date = new Date();

        return RedissonUtil.doMultiLock("", ketSet, () -> {

            // 处理
            return handlePrivateChatAgree(userId, tenantId, applyIdSet, date);

        });

    }

    /**
     * 处理
     */
    @NotNull
    private String handlePrivateChatAgree(Long userId, Long tenantId, Set<Long> applyIdSet, Date date) {

        List<SysImSessionApplyDO> sysImSessionApplyDOList = lambdaQuery().eq(SysImSessionApplyDO::getPrivateChatApplyTargetUserId, userId).eq(BaseEntityNoIdSuper::getTenantId, tenantId).in(SysImSessionApplyDO::getId, applyIdSet).eq(SysImSessionApplyDO::getStatus, SysImSessionApplyStatusEnum.APPLYING).select(SysImSessionApplyDO::getId).list();

        if (CollUtil.isEmpty(sysImSessionApplyDOList)) {
            return BaseBizCodeEnum.OK;
        }

        // 重新赋值：会话申请主键 id集合
        applyIdSet = sysImSessionApplyDOList.stream().map(SysImSessionApplyDO::getId).collect(Collectors.toSet());

        // 更新为：已通过，备注：如果：我也给申请人发送了好友申请，则不处理该数据
        lambdaUpdate().in(SysImSessionApplyDO::getId, applyIdSet).set(SysImSessionApplyDO::getStatus, SysImSessionApplyStatusEnum.PASSED).set(BaseEntityNoIdSuper::getUpdateTime, date).set(SysImSessionApplyDO::getShowFlag, true).update();

        // 会话关联用户主键 id集合，目的：让会话恢复可用状态
        Set<Long> enabelSessionRefUserIdSet = new HashSet<>();

        // 遍历处理：申请：让会话恢复可用状态/新建会话
        for (Long item : applyIdSet) {

            List<SysImSessionRefUserDO> sysImSessionRefUserDOList = sysImSessionRefUserService.lambdaQuery().and(i -> i.eq(SysImSessionRefUserDO::getPrivateChatRefUserId, userId).or().eq(SysImSessionRefUserDO::getUserId, userId)).select(SysImSessionRefUserDO::getId).list();

            if (CollUtil.isEmpty(sysImSessionRefUserDOList)) {

                // 新建一个会话

                // 加入该会话

            } else {

                for (SysImSessionRefUserDO subItem : sysImSessionRefUserDOList) {
                    enabelSessionRefUserIdSet.add(subItem.getId());
                }

            }

        }

        if (CollUtil.isNotEmpty(enabelSessionRefUserIdSet)) {

            // 批量：让会话恢复可用状态
            sysImSessionRefUserService.lambdaUpdate().in(SysImSessionRefUserDO::getId, enabelSessionRefUserIdSet).set(SysImSessionRefUserDO::getShowFlag, true).set(SysImSessionRefUserDO::getEnableFlag, true).set(SysImSessionRefUserDO::getBlockFlag, true).set(BaseEntityNoIdSuper::getUpdateTime, date).update();

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

        return BaseBizCodeEnum.OK;

    }

    /**
     * 私聊：拉黑
     */
    @Override
    public String privateChatBlock(NotNullId notNullId) {

        return BaseBizCodeEnum.OK;

    }

    /**
     * 私聊：拉黑取消
     */
    @Override
    public String privateChatBlockCancel(NotEmptyIdSet notEmptyIdSet) {

        return BaseBizCodeEnum.OK;

    }

}
