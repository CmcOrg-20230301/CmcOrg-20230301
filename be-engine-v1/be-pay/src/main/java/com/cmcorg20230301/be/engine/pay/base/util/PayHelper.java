package com.cmcorg20230301.be.engine.pay.base.util;

import cn.hutool.core.collection.CollUtil;
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

import javax.annotation.Resource;
import java.util.List;

@Resource
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
    public static SysPayConfigurationDO getSysPayConfigurationDO(@Nullable Long tenantId,
        SysPayTypeEnum sysPayTypeEnum) {

        if (tenantId == null) {
            tenantId = BaseConstant.TENANT_ID;
        }

        List<SysPayConfigurationDO> sysPayConfigurationDOList =
            sysPayConfigurationService.lambdaQuery().eq(BaseEntityNoIdFather::getTenantId, tenantId)
                .eq(BaseEntityNoId::getEnableFlag, true).eq(SysPayConfigurationDO::getType, sysPayTypeEnum).list();

        if (CollUtil.isEmpty(sysPayConfigurationDOList)) {
            ApiResultVO.errorMsg("操作失败：暂未配置【{}】支付配置", sysPayTypeEnum.name());
        }

        // 随机取一个
        return RandomUtil.randomEle(sysPayConfigurationDOList);

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
