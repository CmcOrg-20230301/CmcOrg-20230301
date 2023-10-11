package com.cmcorg20230301.be.engine.wallet.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoId;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoIdFather;
import com.cmcorg20230301.be.engine.security.util.SysTenantUtil;
import com.cmcorg20230301.be.engine.wallet.model.dto.SysUserBankCardInsertOrUpdateUserSelfDTO;
import com.cmcorg20230301.be.engine.wallet.model.dto.SysUserBankCardPageDTO;
import com.cmcorg20230301.be.engine.wallet.model.entity.SysUserBankCardDO;
import com.cmcorg20230301.be.engine.wallet.service.SysTenantBankCardService;
import com.cmcorg20230301.be.engine.wallet.service.SysUserBankCardService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Set;

@Service
public class SysTenantBankCardServiceImpl implements SysTenantBankCardService {

    @Resource
    SysUserBankCardService sysUserBankCardService;

    /**
     * 新增/修改-租户
     */
    @Override
    public String insertOrUpdateTenant(SysUserBankCardInsertOrUpdateUserSelfDTO dto) {

        // 执行
        return sysUserBankCardService.doInsertOrUpdate(dto, true);

    }

    /**
     * 分页排序查询
     */
    @Override
    public Page<SysUserBankCardDO> myPage(SysUserBankCardPageDTO dto) {

        // 执行
        return sysUserBankCardService.doMyPage(dto, true);

    }

    /**
     * 通过租户主键id，查看详情
     */
    @Override
    public SysUserBankCardDO infoById(NotNullId notNullId) {

        // 获取：用户关联的租户
        Set<Long> queryTenantIdSet = SysTenantUtil.getUserRefTenantIdSet();

        return sysUserBankCardService.lambdaQuery().eq(BaseEntityNoIdFather::getTenantId, notNullId.getId())
            .in(BaseEntityNoId::getTenantId, queryTenantIdSet).one();

    }

}
