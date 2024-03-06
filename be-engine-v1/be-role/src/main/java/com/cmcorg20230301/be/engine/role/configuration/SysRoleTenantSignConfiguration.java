package com.cmcorg20230301.be.engine.role.configuration;

import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.be.engine.role.service.SysRoleService;
import com.cmcorg20230301.be.engine.security.model.configuration.ITenantSignConfiguration;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntity;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoId;
import com.cmcorg20230301.be.engine.security.model.entity.SysRoleDO;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class SysRoleTenantSignConfiguration implements ITenantSignConfiguration {

    @Resource
    SysRoleService sysRoleService;

    @Override
    public void signUp(@NotNull Long tenantId) {

        // nothing

    }

    @Override
    public void delete(Set<Long> tenantIdSet) {

        List<SysRoleDO> sysRoleDOList =
            sysRoleService.lambdaQuery().in(BaseEntityNoId::getTenantId, tenantIdSet)
                .select(BaseEntity::getId).list();

        Set<Long> roleIdSet = sysRoleDOList.stream().map(BaseEntity::getId)
            .collect(Collectors.toSet());

        sysRoleService.deleteByIdSet(new NotEmptyIdSet(roleIdSet));

    }

}
