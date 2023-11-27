package com.cmcorg20230301.be.engine.util.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MyNumberUtil {

    /**
     * 少于 0，则返回默认值
     */
    public static long getLessThanZeroDefaultValue(long value, long defaultValue) {
        return value < 0 ? defaultValue : value;
    }

    /**
     * 获取：单价
     */
    public static BigDecimal getUnitPrice(BigDecimal totalPrice, BigDecimal number) {

        return totalPrice.divide(number, 3, RoundingMode.HALF_UP);

    }

}
