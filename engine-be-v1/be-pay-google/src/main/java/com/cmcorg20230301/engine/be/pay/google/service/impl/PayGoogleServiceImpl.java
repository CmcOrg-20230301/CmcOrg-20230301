package com.cmcorg20230301.engine.be.pay.google.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.cmcorg20230301.engine.be.model.model.dto.NotNullId;
import com.cmcorg20230301.engine.be.pay.base.model.bo.SysPayTradeNotifyBO;
import com.cmcorg20230301.engine.be.pay.base.model.enums.SysPayTradeStatusEnum;
import com.cmcorg20230301.engine.be.pay.base.util.PayUtil;
import com.cmcorg20230301.engine.be.pay.google.model.dto.SysPayGooglePaySuccessDTO;
import com.cmcorg20230301.engine.be.pay.google.service.PayGoogleService;
import com.cmcorg20230301.engine.be.pay.google.util.PayGoogleUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PayGoogleServiceImpl implements PayGoogleService {

    /**
     * 支付成功的回调，备注：由客户端调用
     */
    @Override
    public boolean paySuccess(SysPayGooglePaySuccessDTO dto) {

        SysPayTradeNotifyBO sysPayTradeNotifyBO = new SysPayTradeNotifyBO();

        SysPayTradeStatusEnum sysPayTradeStatusEnum = PayGoogleUtil.query(dto.getId().toString(), sysPayTradeNotifyBO);

        if (SysPayTradeStatusEnum.WAIT_BUYER_CONSUME.equals(sysPayTradeStatusEnum) == false) {
            return false;
        }

        sysPayTradeNotifyBO.setTradeStatus(CollUtil.getFirst(sysPayTradeStatusEnum.getStatusSet()));
        sysPayTradeNotifyBO.setOutTradeNo(dto.getId().toString());

        // 处理：订单回调
        return PayUtil.handleTradeNotify(sysPayTradeNotifyBO, sysPayDO -> {

            sysPayDO.setToken(dto.getToken());

        });

    }

    /**
     * 支付核销的回调，备注：由客户端调用
     */
    @Override
    @SneakyThrows
    public boolean payConsume(NotNullId notNullId) {

        SysPayTradeNotifyBO sysPayTradeNotifyBO = new SysPayTradeNotifyBO();

        SysPayTradeStatusEnum sysPayTradeStatusEnum =
            PayGoogleUtil.query(notNullId.getId().toString(), sysPayTradeNotifyBO);

        if (SysPayTradeStatusEnum.TRADE_FINISHED.equals(sysPayTradeStatusEnum) == false) {
            return false;
        }

        sysPayTradeNotifyBO.setTradeStatus(CollUtil.getFirst(sysPayTradeStatusEnum.getStatusSet()));
        sysPayTradeNotifyBO.setOutTradeNo(notNullId.getId().toString());

        // 处理：订单回调
        return PayUtil.handleTradeNotify(sysPayTradeNotifyBO, null);

    }

}
