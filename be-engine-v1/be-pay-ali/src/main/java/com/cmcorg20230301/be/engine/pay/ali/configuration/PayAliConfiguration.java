package com.cmcorg20230301.be.engine.pay.ali.configuration;

import com.cmcorg20230301.be.engine.pay.ali.util.PayAliUtil;
import com.cmcorg20230301.be.engine.pay.base.model.bo.SysPayReturnBO;
import com.cmcorg20230301.be.engine.pay.base.model.configuration.ISysPay;
import com.cmcorg20230301.be.engine.pay.base.model.dto.PayDTO;
import com.cmcorg20230301.be.engine.pay.base.model.entity.SysPayConfigurationDO;
import com.cmcorg20230301.be.engine.pay.base.model.enums.SysPayTradeStatusEnum;
import com.cmcorg20230301.be.engine.pay.base.model.enums.SysPayTypeEnum;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.context.annotation.Configuration;

/**
 * 支付宝支付相关配置类
 */
@Configuration
public class PayAliConfiguration implements ISysPay {

    /**
     * 支付方式类型
     */
    @Override
    @NotNull
    public SysPayTypeEnum getSysPayType() {
        return SysPayTypeEnum.ALI;
    }

    /**
     * 支付返回值，备注：一般返回 url
     */
    @Override
    @NotNull
    public SysPayReturnBO pay(PayDTO dto) {
        return PayAliUtil.pay(dto);
    }

    /**
     * 查询订单状态
     */
    @Override
    @NotNull
    public SysPayTradeStatusEnum query(String outTradeNo, Long tenantId,
        @Nullable SysPayConfigurationDO sysPayConfigurationDoTemp) {
        return PayAliUtil.query(outTradeNo, tenantId, sysPayConfigurationDoTemp);
    }

}
