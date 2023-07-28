package com.cmcorg20230301.engine.be.security.util;

import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.cmcorg20230301.engine.be.security.model.vo.ApiResultVO;

/**
 * 密码加密类
 */
public class PasswordConvertUtil {

    // 盐和加密后密码的分割符
    private static final String REGEX = "/";

    /**
     * 密码加密
     */
    public static String convert(String password, boolean checkPasswordBlank) {

        if (StrUtil.isBlank(password)) {
            if (checkPasswordBlank) {
                ApiResultVO.errorMsg("密码不能为空");
            } else {
                return "";
            }
        }

        String salt = IdUtil.simpleUUID(); // 取盐

        String saltPro = shaEncode(salt); // 盐处理一下

        String newPassword = cycle(saltPro + password); // 循环

        StrBuilder strBuilder = new StrBuilder(salt); // 把盐放到最前面
        strBuilder.append(REGEX).append(newPassword);

        return strBuilder.toString();

    }

    /**
     * 循环加密
     */
    private static String cycle(String password) {

        for (int i = 0; i < 6; i++) {
            password = shaEncode(password);
        }

        return password;

    }

    /**
     * 密码匹配
     *
     * @param source 用户数据库的密码
     * @param target 前端传过来的密码
     * @return true 一致 false 不一致
     */
    public static boolean match(String source, String target) {

        if (StrUtil.isBlank(source)) {
            ApiResultVO.errorMsg("原密码不能为空");
        }
        if (StrUtil.isBlank(target)) {
            ApiResultVO.errorMsg("需要比对的密码不能为空");
        }

        String[] split = source.split(REGEX);

        split[0] = shaEncode(split[0]); // 盐处理一下

        return cycle(split[0] + target).equals(split[1]);

    }

    /**
     * 摘要算法
     */
    private static String shaEncode(String password) {

        return DigestUtil.sha512Hex((DigestUtil.sha512Hex(password)));

    }

}
