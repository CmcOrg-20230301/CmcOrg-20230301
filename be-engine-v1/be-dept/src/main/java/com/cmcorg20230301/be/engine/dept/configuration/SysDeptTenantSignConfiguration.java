package com.cmcorg20230301.be.engine.dept.configuration;

import com.cmcorg20230301.be.engine.dept.service.SysDeptService;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.be.engine.security.model.configuration.ITenantSignConfiguration;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntity;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoId;
import com.cmcorg20230301.be.engine.security.model.entity.SysDeptDO;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class SysDeptTenantSignConfiguration implements ITenantSignConfiguration {

    @Resource
    SysDeptService sysDeptService;

    @Override
    public void signUp(@NotNull Long tenantId) {

        // nothing

    }

    @Override
    public void delete(Set<Long> tenantIdSet) {

        List<SysDeptDO> sysDeptDOList =
                sysDeptService.lambdaQuery().in(BaseEntityNoId::getTenantId, tenantIdSet).select(BaseEntity::getId).list();

        Set<Long> deptIdSet = sysDeptDOList.stream().map(BaseEntity::getId).collect(Collectors.toSet());

        sysDeptService.deleteByIdSet(new NotEmptyIdSet(deptIdSet), false);

    }

}
