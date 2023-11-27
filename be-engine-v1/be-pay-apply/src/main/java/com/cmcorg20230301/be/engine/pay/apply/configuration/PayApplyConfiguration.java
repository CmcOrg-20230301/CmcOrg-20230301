package com.cmcorg20230301.be.engine.pay.apply.configuration;

import com.cmcorg20230301.be.engine.pay.apply.util.PayApplyUtil;
import com.cmcorg20230301.be.engine.pay.base.model.bo.SysPayReturnBO;
import com.cmcorg20230301.be.engine.pay.base.model.configuration.ISysPay;
import com.cmcorg20230301.be.engine.pay.base.model.dto.PayDTO;
import com.cmcorg20230301.be.engine.pay.base.model.entity.SysPayConfigurationDO;
import com.cmcorg20230301.be.engine.pay.base.model.enums.SysPayTradeStatusEnum;
import com.cmcorg20230301.be.engine.pay.base.model.enums.SysPayTypeEnum;
import com.cmcorg20230301.be.engine.pay.base.model.interfaces.ISysPayType;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;

/**
 * 苹果支付相关配置类
 */
@Configuration
public class PayApplyConfiguration implements ISysPay {

    /**
     * 支付方式类型
     */
    @Override
    @NotNull
    public ISysPayType getSysPayType() {
        return SysPayTypeEnum.GOOGLE;
    }

    /**
     * 支付返回值，备注：一般返回 url
     */
    @Override
    @NotNull
    public SysPayReturnBO pay(PayDTO dto) {
        return PayApplyUtil.pay(dto);
    }

    /**
     * 查询订单状态
     */
    @Override
    @NotNull
    public SysPayTradeStatusEnum query(String outTradeNo, SysPayConfigurationDO sysPayConfigurationDO) {
        return PayApplyUtil.query(outTradeNo, null, sysPayConfigurationDO);
    }

}
