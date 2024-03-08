package com.cmcorg20230301.be.engine.user.configuration;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.be.engine.security.model.configuration.ITenantSignConfiguration;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntity;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoId;
import com.cmcorg20230301.be.engine.security.model.entity.SysUserDO;
import com.cmcorg20230301.be.engine.user.service.SysUserService;

@Component
public class SysUserTenantSignConfiguration implements ITenantSignConfiguration {

    @Resource
    SysUserService sysUserService;

    @Override
    public void signUp(@NotNull Long tenantId) {

        // nothing

    }

    @Override
    public void delete(Set<Long> tenantIdSet) {

        List<SysUserDO> sysUserDOList =
            sysUserService.lambdaQuery().in(BaseEntityNoId::getTenantId, tenantIdSet).select(BaseEntity::getId).list();

        Set<Long> userIdSet = sysUserDOList.stream().map(BaseEntity::getId).collect(Collectors.toSet());

        sysUserService.deleteByIdSet(new NotEmptyIdSet(userIdSet));

    }

}
