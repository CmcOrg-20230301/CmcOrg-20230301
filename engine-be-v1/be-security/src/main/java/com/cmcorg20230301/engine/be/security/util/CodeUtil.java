package com.cmcorg20230301.engine.be.security.util;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.cmcorg20230301.engine.be.security.model.vo.ApiResultVO;

/**
 * code 工具类
 */
public class CodeUtil {

    /**
     * 获取验证码
     */
    public static String getCode() {

        return RandomUtil.randomNumbers(6);

    }

    /**
     * 检查验证码
     */
    public static void checkCode(String sourceCode, String targetCode) {

        checkCode(sourceCode, targetCode, "操作失败：请先获取验证码", "验证码有误，请重新输入");

    }

    /**
     * 检查验证码
     */
    public static void checkCode(String sourceCode, String targetCode, String targetCodeBlankError,
        String notEqualsError) {

        // 如果不存在验证码
        if (StrUtil.isBlank(targetCode)) {
            ApiResultVO.error(targetCodeBlankError);
        }

        // 如果验证码不匹配
        if (!targetCode.equalsIgnoreCase(sourceCode)) {
            ApiResultVO.error(notEqualsError);
        }

    }

}
