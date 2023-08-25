package com.cmcorg20230301.be.engine.util.util;

import cn.hutool.core.util.RandomUtil;

public class NicknameUtil {

    /**
     * 获取：默认的用户名
     */
    public static String getRandomNickname() {
        return getRandomNickname("用户昵称");
    }

    /**
     * 根据前缀获取：默认的用户名
     * 备注：不使用邮箱的原因，因为邮箱不符合 用户昵称的规则：只能包含中文，数字，字母，下划线，长度2-20
     */
    public static String getRandomNickname(String preStr) {
        return preStr + RandomUtil.randomStringUpper(6);
    }

}
