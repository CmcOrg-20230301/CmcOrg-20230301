package com.cmcorg20230301.be.engine.pay.base.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.pay.base.mapper.SysPayMapper;
import com.cmcorg20230301.be.engine.pay.base.model.entity.SysPayDO;
import com.cmcorg20230301.be.engine.pay.base.model.enums.SysPayTradeStatusEnum;
import com.cmcorg20230301.be.engine.pay.base.service.SysPayService;
import com.cmcorg20230301.be.engine.pay.base.util.PayUtil;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoId;
import com.cmcorg20230301.be.engine.security.util.SysTenantUtil;
import com.cmcorg20230301.be.engine.security.util.UserUtil;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class SysPayServiceImpl extends ServiceImpl<SysPayMapper, SysPayDO> implements SysPayService {

    /**
     * 通过主键id，查看支付状态-本平台
     */
    @Override
    public SysPayTradeStatusEnum payTradeStatusById(NotNullId notNullId) {

        // 获取：用户关联的租户
        Set<Long> queryTenantIdSet = SysTenantUtil.getUserRefTenantIdSet();

        Long currentTenantIdDefault = UserUtil.getCurrentTenantIdDefault();

        if (!UserUtil.getCurrentTenantTopFlag(currentTenantIdDefault)) {

            // 添加：父级的租户主键 id
            Long parentTenantId = SysTenantUtil.getSysTenantDO(currentTenantIdDefault).getParentId();

            queryTenantIdSet.add(parentTenantId);

        }

        SysPayDO sysPayDO =
                lambdaQuery().eq(SysPayDO::getId, notNullId.getId()).in(BaseEntityNoId::getTenantId, queryTenantIdSet)
                        .select(SysPayDO::getStatus).one();

        if (sysPayDO == null) {
            return null;
        }

        return sysPayDO.getStatus();

    }

    /**
     * 通过主键id，查看支付状态-第三方支付平台
     */
    @Override
    public SysPayTradeStatusEnum payTradeStatusByIdOther(NotNullId notNullId) {

        // 查询：第三方的支付状态
        return PayUtil.query(notNullId.getId().toString(), true);

    }

}




