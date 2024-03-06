package com.cmcorg20230301.be.engine.pay.apply.util;

import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.cmcorg20230301.be.engine.pay.base.model.bo.SysPayReturnBO;
import com.cmcorg20230301.be.engine.pay.base.model.bo.SysPayTradeNotifyBO;
import com.cmcorg20230301.be.engine.pay.base.model.dto.PayDTO;
import com.cmcorg20230301.be.engine.pay.base.model.entity.SysPayConfigurationDO;
import com.cmcorg20230301.be.engine.pay.base.model.enums.SysPayTradeStatusEnum;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

/**
 * 支付：苹果工具类
 */
@Component
public class PayApplyUtil {

    public static final String OUT_TRADE_NO = "outTradeNo";

    /**
     * 支付
     */
    @SneakyThrows
    @NotNull
    public static SysPayReturnBO pay(PayDTO dto) {

        JSONObject jsonObject = JSONUtil.createObj().set(OUT_TRADE_NO, dto.getOutTradeNo());

        return new SysPayReturnBO(jsonObject.toString(), null);

    }

    /**
     * 查询订单状态
     *
     * @param outTradeNo 本系统的支付主键 id，必填
     */
    @SneakyThrows
    @NotNull
    public static SysPayTradeStatusEnum query(String outTradeNo,
        @Nullable SysPayTradeNotifyBO sysPayTradeNotifyBO,
        SysPayConfigurationDO sysPayConfigurationDO) {

        Assert.notBlank(outTradeNo);

        return SysPayTradeStatusEnum.UNKNOWN;

    }

}
