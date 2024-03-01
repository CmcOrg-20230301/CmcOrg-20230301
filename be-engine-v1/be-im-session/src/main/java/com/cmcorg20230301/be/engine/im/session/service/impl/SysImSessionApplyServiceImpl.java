package com.cmcorg20230301.be.engine.im.session.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.cmcorg20230301.be.engine.im.session.mapper.SysImSessionApplyMapper;
import com.cmcorg20230301.be.engine.im.session.model.entity.SysImSessionApplyDO;
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
import com.cmcorg20230301.be.engine.security.util.UserUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

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
    public String privateChatApply(NotNullId notNullId) {

        Long tenantId = UserUtil.getCurrentTenantIdDefault();

        Long userId = UserUtil.getCurrentUserId();

        Long targetUserId = notNullId.getId();

        // 判断：目标用户主键 id是否合法
        boolean exists = ChainWrappers.lambdaQueryChain(sysUserMapper).eq(BaseEntity::getId, targetUserId).eq(BaseEntityNoIdSuper::getTenantId, tenantId).exists();

        if (exists) {
            ApiResultVO.error("操作失败：目标用户不存在", targetUserId);
        }

        return RedissonUtil.doLock(BaseRedisKeyEnum.PRE_SYS_IM_SESSION_APPLY_PRIVATE_CHAT.name() + userId + tenantId + targetUserId, () -> {

            SysImSessionApplyDO sysImSessionApplyDO = lambdaQuery().eq(SysImSessionApplyDO::getUserId, userId).eq(BaseEntityNoIdSuper::getTenantId, tenantId).eq(SysImSessionApplyDO::getPrivateChatApplyTargetUserId, targetUserId).one();

            if (sysImSessionApplyDO == null) { // 如果是：第一次申请

                sysImSessionApplyDO = new SysImSessionApplyDO();

                Long id = IdGeneratorUtil.nextId();

                sysImSessionApplyDO.setId(id);
                sysImSessionApplyDO.setUserId(userId);
                sysImSessionApplyDO.setSessionId(BaseConstant.NEGATIVE_ONE);
                sysImSessionApplyDO.setPrivateChatApplyTargetUserId(targetUserId);
                sysImSessionApplyDO.setEnableFlag(true);
                sysImSessionApplyDO.setDelFlag(false);
                sysImSessionApplyDO.setRemark("");
                sysImSessionApplyDO.setSessionType(SysImSessionTypeEnum.PRIVATE_CHAT.getCode());
                sysImSessionApplyDO.setStatus(SysImSessionApplyStatusEnum.APPLYING);
                sysImSessionApplyDO.setShowFlag(true);
                sysImSessionApplyDO.setRejectReason("");
                sysImSessionApplyDO.setTenantId(tenantId);

            } else { // 如果：已经存在申请

                if (SysImSessionApplyStatusEnum.BLOCKED.equals(sysImSessionApplyDO.getStatus())) {
                    ApiResultVO.error("操作失败：您已被拉黑，无法发送申请", targetUserId);
                }

            }

            return BaseBizCodeEnum.OK;

        });

    }

    /**
     * 私聊：同意添加
     */
    @Override
    public String privateChatAgree(NotEmptyIdSet notEmptyIdSet) {


        return null;

    }

}
