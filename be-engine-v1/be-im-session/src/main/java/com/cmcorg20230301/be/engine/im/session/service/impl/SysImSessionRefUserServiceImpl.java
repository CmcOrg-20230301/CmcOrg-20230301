package com.cmcorg20230301.be.engine.im.session.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.cmcorg20230301.be.engine.im.session.mapper.SysImSessionMapper;
import com.cmcorg20230301.be.engine.im.session.mapper.SysImSessionRefUserMapper;
import com.cmcorg20230301.be.engine.im.session.model.entity.SysImSessionDO;
import com.cmcorg20230301.be.engine.im.session.model.entity.SysImSessionRefUserDO;
import com.cmcorg20230301.be.engine.im.session.service.SysImSessionRefUserService;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullIdAndNotEmptyLongSet;
import com.cmcorg20230301.be.engine.redisson.util.RedissonUtil;
import com.cmcorg20230301.be.engine.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntity;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoIdSuper;
import com.cmcorg20230301.be.engine.security.model.entity.SysUserInfoDO;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.security.util.SysTenantUtil;
import com.cmcorg20230301.be.engine.security.util.SysUserInfoUtil;
import com.cmcorg20230301.be.engine.security.util.UserUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SysImSessionRefUserServiceImpl extends ServiceImpl<SysImSessionRefUserMapper, SysImSessionRefUserDO>
        implements SysImSessionRefUserService {

    @Resource
    SysImSessionMapper sysImSessionMapper;

    /**
     * 加入新用户
     */
    @Override
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

        return RedissonUtil.doMultiLock(sessionId.toString(), userIdSet, () -> {

            // 查询出：已经存在该会话的用户数据
            List<SysImSessionRefUserDO> sysImSessionRefUserDOList = lambdaQuery().in(SysImSessionRefUserDO::getUserId, userIdSet).eq(BaseEntityNoIdSuper::getTenantId, tenantId).eq(SysImSessionRefUserDO::getSessionId, sessionId).select(SysImSessionRefUserDO::getUserId).list();

            if (CollUtil.isNotEmpty(sysImSessionRefUserDOList)) {

                Set<Long> existUserIdSet = sysImSessionRefUserDOList.stream().map(SysImSessionRefUserDO::getUserId).collect(Collectors.toSet());

                userIdSet.removeAll(existUserIdSet);

            }

            if (CollUtil.isEmpty(userIdSet)) {

                return BaseBizCodeEnum.OK;

            }

            Map<Long, SysUserInfoDO> userInfoDoMap = SysUserInfoUtil.getUserInfoDoMap(userIdSet);

            List<SysImSessionRefUserDO> insertList = new ArrayList<>();

            for (Long item : userIdSet) {

                SysUserInfoDO sysUserInfoDO = userInfoDoMap.get(item);

                if (sysUserInfoDO == null) {
                    continue;
                }

                SysImSessionRefUserDO sysImSessionRefUserDO = new SysImSessionRefUserDO();

                sysImSessionRefUserDO.setUserId(item);

                sysImSessionRefUserDO.setSessionId(sessionId);

                sysImSessionRefUserDO.setEnableFlag(true);

                sysImSessionRefUserDO.setDelFlag(false);

                sysImSessionRefUserDO.setRemark("");

                sysImSessionRefUserDO.setSessionType(sysImSessionDO.getType()); // 冗余字段

                sysImSessionRefUserDO.setSessionNickname(sysUserInfoDO.getNickname());

                insertList.add(sysImSessionRefUserDO);

            }

            saveBatch(insertList);

            return BaseBizCodeEnum.OK;

        });


    }

}
