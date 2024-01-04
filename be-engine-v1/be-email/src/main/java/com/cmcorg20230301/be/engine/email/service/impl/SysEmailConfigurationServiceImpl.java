package com.cmcorg20230301.be.engine.email.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.BooleanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmcorg20230301.be.engine.email.mapper.SysEmailConfigurationMapper;
import com.cmcorg20230301.be.engine.email.model.dto.SysEmailConfigurationInsertOrUpdateDTO;
import com.cmcorg20230301.be.engine.email.model.entity.SysEmailConfigurationDO;
import com.cmcorg20230301.be.engine.email.service.SysEmailConfigurationService;
import com.cmcorg20230301.be.engine.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.be.engine.security.util.UserUtil;
import org.springframework.stereotype.Service;

@Service
public class SysEmailConfigurationServiceImpl extends ServiceImpl<SysEmailConfigurationMapper, SysEmailConfigurationDO>
        implements SysEmailConfigurationService {

    /**
     * 新增/修改
     */
    @Override
    public String insertOrUpdate(SysEmailConfigurationInsertOrUpdateDTO dto) {

        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        SysEmailConfigurationDO sysEmailConfigurationDO =
                lambdaQuery().eq(SysEmailConfigurationDO::getId, currentTenantIdDefault).one();

        boolean insertFlag = sysEmailConfigurationDO == null;

        if (insertFlag) {

            sysEmailConfigurationDO = new SysEmailConfigurationDO();

            sysEmailConfigurationDO.setId(currentTenantIdDefault);

            Assert.notBlank(dto.getPass(), "操作失败：第一次设置时，密码不能为空");

        }

        sysEmailConfigurationDO.setPort(dto.getPort());
        sysEmailConfigurationDO.setFromEmail(dto.getFromEmail());
        sysEmailConfigurationDO.setPass(dto.getPass());
        sysEmailConfigurationDO.setSslFlag(BooleanUtil.isTrue(dto.getSslFlag()));
        sysEmailConfigurationDO.setContentPre(dto.getContentPre());

        if (insertFlag) {

            save(sysEmailConfigurationDO);

        } else {

            updateById(sysEmailConfigurationDO);

        }

        return BaseBizCodeEnum.OK;

    }

    /**
     * 通过主键id，查看详情
     */
    @Override
    public SysEmailConfigurationDO infoById() {

        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        SysEmailConfigurationDO sysEmailConfigurationDO =
                lambdaQuery().eq(SysEmailConfigurationDO::getId, currentTenantIdDefault).one();

        if (sysEmailConfigurationDO != null) {
            sysEmailConfigurationDO.setPass(null); // 不返回密码
        }

        return sysEmailConfigurationDO;

    }

}
