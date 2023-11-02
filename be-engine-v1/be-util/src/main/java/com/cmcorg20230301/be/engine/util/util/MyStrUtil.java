package com.cmcorg20230301.be.engine.util.util;

import cn.hutool.core.util.StrUtil;

import java.util.function.Consumer;

public class MyStrUtil {

    /**
     * 根据最大长度，拆分字符串，然后把拆分结果多次执行：consumer
     */
    public static void subWithMaxLengthAndConsumer(String content, int maxLength, Consumer<String> consumer) {

        if (StrUtil.isBlank(content)) {
            return;
        }

        int length = content.length() / maxLength;

        if (length == 0) {

            consumer.accept(content);

        } else {

            for (int i = 0; i < length + 1; i++) {

                String subStr = StrUtil.subWithLength(content, i * maxLength, maxLength);

                if (StrUtil.isBlank(subStr)) {
                    return;
                }

                consumer.accept(subStr);

            }

        }

    }

}
