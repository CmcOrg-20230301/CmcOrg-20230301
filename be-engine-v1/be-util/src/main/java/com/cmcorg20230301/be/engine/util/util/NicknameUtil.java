package com.cmcorg20230301.be.engine.util.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import org.jetbrains.annotations.Nullable;

import java.util.Date;

public class NicknameUtil {

    /**
     * 获取：默认的昵称
     */
    public static String getRandomNickname() {
        return getRandomNickname("用户昵称");
    }

    /**
     * 根据前缀获取：默认的昵称
     * 备注：不使用邮箱的原因，因为邮箱不符合 用户昵称的规则：只能包含中文，数字，字母，下划线，长度2-20
     */
    public static String getRandomNickname(@Nullable String preStr) {

        if (preStr == null) {
            preStr = "";
        }

        return preStr + RandomUtil.randomStringUpper(6);

    }

    /**
     * 获取：日期类型的昵称
     */
    public static String getDateTimeNickname(@Nullable String preStr) {

        if (preStr == null) {
            preStr = "";
        }

        String formatDateStr = DateUtil.formatDateTime(new Date());

        return preStr + formatDateStr;

    }

}
