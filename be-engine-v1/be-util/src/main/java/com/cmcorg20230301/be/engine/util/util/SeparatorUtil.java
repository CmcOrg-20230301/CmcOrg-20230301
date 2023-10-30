package com.cmcorg20230301.be.engine.util.util;

import cn.hutool.core.collection.CollUtil;

/**
 * 连接符，工具类
 */
public class SeparatorUtil {

    // 竖线分隔符
    public final static String VERTICAL_LINE_SEPARATOR = "|";

    // 冒号分隔符
    public final static String COLON_SEPARATOR = ":";

    // 分号分隔符
    public final static String SEMICOLON_SEPARATOR = ";";

    // 数字分隔符，例如：微信公众号网页授权登录之后，传递 state参数时：重定向后会带上 state参数，开发者可以填写a-zA-Z0-9的参数值，最多128字节
    public final static String NUMBER_STR_SEPARATOR = "000000";

    /**
     * 返回：被【竖线分隔符】，包裹的字符串
     */
    public static String verticalLine(Object object) {

        return VERTICAL_LINE_SEPARATOR + object + VERTICAL_LINE_SEPARATOR;

    }

    /**
     * @return 示例：|1||2||3|
     */
    public static <T> String verticalLine(Iterable<T> iterable) {

        return CollUtil.join(iterable, "", VERTICAL_LINE_SEPARATOR, VERTICAL_LINE_SEPARATOR);

    }

    /**
     * 返回：【冒号分隔符】，隔开的字符串
     *
     * @return 示例：1:2:3
     */
    public static <T> String colon(Iterable<T> iterable) {

        return CollUtil.join(iterable, COLON_SEPARATOR);

    }

}
