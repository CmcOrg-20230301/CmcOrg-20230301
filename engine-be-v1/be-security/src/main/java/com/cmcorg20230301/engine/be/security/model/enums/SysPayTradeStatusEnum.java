package com.cmcorg20230301.engine.be.security.model.enums;

import cn.hutool.core.collection.CollUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * 交易状态枚举类
 */
@AllArgsConstructor
@Getter
public enum SysPayTradeStatusEnum {

    WAIT_BUYER_PAY("交易创建，等待买家付款", CollUtil.newHashSet("WAIT_BUYER_PAY", "NOTPAY")), //
    TRADE_CLOSED("未付款交易超时关闭，或支付完成后全额退款", CollUtil.newHashSet("TRADE_CLOSED", "REFUND", "CLOSED")), //
    TRADE_SUCCESS("交易支付成功", CollUtil.newHashSet("TRADE_SUCCESS", "SUCCESS")), //
    TRADE_FINISHED("交易结束，不可退款", CollUtil.newHashSet("TRADE_FINISHED")), //

    ;

    private final String name; // 描述
    private final Set<String> codeSet; // 映射：支付平台的 code

    /**
     * 通过：code，获取枚举类
     */
    @Nullable
    public static SysPayTradeStatusEnum getByCode(String code) {

        for (SysPayTradeStatusEnum item : SysPayTradeStatusEnum.values()) {

            if (item.getCodeSet().contains(code)) {
                return item;
            }
        }

        return null;

    }

}
