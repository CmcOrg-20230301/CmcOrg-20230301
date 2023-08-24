package com.cmcorg20230301.engine.be.pay.ali.configuration;

import com.cmcorg20230301.engine.be.model.model.dto.PayDTO;
import com.cmcorg20230301.engine.be.pay.ali.util.PayAliUtil;
import com.cmcorg20230301.engine.be.pay.base.model.configuration.ISysPay;
import com.cmcorg20230301.engine.be.pay.base.model.enums.SysPayTradeStatusEnum;
import com.cmcorg20230301.engine.be.pay.base.model.enums.SysPayTypeEnum;
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
    public SysPayTypeEnum getSysPayType() {
        return SysPayTypeEnum.ALI;
    }

    /**
     * 支付返回值，备注：一般返回 url
     */
    @Override
    public String pay(PayDTO dto) {
        return PayAliUtil.pay(dto);
    }

    /**
     * 查询订单状态
     */
    @Override
    public SysPayTradeStatusEnum query(String outTradeNo) {
        return PayAliUtil.query(outTradeNo);
    }

}
