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

        return totalPrice.divide(number, 5, RoundingMode.HALF_UP);

    }

    /**
     * 获取：字符串
     */
    public static String getStr(BigDecimal number) {

        return getStr(number, 2);

    }

    /**
     * 获取：字符串
     */
    public static String getStr(BigDecimal number, int newScale) {

        return number.setScale(newScale, RoundingMode.HALF_UP).toPlainString();

    }

    /**
     * 获取：一个数的倍数
     */
    public static BigDecimal getByMultiple(BigDecimal number, int multiple) {

        return number.multiply(BigDecimal.valueOf(multiple));

    }

    /**
     * 获取：一个数的倍数字符串
     */
    public static String getStrByMultiple(BigDecimal number, int multiple) {

        return getStr(getByMultiple(number, multiple));

    }

}
