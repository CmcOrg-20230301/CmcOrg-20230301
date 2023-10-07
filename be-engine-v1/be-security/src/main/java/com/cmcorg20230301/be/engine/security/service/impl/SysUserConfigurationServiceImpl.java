package com.cmcorg20230301.be.engine.security.service.impl;

import cn.hutool.core.util.BooleanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmcorg20230301.be.engine.redisson.model.enums.BaseRedisKeyEnum;
import com.cmcorg20230301.be.engine.redisson.util.RedissonUtil;
import com.cmcorg20230301.be.engine.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.be.engine.security.mapper.SysSignConfigurationMapper;
import com.cmcorg20230301.be.engine.security.model.dto.SysUserConfigurationInsertOrUpdateDTO;
import com.cmcorg20230301.be.engine.security.model.entity.SysUserConfigurationDO;
import com.cmcorg20230301.be.engine.security.service.SysUserConfigurationService;
import com.cmcorg20230301.be.engine.security.util.SysTenantUtil;
import com.cmcorg20230301.be.engine.security.util.UserUtil;
import org.springframework.stereotype.Service;

@Service
public class SysUserConfigurationServiceImpl extends ServiceImpl<SysSignConfigurationMapper, SysUserConfigurationDO>
    implements SysUserConfigurationService {

    /**
     * 通过：租户 id，获取：用户登录注册相关配置
     */
    @Override
    public SysUserConfigurationDO getSysUserConfigurationDoByTenantId(Long tenantId) {

        tenantId = SysTenantUtil.getTenantId(tenantId);

        SysUserConfigurationDO sysUserConfigurationDO = lambdaQuery().eq(SysUserConfigurationDO::getId, tenantId).one();

        if (sysUserConfigurationDO == null) {

            Long finalTenantId = tenantId;

            sysUserConfigurationDO =
                RedissonUtil.doLock(BaseRedisKeyEnum.PRE_SIGN_CONFIGURATION.name() + tenantId, () -> {

                    // 这里需要再查询一次
                    SysUserConfigurationDO tempSysUserConfigurationDO =
                        lambdaQuery().eq(SysUserConfigurationDO::getId, finalTenantId).one();

                    if (tempSysUserConfigurationDO != null) {
                        return tempSysUserConfigurationDO;
                    }

                    tempSysUserConfigurationDO = new SysUserConfigurationDO();

                    tempSysUserConfigurationDO.setId(finalTenantId);
                    tempSysUserConfigurationDO.setSignInNameSignUpEnable(true);
                    tempSysUserConfigurationDO.setEmailSignUpEnable(true);
                    tempSysUserConfigurationDO.setPhoneSignUpEnable(true);

                    save(tempSysUserConfigurationDO); // 保存：用户登录注册相关配置

                    return tempSysUserConfigurationDO;

                });

        }

        return sysUserConfigurationDO;

    }

    /**
     * 新增/修改
     */
    @Override
    public String insertOrUpdate(SysUserConfigurationInsertOrUpdateDTO dto) {

        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        SysUserConfigurationDO sysUserConfigurationDO =
            lambdaQuery().eq(SysUserConfigurationDO::getId, currentTenantIdDefault).one();

        boolean insertFlag = sysUserConfigurationDO == null;

        if (insertFlag) {

            sysUserConfigurationDO = new SysUserConfigurationDO();

            sysUserConfigurationDO.setId(currentTenantIdDefault);

        }

        sysUserConfigurationDO.setSignInNameSignUpEnable(BooleanUtil.isTrue(dto.getSignInNameSignUpEnable()));
        sysUserConfigurationDO.setEmailSignUpEnable(BooleanUtil.isTrue(dto.getEmailSignUpEnable()));
        sysUserConfigurationDO.setPhoneSignUpEnable(BooleanUtil.isTrue(dto.getPhoneSignUpEnable()));

        if (insertFlag) {

            save(sysUserConfigurationDO);

        } else {

            updateById(sysUserConfigurationDO);

        }

        return BaseBizCodeEnum.OK;

    }

    /**
     * 通过主键id，查看详情
     */
    @Override
    public SysUserConfigurationDO infoById() {

        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        // 通过：租户 id，获取：用户登录注册相关配置
        return getSysUserConfigurationDoByTenantId(currentTenantIdDefault);

    }

}
