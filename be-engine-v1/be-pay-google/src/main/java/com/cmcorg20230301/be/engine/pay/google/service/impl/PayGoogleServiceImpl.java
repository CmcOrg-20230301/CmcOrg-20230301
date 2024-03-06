package com.cmcorg20230301.be.engine.pay.google.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.cmcorg20230301.be.engine.pay.base.model.bo.SysPayTradeNotifyBO;
import com.cmcorg20230301.be.engine.pay.base.model.entity.SysPayConfigurationDO;
import com.cmcorg20230301.be.engine.pay.base.model.enums.SysPayTradeStatusEnum;
import com.cmcorg20230301.be.engine.pay.base.util.PayHelper;
import com.cmcorg20230301.be.engine.pay.base.util.PayUtil;
import com.cmcorg20230301.be.engine.pay.google.model.dto.SysPayGooglePayConsumeDTO;
import com.cmcorg20230301.be.engine.pay.google.model.dto.SysPayGooglePaySuccessDTO;
import com.cmcorg20230301.be.engine.pay.google.service.PayGoogleService;
import com.cmcorg20230301.be.engine.pay.google.util.PayGoogleUtil;
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

        SysPayConfigurationDO sysPayConfigurationDO =
            PayHelper.getSysPayConfigurationDO(dto.getSysPayConfigurationId());

        if (sysPayConfigurationDO == null) {
            return false;
        }

        SysPayTradeNotifyBO sysPayTradeNotifyBO = new SysPayTradeNotifyBO();

        SysPayTradeStatusEnum sysPayTradeStatusEnum =
            PayGoogleUtil.query(dto.getId().toString(), sysPayTradeNotifyBO, sysPayConfigurationDO);

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
    public boolean payConsume(SysPayGooglePayConsumeDTO dto) {

        SysPayConfigurationDO sysPayConfigurationDO =
            PayHelper.getSysPayConfigurationDO(dto.getSysPayConfigurationId());

        if (sysPayConfigurationDO == null) {
            return false;
        }

        SysPayTradeNotifyBO sysPayTradeNotifyBO = new SysPayTradeNotifyBO();

        SysPayTradeStatusEnum sysPayTradeStatusEnum =
            PayGoogleUtil.query(dto.getId().toString(), sysPayTradeNotifyBO, sysPayConfigurationDO);

        if (SysPayTradeStatusEnum.TRADE_FINISHED.equals(sysPayTradeStatusEnum) == false) {
            return false;
        }

        sysPayTradeNotifyBO.setTradeStatus(CollUtil.getFirst(sysPayTradeStatusEnum.getStatusSet()));
        sysPayTradeNotifyBO.setOutTradeNo(dto.getId().toString());

        // 处理：订单回调
        return PayUtil.handleTradeNotify(sysPayTradeNotifyBO, null);

    }

}
