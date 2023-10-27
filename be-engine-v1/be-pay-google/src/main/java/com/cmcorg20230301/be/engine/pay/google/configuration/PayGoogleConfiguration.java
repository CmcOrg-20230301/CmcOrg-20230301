package com.cmcorg20230301.be.engine.pay.google.configuration;

import com.cmcorg20230301.be.engine.pay.base.model.bo.SysPayReturnBO;
import com.cmcorg20230301.be.engine.pay.base.model.configuration.ISysPay;
import com.cmcorg20230301.be.engine.pay.base.model.dto.PayDTO;
import com.cmcorg20230301.be.engine.pay.base.model.entity.SysPayConfigurationDO;
import com.cmcorg20230301.be.engine.pay.base.model.enums.SysPayTradeStatusEnum;
import com.cmcorg20230301.be.engine.pay.base.model.enums.SysPayTypeEnum;
import com.cmcorg20230301.be.engine.pay.google.util.PayGoogleUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;

/**
 * 谷歌支付相关配置类
 */
@Configuration
public class PayGoogleConfiguration implements ISysPay {

    /**
     * 支付方式类型
     */
    @Override
    @NotNull
    public SysPayTypeEnum getSysPayType() {
        return SysPayTypeEnum.GOOGLE;
    }

    /**
     * 支付返回值，备注：一般返回 url
     */
    @Override
    @NotNull
    public SysPayReturnBO pay(PayDTO dto) {
        return PayGoogleUtil.pay(dto);
    }

    /**
     * 查询订单状态
     */
    @Override
    @NotNull
    public SysPayTradeStatusEnum query(String outTradeNo, SysPayConfigurationDO sysPayConfigurationDoTemp) {
        return PayGoogleUtil.query(outTradeNo, null, sysPayConfigurationDoTemp);
    }

}
