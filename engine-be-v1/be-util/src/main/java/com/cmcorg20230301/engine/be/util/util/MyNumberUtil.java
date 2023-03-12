package com.cmcorg20230301.engine.be.util.util;

public class MyNumberUtil {

    /**
     * 少于 0，则返回默认值
     */
    public static long getLessThanZeroDefaultValue(long value, long defaultValue) {
        return value < 0 ? defaultValue : value;
    }

}
