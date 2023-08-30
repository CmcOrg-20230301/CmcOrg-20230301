package com.cmcorg20230301.be.engine.dept.configuration;

import com.cmcorg20230301.be.engine.dept.model.entity.SysDeptDO;
import com.cmcorg20230301.be.engine.dept.service.SysDeptService;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.be.engine.security.model.configuration.ITenantDeleteConfiguration;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntity;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoId;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class SysDeptTenantDeleteConfiguration implements ITenantDeleteConfiguration {

    @Resource
    SysDeptService sysDeptService;

    @Override
    public void handle(Set<Long> tenantIdSet) {

        List<SysDeptDO> sysDeptDOList =
            sysDeptService.lambdaQuery().in(BaseEntityNoId::getTenantId, tenantIdSet).select(BaseEntity::getId).list();

        Set<Long> deptIdSet = sysDeptDOList.stream().map(BaseEntity::getId).collect(Collectors.toSet());

        sysDeptService.deleteByIdSet(new NotEmptyIdSet(deptIdSet));

    }

}
