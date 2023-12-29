package com.cmcorg20230301.be.engine.pay.base.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.cmcorg20230301.be.engine.model.model.constant.BaseConstant;
import com.cmcorg20230301.be.engine.pay.base.model.dto.PayDTO;
import com.cmcorg20230301.be.engine.pay.base.model.entity.SysPayConfigurationDO;
import com.cmcorg20230301.be.engine.pay.base.service.SysPayConfigurationService;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntity;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoId;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoIdSuper;
import com.cmcorg20230301.be.engine.security.model.entity.SysTenantDO;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.security.util.SysTenantUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.function.Consumer;

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
    public static SysPayConfigurationDO getSysPayConfigurationDO(@Nullable Long tenantId, Integer sysPayType,
        @Nullable Boolean useParentTenantPayFlag) {

        if (tenantId == null) {
            tenantId = BaseConstant.TOP_TENANT_ID;
        }

        List<SysPayConfigurationDO> sysPayConfigurationDOList =
            sysPayConfigurationService.lambdaQuery().eq(BaseEntityNoIdSuper::getTenantId, tenantId)
                .eq(BaseEntityNoId::getEnableFlag, true).eq(SysPayConfigurationDO::getType, sysPayType).list();

        SysPayConfigurationDO sysPayConfigurationDO = null;

        if (CollUtil.isEmpty(sysPayConfigurationDOList)) {

            if (BooleanUtil.isTrue(useParentTenantPayFlag)) {

                // 递归：获取上级租户的支付方式
                sysPayConfigurationDO = handleUseParentTenantPayFlag(tenantId, tenantId, lambdaQueryChainWrapper -> {

                    lambdaQueryChainWrapper.eq(SysPayConfigurationDO::getType, sysPayType);

                });

            } else {

                ApiResultVO.error("操作失败：暂未配置支付", sysPayType);

            }

        } else {

            // 随机取一个
            sysPayConfigurationDO = RandomUtil.randomEle(sysPayConfigurationDOList);

        }

        return sysPayConfigurationDO;

    }

    /**
     * 获取：默认支付
     */
    public static SysPayConfigurationDO getDefaultSysPayConfigurationDO(@Nullable Long tenantId,
        @Nullable Boolean useParentTenantPayFlag) {

        if (tenantId == null) {
            tenantId = BaseConstant.TOP_TENANT_ID;
        }

        SysPayConfigurationDO sysPayConfigurationDO =
            sysPayConfigurationService.lambdaQuery().eq(BaseEntityNoIdSuper::getTenantId, tenantId)
                .eq(SysPayConfigurationDO::getDefaultFlag, true).eq(BaseEntityNoId::getEnableFlag, true).one();

        if (sysPayConfigurationDO == null) {

            if (BooleanUtil.isTrue(useParentTenantPayFlag)) {

                // 递归：获取上级租户的支付方式
                sysPayConfigurationDO = handleUseParentTenantPayFlag(tenantId, tenantId, lambdaQueryChainWrapper -> {

                    lambdaQueryChainWrapper.eq(SysPayConfigurationDO::getDefaultFlag, true);

                });

            }

        }

        if (sysPayConfigurationDO == null) {

            ApiResultVO.error("操作失败：未配置默认支付方式，请联系管理员", StrUtil.format("tenantIdOriginal：{} ", tenantId));

        }

        return sysPayConfigurationDO;

    }

    /**
     * 递归：获取上级租户的支付方式
     */
    @NotNull
    public static SysPayConfigurationDO handleUseParentTenantPayFlag(Long tenantIdOriginal, Long currentTenantId,
        @Nullable Consumer<LambdaQueryChainWrapper<SysPayConfigurationDO>> lambdaQueryChainWrapperConsumer) {

        if (BaseConstant.TOP_TENANT_ID.equals(currentTenantId)) {

            ApiResultVO.error("操作失败：未配置支付，请联系管理员",
                StrUtil.format("tenantIdOriginal：{}，currentTenantId：{}", tenantIdOriginal, currentTenantId));

        }

        SysTenantDO sysTenantDO = SysTenantUtil.getSysTenantCacheMap(false).get(currentTenantId);

        if (sysTenantDO == null) {

            ApiResultVO.error("操作失败：租户不存在",
                StrUtil.format("tenantIdOriginal：{}，currentTenantId：{}", tenantIdOriginal, currentTenantId));

        }

        if (!sysTenantDO.getEnableFlag()) {

            ApiResultVO.error("操作失败：租户已被禁用，无法调用支付",
                StrUtil.format("tenantIdOriginal：{}，currentTenantId：{}", tenantIdOriginal, currentTenantId));

        }

        currentTenantId = sysTenantDO.getParentId();  // 设置为：上级租户 id

        LambdaQueryChainWrapper<SysPayConfigurationDO> lambdaQueryChainWrapper =
            sysPayConfigurationService.lambdaQuery().eq(BaseEntityNoIdSuper::getTenantId, currentTenantId)
                .eq(BaseEntityNoId::getEnableFlag, true);

        if (lambdaQueryChainWrapperConsumer != null) {
            lambdaQueryChainWrapperConsumer.accept(lambdaQueryChainWrapper);
        }

        List<SysPayConfigurationDO> sysPayConfigurationDOList = lambdaQueryChainWrapper.list();

        SysPayConfigurationDO sysPayConfigurationDO = null;

        if (CollUtil.isNotEmpty(sysPayConfigurationDOList)) {

            // 随机取一个
            sysPayConfigurationDO = RandomUtil.randomEle(sysPayConfigurationDOList);

        }

        if (sysPayConfigurationDO == null) {

            // 递归：获取上级租户的支付方式
            return handleUseParentTenantPayFlag(tenantIdOriginal, currentTenantId, lambdaQueryChainWrapperConsumer);

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
     * 执行：在调用支付前，进行的操作，备注：可以更换支付配置
     */
    public static void execPreDoPayConsumer(PayDTO payDTO) {

        Consumer<PayDTO> preDoPayConsumer = payDTO.getPreDoPayConsumer();

        if (preDoPayConsumer != null) {
            preDoPayConsumer.accept(payDTO); // 执行：检查
        }

    }

}
