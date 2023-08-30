package com.cmcorg20230301.be.engine.user.configuration;

import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.be.engine.security.model.configuration.ITenantDeleteConfiguration;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntity;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoId;
import com.cmcorg20230301.be.engine.security.model.entity.SysUserDO;
import com.cmcorg20230301.be.engine.user.service.SysUserService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class SysUserTenantDeleteConfiguration implements ITenantDeleteConfiguration {

    @Resource
    SysUserService sysUserService;

    @Override
    public void handle(Set<Long> tenantIdSet) {

        List<SysUserDO> sysUserDOList =
            sysUserService.lambdaQuery().in(BaseEntityNoId::getTenantId, tenantIdSet).select(BaseEntity::getId).list();

        Set<Long> userIdSet = sysUserDOList.stream().map(BaseEntity::getId).collect(Collectors.toSet());

        sysUserService.deleteByIdSet(new NotEmptyIdSet(userIdSet));

    }

}
