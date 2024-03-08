package com.cmcorg20230301.be.engine.post.configuration;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.be.engine.post.service.SysPostService;
import com.cmcorg20230301.be.engine.security.model.configuration.ITenantSignConfiguration;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntity;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoId;
import com.cmcorg20230301.be.engine.security.model.entity.SysPostDO;

@Component
public class SysPostTenantSignConfiguration implements ITenantSignConfiguration {

    @Resource
    SysPostService sysPostService;

    @Override
    public void signUp(@NotNull Long tenantId) {

        // nothing

    }

    @Override
    public void delete(Set<Long> tenantIdSet) {

        List<SysPostDO> sysPostDOList =
            sysPostService.lambdaQuery().in(BaseEntityNoId::getTenantId, tenantIdSet).select(BaseEntity::getId).list();

        Set<Long> postIdSet = sysPostDOList.stream().map(BaseEntity::getId).collect(Collectors.toSet());

        sysPostService.deleteByIdSet(new NotEmptyIdSet(postIdSet), false);

    }

}
