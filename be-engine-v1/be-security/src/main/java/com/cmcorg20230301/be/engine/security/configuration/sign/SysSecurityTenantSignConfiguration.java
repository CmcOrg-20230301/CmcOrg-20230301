package com.cmcorg20230301.be.engine.security.configuration.sign;

import com.cmcorg20230301.be.engine.model.model.constant.BaseConstant;
import com.cmcorg20230301.be.engine.security.model.configuration.ITenantSignConfiguration;
import com.cmcorg20230301.be.engine.security.model.entity.SysTenantDO;
import com.cmcorg20230301.be.engine.security.model.entity.SysUserDeleteLogDO;
import com.cmcorg20230301.be.engine.security.service.SysUserDeleteLogService;
import com.cmcorg20230301.be.engine.security.util.MyEntityUtil;
import com.cmcorg20230301.be.engine.security.util.SysTenantUtil;
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

    @Override
    public void signUp(@NotNull Long tenantId) {

        // nothing

    }

    @Override
    public void delete(Set<Long> tenantIdSet) {

        List<SysUserDeleteLogDO> sysUserDeleteLogDOList = new ArrayList<>(tenantIdSet.size());

        for (Long item : tenantIdSet) {

            SysTenantDO sysTenantDO = SysTenantUtil.getSysTenantCacheMap(false).get(item);

            if (sysTenantDO == null) {
                continue;
            }

            SysUserDeleteLogDO sysUserDeleteLogDO = new SysUserDeleteLogDO();

            sysUserDeleteLogDO.setId(BaseConstant.TENANT_USER_ID);
            sysUserDeleteLogDO.setTenantId(sysTenantDO.getId());
            sysUserDeleteLogDO.setParentId(sysTenantDO.getParentId());
            sysUserDeleteLogDO.setPassword(MyEntityUtil.getNotNullStr(null));
            sysUserDeleteLogDO.setEmail(MyEntityUtil.getNotNullStr(null));
            sysUserDeleteLogDO.setSignInName(MyEntityUtil.getNotNullStr(null));
            sysUserDeleteLogDO.setPhone(MyEntityUtil.getNotNullStr(null));
            sysUserDeleteLogDO.setWxOpenId(MyEntityUtil.getNotNullStr(null));
            sysUserDeleteLogDO.setWxAppId(MyEntityUtil.getNotNullStr(null));
            sysUserDeleteLogDO.setUuid(MyEntityUtil.getNotNullStr(null));
            sysUserDeleteLogDO.setNickname(sysTenantDO.getName());
            sysUserDeleteLogDO.setBio(MyEntityUtil.getNotNullStr(null));
            sysUserDeleteLogDO.setAvatarFileId(MyEntityUtil.getNotNullLong(null));

            sysUserDeleteLogDOList.add(sysUserDeleteLogDO);

        }

        sysUserDeleteLogService.saveBatch(sysUserDeleteLogDOList);

    }

}
