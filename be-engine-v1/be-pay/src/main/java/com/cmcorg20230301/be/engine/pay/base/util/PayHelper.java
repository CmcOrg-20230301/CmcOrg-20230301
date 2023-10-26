package com.cmcorg20230301.be.engine.pay.base.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.RandomUtil;
import com.cmcorg20230301.be.engine.model.model.constant.BaseConstant;
import com.cmcorg20230301.be.engine.pay.base.model.dto.PayDTO;
import com.cmcorg20230301.be.engine.pay.base.model.entity.SysPayConfigurationDO;
import com.cmcorg20230301.be.engine.pay.base.model.enums.SysPayTypeEnum;
import com.cmcorg20230301.be.engine.pay.base.service.SysPayConfigurationService;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntity;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoId;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoIdFather;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class PayHelper {

    private static SysPayConfigurationService sysPayConfigurationService;

    @Resource
    public void setSysPayConfigurationService(SysPayConfigurationService sysPayConfigurationService) {
        PayHelper.sysPayConfigurationService = sysPayConfigurationService;
    }

    /**
     * 获取：SysPayConfigurationDO对象
     */
    @NotNull
    public static SysPayConfigurationDO getSysPayConfigurationDO(@Nullable Long tenantId, SysPayTypeEnum sysPayTypeEnum,
        @Nullable Boolean useParentTenantPayFlag) {

        if (tenantId == null) {
            tenantId = BaseConstant.TOP_TENANT_ID;
        }

        List<SysPayConfigurationDO> sysPayConfigurationDOList =
            sysPayConfigurationService.lambdaQuery().eq(BaseEntityNoIdFather::getTenantId, tenantId)
                .eq(BaseEntityNoId::getEnableFlag, true).eq(SysPayConfigurationDO::getType, sysPayTypeEnum).list();

        SysPayConfigurationDO sysPayConfigurationDO;

        if (CollUtil.isEmpty(sysPayConfigurationDOList)) {

            if (BooleanUtil.isTrue(useParentTenantPayFlag)) {

                PayDTO payDTO = new PayDTO();

                payDTO.setTenantId(tenantId);

                // 递归：获取上级租户的支付方式
                sysPayConfigurationDO =
                    PayUtil.handleUseParentTenantPayFlag(payDTO, tenantId, lambdaQueryChainWrapper -> {

                        lambdaQueryChainWrapper.eq(SysPayConfigurationDO::getType, sysPayTypeEnum);

                    });

            } else {

                ApiResultVO.errorMsg("操作失败：暂未配置【{}】支付", sysPayTypeEnum.name());

                sysPayConfigurationDO = new SysPayConfigurationDO(); // 只是为了通过语法的检测，这里的代码不会执行

            }

        } else {

            // 随机取一个
            sysPayConfigurationDO = RandomUtil.randomEle(sysPayConfigurationDOList);

        }

        return sysPayConfigurationDO;

    }

    /**
     * 获取：SysPayConfigurationDO对象
     */
    @Nullable
    public static SysPayConfigurationDO getSysPayConfigurationDO(Long tenantId, long sysPayConfigurationId,
        SysPayTypeEnum sysPayTypeEnum) {

        return sysPayConfigurationService.lambdaQuery().eq(BaseEntityNoIdFather::getTenantId, tenantId)
            .eq(SysPayConfigurationDO::getType, sysPayTypeEnum).eq(BaseEntity::getId, sysPayConfigurationId).one();

    }

}
