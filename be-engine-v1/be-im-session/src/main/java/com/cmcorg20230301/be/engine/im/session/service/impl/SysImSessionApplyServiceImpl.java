package com.cmcorg20230301.be.engine.im.session.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmcorg20230301.be.engine.im.session.mapper.SysImSessionApplyMapper;
import com.cmcorg20230301.be.engine.im.session.model.entity.SysImSessionApplyDO;
import com.cmcorg20230301.be.engine.im.session.service.SysImSessionApplyService;
import com.cmcorg20230301.be.engine.im.session.service.SysImSessionRefUserService;
import com.cmcorg20230301.be.engine.im.session.service.SysImSessionService;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.redisson.model.enums.BaseRedisKeyEnum;
import com.cmcorg20230301.be.engine.redisson.util.RedissonUtil;
import com.cmcorg20230301.be.engine.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoIdSuper;
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

    /**
     * 私聊：申请添加
     */
    @Override
    public String privateChatApply(NotNullId notNullId) {

        Long tenantId = UserUtil.getCurrentTenantIdDefault();

        Long userId = UserUtil.getCurrentUserId();

        Long sessionId = notNullId.getId();

        // 判断：sessionId是否合法

        return RedissonUtil.doLock(BaseRedisKeyEnum.PRE_SYS_IM_SESSION_APPLY.name() + userId + tenantId, () -> {

            SysImSessionApplyDO sysImSessionApplyDO = lambdaQuery().eq(SysImSessionApplyDO::getUserId, userId).eq(BaseEntityNoIdSuper::getTenantId, tenantId).eq(SysImSessionApplyDO::getSessionId, sessionId).one();

            if (sysImSessionApplyDO == null) { // 如果是：第一次申请


            } else { // 如果：已经存在申请


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
