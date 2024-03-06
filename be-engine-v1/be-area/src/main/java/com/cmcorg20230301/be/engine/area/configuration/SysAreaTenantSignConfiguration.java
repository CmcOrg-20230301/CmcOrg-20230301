package com.cmcorg20230301.be.engine.area.configuration;

import com.cmcorg20230301.be.engine.area.service.SysAreaService;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.be.engine.security.model.configuration.ITenantSignConfiguration;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntity;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoId;
import com.cmcorg20230301.be.engine.security.model.entity.SysAreaDO;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class SysAreaTenantSignConfiguration implements ITenantSignConfiguration {

    @Resource
    SysAreaService sysAreaService;

    @Override
    public void signUp(@NotNull Long tenantId) {

        // nothing

    }

    @Override
    public void delete(Set<Long> tenantIdSet) {

        List<SysAreaDO> sysAreaDOList =
            sysAreaService.lambdaQuery().in(BaseEntityNoId::getTenantId, tenantIdSet)
                .select(BaseEntity::getId).list();

        Set<Long> areaIdSet = sysAreaDOList.stream().map(BaseEntity::getId)
            .collect(Collectors.toSet());

        sysAreaService.deleteByIdSet(new NotEmptyIdSet(areaIdSet), false);

    }

}
