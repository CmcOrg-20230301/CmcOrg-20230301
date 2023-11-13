package com.cmcorg20230301.be.engine.pay.ali.configuration;

import com.cmcorg20230301.be.engine.pay.ali.util.PayAliUtil;
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
 * 支付宝支付相关配置类
 */
@Configuration
public class PayAliAppConfiguration implements ISysPay {

    /**
     * 支付方式类型
     */
    @Override
    @NotNull
    public ISysPayType getSysPayType() {
        return SysPayTypeEnum.ALI_APP;
    }

    /**
     * 支付返回值，备注：一般返回 url
     */
    @Override
    @NotNull
    public SysPayReturnBO pay(PayDTO dto) {
        return PayAliUtil.payApp(dto);
    }

    /**
     * 查询订单状态
     */
    @Override
    @NotNull
    public SysPayTradeStatusEnum query(String outTradeNo, SysPayConfigurationDO sysPayConfigurationDoTemp) {
        return PayAliUtil.query(outTradeNo, sysPayConfigurationDoTemp);
    }

}
