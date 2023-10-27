package com.cmcorg20230301.be.engine.pay.base.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.RandomUtil;
import com.cmcorg20230301.be.engine.model.model.constant.BaseConstant;
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
import java.math.BigDecimal;
import java.math.RoundingMode;
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

                // 递归：获取上级租户的支付方式
                sysPayConfigurationDO =
                    PayUtil.handleUseParentTenantPayFlag(tenantId, tenantId, lambdaQueryChainWrapper -> {

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
    public static SysPayConfigurationDO getSysPayConfigurationDO(long sysPayConfigurationId) {

        return sysPayConfigurationService.lambdaQuery().eq(BaseEntity::getId, sysPayConfigurationId).one();

    }

    /**
     * 获取：支付时的金额，字符串
     */
    public static String getPayTotalAmountStr(BigDecimal totalAmount) {

        // 备注：这里会返回一个新的 BigDecimal对象
        return totalAmount.setScale(2, RoundingMode.HALF_UP).toPlainString();

    }

}
