package com.cmcorg20230301.engine.be.security.util;

import cn.hutool.core.codec.Base32;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import com.cmcorg20230301.engine.be.security.properties.CommonProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.validation.constraints.NotNull;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * 谷歌验证器工具类
 */
@Slf4j
@Component
public class GoogleAuthenticatorUtil {

    // ip/域名/平台的名字
    private static String platformName;

    public GoogleAuthenticatorUtil(CommonProperties commonProperties) {

        GoogleAuthenticatorUtil.platformName = commonProperties.getPlatformName();

    }

    // taken from Google pam docs - we probably don't need to mess with these
    private static final int SECRET_SIZE = 10;

    private static final String RANDOM_NUMBER_ALGORITHM = "SHA1PRNG";

    // default 3 - max 17 (from google docs)：最多可偏移的时间
    private static final int WINDOW_SIZE = 6; // 6 * 30秒

    /**
     * 获取：二维码 base64格式
     */
    public static String getQRBarcodeBase64(String nickname, String secret) {

        String format = " otpauth://totp/{}@{}?secret={}";

        String content = StrUtil.format(format, nickname, platformName, secret);

        return QrCodeUtil.generateAsBase64(content, new QrConfig(), ImgUtil.IMAGE_TYPE_PNG);

    }

    /**
     * 检查：code
     */
    @NotNull
    public static boolean authCode(String code, String secret) {

        return GoogleAuthenticatorUtil.checkCode(secret, Long.parseLong(code), System.currentTimeMillis());

    }

    /**
     * 生成秘钥
     */
    public static String genSecret(String nickname) {

        return GoogleAuthenticatorUtil.generateSecretKey(nickname);

    }

    /**
     * 生成秘钥
     */
    private static String generateSecretKey(String nickname) {

        SecureRandom sr;

        try {

            sr = SecureRandom.getInstance(RANDOM_NUMBER_ALGORITHM);
            sr.setSeed(Base64.decode(IdUtil.randomUUID() + nickname));
            byte[] buffer = sr.generateSeed(SECRET_SIZE);

            return Base32.encode(buffer);

        } catch (NoSuchAlgorithmException ignored) {

        }

        return null;

    }

    /**
     * 检查，code
     */
    private static boolean checkCode(String secret, long code, long timeTs) {

        byte[] decodedKey = Base32.decode(secret);

        // convert unix msec time into a 30 second "window"
        // this is per the TOTP spec (see the RFC for details)
        long t = (timeTs / 1000L) / 30L;

        // Window is used to check codes generated in the near past.
        // You can use this value to tune how far you're willing to go.
        for (int i = -WINDOW_SIZE; i <= WINDOW_SIZE; ++i) {

            long hash;

            try {

                hash = verifyCode(decodedKey, t + i);

            } catch (Exception e) {

                // Yes, this is bad form - but
                // the exceptions thrown would be rare and a static configuration problem
                e.printStackTrace();
                throw new RuntimeException(e.getMessage());
                //return false;

            }

            if (hash == code) {

                return true;

            }

        }

        // The validation code is invalid.
        return false;

    }

    /**
     * 获取：code的 hash
     */
    private static int verifyCode(byte[] key, long t) throws Exception {

        byte[] data = new byte[8];
        long value = t;

        for (int i = 8; i-- > 0; value >>>= 8) {
            data[i] = (byte)value;
        }

        SecretKeySpec signKey = new SecretKeySpec(key, "HmacSHA1");

        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(signKey);

        byte[] hash = mac.doFinal(data);
        int offset = hash[20 - 1] & 0xF;
        // We're using a long because Java hasn't got unsigned int.
        long truncatedHash = 0;

        for (int i = 0; i < 4; ++i) {

            truncatedHash <<= 8;
            // We are dealing with signed bytes:
            // we just keep the first byte.
            truncatedHash |= (hash[offset + i] & 0xFF);

        }

        truncatedHash &= 0x7FFFFFFF;
        truncatedHash %= 1000000;

        return (int)truncatedHash;

    }

}
