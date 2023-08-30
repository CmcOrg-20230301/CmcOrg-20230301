package com.cmcorg20230301.be.engine.role.configuration;

import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.be.engine.role.service.SysRoleService;
import com.cmcorg20230301.be.engine.security.model.configuration.ITenantDeleteConfiguration;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntity;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoId;
import com.cmcorg20230301.be.engine.security.model.entity.SysRoleDO;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class SysRoleTenantDeleteConfiguration implements ITenantDeleteConfiguration {

    @Resource
    SysRoleService sysRoleService;

    @Override
    public void handle(Set<Long> tenantIdSet) {

        List<SysRoleDO> sysRoleDOList =
            sysRoleService.lambdaQuery().in(BaseEntityNoId::getTenantId, tenantIdSet).select(BaseEntity::getId).list();

        Set<Long> roleIdSet = sysRoleDOList.stream().map(BaseEntity::getId).collect(Collectors.toSet());

        sysRoleService.deleteByIdSet(new NotEmptyIdSet(roleIdSet));

    }

}
