package com.cmcorg20230301.be.engine.area.configuration;

import com.cmcorg20230301.be.engine.area.model.entity.SysAreaDO;
import com.cmcorg20230301.be.engine.area.service.SysAreaService;
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
public class SysAreaTenantDeleteConfiguration implements ITenantDeleteConfiguration {

    @Resource
    SysAreaService sysAreaService;

    @Override
    public void handle(Set<Long> tenantIdSet) {

        List<SysAreaDO> sysAreaDOList =
            sysAreaService.lambdaQuery().in(BaseEntityNoId::getTenantId, tenantIdSet).select(BaseEntity::getId).list();

        Set<Long> areaIdSet = sysAreaDOList.stream().map(BaseEntity::getId).collect(Collectors.toSet());

        sysAreaService.deleteByIdSet(new NotEmptyIdSet(areaIdSet));

    }

}
