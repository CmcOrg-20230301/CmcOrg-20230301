package com.cmcorg20230301.engine.be.pay.ali.configuration;

import com.cmcorg20230301.engine.be.model.model.dto.PayDTO;
import com.cmcorg20230301.engine.be.pay.ali.util.PayAliUtil;
import com.cmcorg20230301.engine.be.pay.base.model.configuration.IPay;
import com.cmcorg20230301.engine.be.pay.base.model.enums.SysPayTradeStatusEnum;
import org.springframework.context.annotation.Configuration;

/**
 * 支付宝支付相关配置类
 */
@Configuration
public class PayAliConfiguration implements IPay {

    /**
     * 1 支付宝 2 微信 3 云闪付
     */
    @Override
    public int getType() {
        return 1;
    }

    /**
     * 支付，返回 url
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
