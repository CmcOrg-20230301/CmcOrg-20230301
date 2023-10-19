package com.cmcorg20230301.be.engine.security.configuration.sign;

import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.cmcorg20230301.be.engine.model.model.constant.BaseConstant;
import com.cmcorg20230301.be.engine.security.mapper.SysTenantMapper;
import com.cmcorg20230301.be.engine.security.model.configuration.ITenantSignConfiguration;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntity;
import com.cmcorg20230301.be.engine.security.model.entity.SysTenantDO;
import com.cmcorg20230301.be.engine.security.model.entity.SysUserDeleteLogDO;
import com.cmcorg20230301.be.engine.security.service.SysUserDeleteLogService;
import com.cmcorg20230301.be.engine.security.util.MyEntityUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class SysSecurityTenantSignConfiguration implements ITenantSignConfiguration {

    @Resource
    SysUserDeleteLogService sysUserDeleteLogService;

    @Resource
    SysTenantMapper sysTenantMapper;

    @Override
    public void signUp(@NotNull Long tenantId) {

        // nothing

    }

    @Override
    public void delete(Set<Long> tenantIdSet) {

        List<SysTenantDO> sysTenantDOList =
            ChainWrappers.lambdaQueryChain(sysTenantMapper).in(BaseEntity::getId, tenantIdSet).list();

        List<SysUserDeleteLogDO> sysUserDeleteLogDOList = new ArrayList<>(tenantIdSet.size());

        for (SysTenantDO item : sysTenantDOList) {

            SysUserDeleteLogDO sysUserDeleteLogDO = new SysUserDeleteLogDO();

            sysUserDeleteLogDO.setId(BaseConstant.TENANT_USER_ID);
            sysUserDeleteLogDO.setTenantId(item.getId());
            sysUserDeleteLogDO.setParentId(item.getParentId());
            sysUserDeleteLogDO.setPassword(MyEntityUtil.getNotNullStr(null));
            sysUserDeleteLogDO.setEmail(MyEntityUtil.getNotNullStr(null));
            sysUserDeleteLogDO.setSignInName(MyEntityUtil.getNotNullStr(null));
            sysUserDeleteLogDO.setPhone(MyEntityUtil.getNotNullStr(null));
            sysUserDeleteLogDO.setWxOpenId(MyEntityUtil.getNotNullStr(null));
            sysUserDeleteLogDO.setWxAppId(MyEntityUtil.getNotNullStr(null));
            sysUserDeleteLogDO.setUuid(MyEntityUtil.getNotNullStr(null));
            sysUserDeleteLogDO.setNickname(item.getName());
            sysUserDeleteLogDO.setBio(MyEntityUtil.getNotNullStr(null));
            sysUserDeleteLogDO.setAvatarFileId(MyEntityUtil.getNotNullLong(null));

            sysUserDeleteLogDOList.add(sysUserDeleteLogDO);

        }

        sysUserDeleteLogService.saveBatch(sysUserDeleteLogDOList);

    }

}
