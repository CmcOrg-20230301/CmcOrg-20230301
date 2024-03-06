package com.cmcorg20230301.be.engine.security.configuration.sign;

import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.cmcorg20230301.be.engine.security.mapper.SysUserInfoMapper;
import com.cmcorg20230301.be.engine.security.mapper.SysUserMapper;
import com.cmcorg20230301.be.engine.security.model.configuration.IUserSignConfiguration;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntity;
import com.cmcorg20230301.be.engine.security.model.entity.SysUserDO;
import com.cmcorg20230301.be.engine.security.model.entity.SysUserDeleteLogDO;
import com.cmcorg20230301.be.engine.security.model.entity.SysUserInfoDO;
import com.cmcorg20230301.be.engine.security.service.SysUserDeleteLogService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class SysSecurityUserSignConfiguration implements IUserSignConfiguration {

    @Resource
    SysUserDeleteLogService sysUserDeleteLogService;

    @Resource
    SysUserMapper sysUserMapper;

    @Resource
    SysUserInfoMapper sysUserInfoMapper;

    @Override
    public Object signUp(@NotNull Long userId, @NotNull Long tenantId) {

        return null;

    }

    @Override
    public void delete(Set<Long> userIdSet) {

        List<SysUserDO> sysUserDOList =
            ChainWrappers.lambdaQueryChain(sysUserMapper).in(BaseEntity::getId, userIdSet).list();

        List<SysUserInfoDO> sysUserInfoDOList =
            ChainWrappers.lambdaQueryChain(sysUserInfoMapper).in(SysUserInfoDO::getId, userIdSet)
                .list();

        Map<Long, SysUserInfoDO> sysUserInfoDoMap =
            sysUserInfoDOList.stream().collect(Collectors.toMap(SysUserInfoDO::getId, it -> it));

        List<SysUserDeleteLogDO> sysUserDeleteLogDOList = new ArrayList<>(userIdSet.size());

        for (SysUserDO item : sysUserDOList) {

            SysUserDeleteLogDO sysUserDeleteLogDO = new SysUserDeleteLogDO();

            SysUserInfoDO sysUserInfoDO = sysUserInfoDoMap.get(item.getId());

            sysUserDeleteLogDO.setId(item.getId());
            sysUserDeleteLogDO.setTenantId(item.getTenantId());
            sysUserDeleteLogDO.setParentId(item.getParentId());
            sysUserDeleteLogDO.setPassword(item.getPassword());
            sysUserDeleteLogDO.setEmail(item.getEmail());
            sysUserDeleteLogDO.setSignInName(item.getSignInName());
            sysUserDeleteLogDO.setPhone(item.getPhone());
            sysUserDeleteLogDO.setWxOpenId(item.getWxOpenId());
            sysUserDeleteLogDO.setWxAppId(item.getWxAppId());
            sysUserDeleteLogDO.setUuid(sysUserInfoDO.getUuid());
            sysUserDeleteLogDO.setNickname(sysUserInfoDO.getNickname());
            sysUserDeleteLogDO.setBio(sysUserInfoDO.getBio());
            sysUserDeleteLogDO.setAvatarFileId(sysUserInfoDO.getAvatarFileId());

            sysUserDeleteLogDOList.add(sysUserDeleteLogDO);

        }

        sysUserDeleteLogService.saveBatch(sysUserDeleteLogDOList);

    }

}
