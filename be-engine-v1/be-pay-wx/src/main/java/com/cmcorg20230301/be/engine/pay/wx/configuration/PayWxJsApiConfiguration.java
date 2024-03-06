package com.cmcorg20230301.be.engine.pay.wx.configuration;

import com.cmcorg20230301.be.engine.pay.base.model.bo.SysPayReturnBO;
import com.cmcorg20230301.be.engine.pay.base.model.configuration.ISysPay;
import com.cmcorg20230301.be.engine.pay.base.model.dto.PayDTO;
import com.cmcorg20230301.be.engine.pay.base.model.entity.SysPayConfigurationDO;
import com.cmcorg20230301.be.engine.pay.base.model.enums.SysPayTradeStatusEnum;
import com.cmcorg20230301.be.engine.pay.base.model.enums.SysPayTypeEnum;
import com.cmcorg20230301.be.engine.pay.base.model.interfaces.ISysPayType;
import com.cmcorg20230301.be.engine.pay.wx.util.PayWxUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;

/**
 * 微信支付相关配置类
 */
@Configuration
public class PayWxJsApiConfiguration implements ISysPay {

    /**
     * 支付方式类型
     */
    @Override
    @NotNull
    public ISysPayType getSysPayType() {
        return SysPayTypeEnum.WX_JSAPI;
    }

    /**
     * 支付返回值，备注：一般返回 url
     */
    @Override
    @NotNull
    public SysPayReturnBO pay(PayDTO dto) {
        return PayWxUtil.payJsApi(dto);
    }

    /**
     * 查询订单状态
     */
    @Override
    @NotNull
    public SysPayTradeStatusEnum query(String outTradeNo,
        SysPayConfigurationDO sysPayConfigurationDO) {
        return PayWxUtil.queryJsApi(outTradeNo, sysPayConfigurationDO);
    }

}
