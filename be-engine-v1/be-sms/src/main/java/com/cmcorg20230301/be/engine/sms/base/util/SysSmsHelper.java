package com.cmcorg20230301.be.engine.sms.base.util;

import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Resource;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.cmcorg20230301.be.engine.model.model.constant.BaseConstant;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntity;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoId;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoIdSuper;
import com.cmcorg20230301.be.engine.security.model.entity.SysTenantDO;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.security.util.SysTenantUtil;
import com.cmcorg20230301.be.engine.security.util.UserUtil;
import com.cmcorg20230301.be.engine.sms.base.model.bo.SysSmsSendBO;
import com.cmcorg20230301.be.engine.sms.base.model.entity.SysSmsConfigurationDO;
import com.cmcorg20230301.be.engine.sms.base.service.SysSmsConfigurationService;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;

@Component
public class SysSmsHelper {

    private static SysSmsConfigurationService sysSmsConfigurationService;

    @Resource
    public void setSysSmsConfigurationService(SysSmsConfigurationService sysSmsConfigurationService) {
        SysSmsHelper.sysSmsConfigurationService = sysSmsConfigurationService;
    }

    /**
     * 获取：SysSmsSendBO对象
     */
    public static SysSmsSendBO getSysSmsSendBO(@Nullable Long tenantId, String sendContent, String phoneNumber) {

        if (tenantId == null) {
            tenantId = BaseConstant.TOP_TENANT_ID;
        }

        SysSmsSendBO sysSmsSendBO = new SysSmsSendBO();

        sysSmsSendBO.setTenantId(tenantId);
        sysSmsSendBO.setUseParentTenantSmsFlag(true);
        sysSmsSendBO.setSendContent(sendContent);
        sysSmsSendBO.setPhoneNumber(phoneNumber);

        return sysSmsSendBO;

    }

    /**
     * 获取：SysSmsSendBO对象
     */
    public static SysSmsSendBO getSysSmsSendBO(String sendContent, String phoneNumber, Long id) {

        if (id == null) {
            ApiResultVO.errorMsg("系统错误：短信配置 id为空");
        }

        SysSmsConfigurationDO sysSmsConfigurationDO =
            sysSmsConfigurationService.lambdaQuery().eq(BaseEntity::getId, id).one();

        if (sysSmsConfigurationDO == null) {
            ApiResultVO.error("未找到短信配置", id);
        }

        SysSmsSendBO sysSmsSendBO = new SysSmsSendBO();

        sysSmsSendBO.setSysSmsConfigurationDO(sysSmsConfigurationDO);

        sysSmsSendBO.setSendContent(sendContent);
        sysSmsSendBO.setPhoneNumber(phoneNumber);

        return sysSmsSendBO;

    }

    /**
     * 获取：SysSmsConfigurationDO对象
     */
    @NotNull
    public static SysSmsConfigurationDO getSysSmsConfigurationDO(@Nullable Long tenantId, Integer sysSmsType,
        @Nullable Boolean useParentTenantSmsFlag) {

        if (tenantId == null) {
            tenantId = BaseConstant.TOP_TENANT_ID;
        }

        List<SysSmsConfigurationDO> sysSmsConfigurationDOList =
            sysSmsConfigurationService.lambdaQuery().eq(BaseEntityNoIdSuper::getTenantId, tenantId)
                .eq(BaseEntityNoId::getEnableFlag, true).eq(SysSmsConfigurationDO::getType, sysSmsType).list();

        SysSmsConfigurationDO sysSmsConfigurationDO = null;

        if (CollUtil.isEmpty(sysSmsConfigurationDOList)) {

            if (BooleanUtil.isTrue(useParentTenantSmsFlag)) {

                // 递归：获取上级租户的短信方式
                sysSmsConfigurationDO = handleUseParentTenantSmsFlag(tenantId, tenantId, lambdaQueryChainWrapper -> {

                    lambdaQueryChainWrapper.eq(SysSmsConfigurationDO::getType, sysSmsType);

                });

            } else {

                ApiResultVO.error("操作失败：暂未配置短信", sysSmsType);

            }

        } else {

            // 随机取一个
            sysSmsConfigurationDO = RandomUtil.randomEle(sysSmsConfigurationDOList);

        }

        return sysSmsConfigurationDO;

    }

