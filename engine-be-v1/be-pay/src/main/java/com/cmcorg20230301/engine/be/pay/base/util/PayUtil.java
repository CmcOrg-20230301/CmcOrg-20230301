package com.cmcorg20230301.engine.be.pay.base.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import com.cmcorg20230301.engine.be.model.model.dto.PayDTO;
import com.cmcorg20230301.engine.be.mysql.util.TransactionUtil;
import com.cmcorg20230301.engine.be.pay.base.model.entity.SysPayDO;
import com.cmcorg20230301.engine.be.pay.base.model.enums.PayRefTypeEnum;
import com.cmcorg20230301.engine.be.pay.base.properties.SysPayProperties;
import com.cmcorg20230301.engine.be.pay.base.service.SysPayService;
import com.cmcorg20230301.engine.be.redisson.util.IdGeneratorUtil;
import com.cmcorg20230301.engine.be.security.model.configuration.IPay;
import com.cmcorg20230301.engine.be.security.model.enums.SysPayTradeStatusEnum;
import com.cmcorg20230301.engine.be.security.model.vo.ApiResultVO;
import com.cmcorg20230301.engine.be.security.util.MyEntityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Component
public class PayUtil {

    private static SysPayProperties sysPayProperties;

    private static final Map<Integer, IPay> PAY_MAP = MapUtil.newHashMap();

    private static SysPayService sysPayService;

    public PayUtil(SysPayProperties sysPayProperties, @Autowired(required = false) List<IPay> iPayList,
        SysPayService sysPayService) {

        PayUtil.sysPayProperties = sysPayProperties;
        PayUtil.sysPayService = sysPayService;

        if (CollUtil.isNotEmpty(iPayList)) {

            for (IPay item : iPayList) {

                PAY_MAP.put(item.getType(), item);

            }

        }

    }

    /**
     * 支付，返回 url
     *
     * @param consumer 注意：SysPayDO对象，只建议设置：refType 和 refId这两个属性，其他属性不建议重新设置
     */
    public static SysPayDO pay(PayDTO dto, Consumer<SysPayDO> consumer) {

        IPay iPay = PAY_MAP.get(sysPayProperties.getBasePayType());

        if (iPay == null) {

            ApiResultVO.error("操作失败：支付方式未找到：{}", sysPayProperties.getBasePayType());

        }

        String url = iPay.pay(dto);

        SysPayDO sysPayDO = new SysPayDO();

        sysPayDO.setId(IdGeneratorUtil.nextId());
        sysPayDO.setPayType(iPay.getType());
        sysPayDO.setUserId(dto.getUserId());
        sysPayDO.setSubject(dto.getSubject());
        sysPayDO.setBody(dto.getBody());
        sysPayDO.setOriginPrice(dto.getTotalAmount());
        sysPayDO.setPayPrice(BigDecimal.ZERO);
        sysPayDO.setExpireTime(dto.getTimeExpire());
        sysPayDO.setOpenId(MyEntityUtil.getNotNullAndTrimStr(dto.getOpenId()));
        sysPayDO.setStatus(SysPayTradeStatusEnum.WAIT_BUYER_PAY);
        sysPayDO.setTransactionId("");
        sysPayDO.setPayReturnValue(url);
        sysPayDO.setRefType(PayRefTypeEnum.NONE);
        sysPayDO.setRefId(-1L);
        sysPayDO.setEnableFlag(true);
        sysPayDO.setDelFlag(false);
        sysPayDO.setRemark("");

        TransactionUtil.exec(() -> {

            if (consumer != null) {

                consumer.accept(sysPayDO);

            }

            sysPayService.save(sysPayDO);

        });

        return sysPayDO;

    }

    /**
     * 查询订单状态
     *
     * @param outTradeNo 商户订单号，商户网站订单系统中唯一订单号，必填
     */
    public static SysPayTradeStatusEnum query(String outTradeNo) {

        IPay iPay = PAY_MAP.get(sysPayProperties.getBasePayType());

        if (iPay == null) {

            ApiResultVO.error("操作失败：支付方式未找到：{}", sysPayProperties.getBasePayType());

        }

        return iPay.query(outTradeNo);

    }

}
