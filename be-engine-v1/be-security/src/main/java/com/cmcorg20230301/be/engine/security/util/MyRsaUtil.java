package com.cmcorg20230301.be.engine.security.util;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.CryptoException;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import com.cmcorg20230301.be.engine.model.model.constant.BaseConstant;
import com.cmcorg20230301.be.engine.model.model.constant.ParamConstant;
import com.cmcorg20230301.be.engine.security.exception.BaseBizCodeEnum;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.util.util.SeparatorUtil;
import org.springframework.stereotype.Component;

@Component
public class MyRsaUtil {

    public static final int EXPIRE_TIME = BaseConstant.MINUTE_30_EXPIRE_TIME;

    public static final String SEPARATOR = SeparatorUtil.SEMICOLON_SEPARATOR;

    /**
     * 非对称：解密
     */
    public static String rsaDecrypt(String str) {

        String paramValue = SysParamUtil.getValueById(ParamConstant.RSA_PRIVATE_KEY_ID); // 获取非对称加密，私钥

        return rsaDecrypt(str, paramValue); // 返回解密之后的 字符串

    }

    /**
     * 非对称：解密
     */
    public static String rsaDecrypt(String str, String privateKey) {

        if (StrUtil.isBlank(str)) {

            ApiResultVO.error(BaseBizCodeEnum.PARAMETER_CHECK_ERROR);

        }

        if (StrUtil.isBlank(privateKey)) {

            ApiResultVO.sysError();

        }

        RSA rsa = new RSA(privateKey, null);

        String decryptStr = null;

        try {

            decryptStr = rsa.decryptStr(str, KeyType.PrivateKey);

        } catch (CryptoException e) {

            ApiResultVO.error(BaseBizCodeEnum.ILLEGAL_REQUEST);

        }

        if (StrUtil.isBlank(decryptStr)) {
            ApiResultVO.error(BaseBizCodeEnum.ILLEGAL_REQUEST);
        }

        String[] split = decryptStr.split(SEPARATOR);
        if (split.length != 2) {
            ApiResultVO.error(BaseBizCodeEnum.ILLEGAL_REQUEST);
        }

        // 获取：客户端传过来的时间戳
        Long userTs = Convert.toLong(split[1]);

        if (userTs == null) {
            ApiResultVO.error(BaseBizCodeEnum.ILLEGAL_REQUEST);
        }

        long currentTimeMillis = System.currentTimeMillis();

        long checkTs = Math.abs(userTs - currentTimeMillis);

        // 不能和服务器时间相差过大
        if (checkTs > EXPIRE_TIME) {
            ApiResultVO.errorMsg("操作失败：您的时间：{}，与当前时间：{}，相差过大，请调整时间后再试", userTs, currentTimeMillis);
        }

        return split[0]; // 返回解密之后的 字符串

    }

    /**
     * 非对称：加密
     */
    public static String rsaEncrypt(String str, String publicKey) {

        if (StrUtil.isBlank(str)) {

            ApiResultVO.error(BaseBizCodeEnum.PARAMETER_CHECK_ERROR);

        }

        if (StrUtil.isBlank(publicKey)) {

            ApiResultVO.sysError();

        }

        RSA rsa = new RSA(null, publicKey);

        String encryptStr = null;

        str = str + SEPARATOR + System.currentTimeMillis(); // 需要加上当前时间戳

        try {

            encryptStr = rsa.encryptBase64(str, KeyType.PublicKey);

        } catch (CryptoException e) {

            ApiResultVO.error(BaseBizCodeEnum.ILLEGAL_REQUEST);

        }

        return encryptStr; // 返回加密之后的 字符串

    }

}