    /**
     * 获取：默认短信
     */
    @NotNull
    public static SysSmsConfigurationDO getDefaultSysSmsConfigurationDO(@Nullable Long tenantId,
        @Nullable Boolean useParentTenantPayFlag) {

        if (tenantId == null) {
            tenantId = BaseConstant.TOP_TENANT_ID;
        }

        SysSmsConfigurationDO sysSmsConfigurationDO =
            sysSmsConfigurationService.lambdaQuery().eq(BaseEntityNoIdSuper::getTenantId, tenantId)
                .eq(SysSmsConfigurationDO::getDefaultFlag, true).eq(BaseEntityNoId::getEnableFlag, true).one();

        if (sysSmsConfigurationDO == null) {

            if (BooleanUtil.isTrue(useParentTenantPayFlag)) {

                // 递归：获取上级租户的短信方式
                sysSmsConfigurationDO = handleUseParentTenantSmsFlag(tenantId, tenantId, lambdaQueryChainWrapper -> {

                    lambdaQueryChainWrapper.eq(SysSmsConfigurationDO::getDefaultFlag, true);

                });

            }

        }

        if (sysSmsConfigurationDO == null) {

            ApiResultVO.error("操作失败：未配置默认短信方式，请联系管理员", StrUtil.format("tenantIdOriginal：{} ", tenantId));

        }

        return sysSmsConfigurationDO;

    }

    /**
     * 处理：短信方式
     */
    public static void handleSysSmsConfigurationDO(SysSmsSendBO sysSmsSendBO) {

        SysSmsConfigurationDO sysSmsConfigurationDO = sysSmsSendBO.getSysSmsConfigurationDO();

        if (sysSmsConfigurationDO == null) {

            if (sysSmsSendBO.getSmsType() == null) { // 如果是：默认短信

                sysSmsConfigurationDO = getDefaultSysSmsConfigurationDO(sysSmsSendBO.getTenantId(),
                    sysSmsSendBO.getUseParentTenantSmsFlag());

            } else {

                sysSmsConfigurationDO = getSysSmsConfigurationDO(sysSmsSendBO.getTenantId(), sysSmsSendBO.getSmsType(),
                    sysSmsSendBO.getUseParentTenantSmsFlag());

            }

        }

        sysSmsSendBO.setSmsType(sysSmsConfigurationDO.getType());
        sysSmsSendBO.setSysSmsConfigurationDO(sysSmsConfigurationDO);

    }

    /**
     * 递归：获取上级租户的短信方式
     */
    @NotNull
    public static SysSmsConfigurationDO handleUseParentTenantSmsFlag(Long tenantIdOriginal, Long currentTenantId,
        @Nullable Consumer<LambdaQueryChainWrapper<SysSmsConfigurationDO>> lambdaQueryChainWrapperConsumer) {

        if (UserUtil.getCurrentTenantTopFlag(currentTenantId)) {

            ApiResultVO.error("操作失败：未配置短信发送，请联系管理员",
                StrUtil.format("tenantIdOriginal：{}，currentTenantId：{}", tenantIdOriginal, currentTenantId));

        }

        SysTenantDO sysTenantDO = SysTenantUtil.getSysTenantCacheMap(false).get(currentTenantId);

        if (sysTenantDO == null) {

            ApiResultVO.error("操作失败：租户不存在",
                StrUtil.format("tenantIdOriginal：{}，currentTenantId：{}", tenantIdOriginal, currentTenantId));

        }

        if (!sysTenantDO.getEnableFlag()) {

            ApiResultVO.error("操作失败：租户已被禁用，无法调用短信",
                StrUtil.format("tenantIdOriginal：{}，currentTenantId：{}", tenantIdOriginal, currentTenantId));

        }

        currentTenantId = sysTenantDO.getParentId(); // 设置为：上级租户 id

        LambdaQueryChainWrapper<SysSmsConfigurationDO> lambdaQueryChainWrapper =
            sysSmsConfigurationService.lambdaQuery().eq(BaseEntityNoIdSuper::getTenantId, currentTenantId)
                .eq(BaseEntityNoId::getEnableFlag, true);

        if (lambdaQueryChainWrapperConsumer != null) {
            lambdaQueryChainWrapperConsumer.accept(lambdaQueryChainWrapper);
        }

        List<SysSmsConfigurationDO> sysSmsConfigurationDOList = lambdaQueryChainWrapper.list();

        SysSmsConfigurationDO sysSmsConfigurationDO = null;

        if (CollUtil.isNotEmpty(sysSmsConfigurationDOList)) {

            // 随机取一个
            sysSmsConfigurationDO = RandomUtil.randomEle(sysSmsConfigurationDOList);

        }

        if (sysSmsConfigurationDO == null) {

            // 递归：获取上级租户的短信方式
            return handleUseParentTenantSmsFlag(tenantIdOriginal, currentTenantId, lambdaQueryChainWrapperConsumer);

        }

        return sysSmsConfigurationDO;

    }

}
