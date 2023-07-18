package com.cmcorg20230301.engine.be.pay.google.configuration;

import com.cmcorg20230301.engine.be.model.model.dto.PayDTO;
import com.cmcorg20230301.engine.be.pay.base.model.configuration.ISysPay;
import com.cmcorg20230301.engine.be.pay.base.model.enums.SysPayTradeStatusEnum;
import com.cmcorg20230301.engine.be.pay.base.model.enums.SysPayTypeEnum;
import com.cmcorg20230301.engine.be.pay.google.util.PayGoogleUtil;
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
    public SysPayTypeEnum getSysPayType() {
        return SysPayTypeEnum.GOOGLE;
    }

    /**
     * 支付返回值，备注：一般返回 url
     */
    @Override
    public String pay(PayDTO dto) {
        return PayGoogleUtil.pay(dto);
    }

    /**
     * 查询订单状态
     */
    @Override
    public SysPayTradeStatusEnum query(String outTradeNo) {
        return PayGoogleUtil.query(outTradeNo, null);
    }

}
