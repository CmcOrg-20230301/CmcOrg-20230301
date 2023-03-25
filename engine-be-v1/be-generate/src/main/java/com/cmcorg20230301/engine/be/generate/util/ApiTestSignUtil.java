package com.cmcorg20230301.engine.be.generate.util;

import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import com.cmcorg20230301.engine.be.security.util.MyRsaUtil;
import com.cmcorg20230301.engine.be.sign.signinname.model.dto.SignSignInNameSignInPasswordDTO;
import com.cmcorg20230301.engine.be.sign.signinname.model.dto.SignSignInNameSignUpDTO;
import com.cmcorg20230301.engine.be.sign.signinname.model.dto.SignSignInNameUpdatePasswordDTO;
import lombok.extern.slf4j.Slf4j;

/**
 * 登录注册相关接口测试工具
 */
@Slf4j
public class ApiTestSignUtil {

    // 执行，接口的地址，备注：最后面不要加斜杠 /
    //    private static final String API_ENDPOINT = "http://43.154.37.130:10001";
    private static final String API_ENDPOINT = "http://127.0.0.1:10001";

    // rsa公钥
    private static final String RSA_PUBLIC_KEY =
        "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDadmaCaffN63JC5QsMK/+le5voCB4DzOsV9xOBZgGJyqnizh9/UcFkIoRae5rebdWUtnPO4CTgdJbuSvu/TtIIPj9De5/wiJilFAWd1Ve7qGaxxTxqWwFNp7p/FLr0YpMeBjOylds9GyA1cnjIqruNdYv+qRZnseE0Sq2WEZus9QIDAQAB";

    // 登录名
    private static final String SIGN_IN_NAME = "cxk";

    // 密码
    private static final String PASSWORD_TEMP = "Ik123456";

    // 新密码
    private static final String NEW_PASSWORD_TEMP = "Ik123456";

    public static void main(String[] args) {

        // 登录名-注册
        signInNameSignUp(API_ENDPOINT, SIGN_IN_NAME);

        // 登录名-用户名账号密码登录
        String jwt = signInNameSignIn(API_ENDPOINT, SIGN_IN_NAME);

        // 登录名-修改密码
        signInNameUpdatePassword(API_ENDPOINT, SIGN_IN_NAME, jwt);

        //        // 登录名-修改账号
        //        signInNameUpdateAccount(API_ENDPOINT, SIGN_IN_NAME, jwt);
        //
        //        // 登录名-账号注销
        //        signInNameSignDelete(API_ENDPOINT, SIGN_IN_NAME, jwt);

    }

    /**
     * 登录名-修改密码
     */
    private static void signInNameUpdatePassword(String apiEndpoint, String signInName, String jwt) {

        long currentTs = System.currentTimeMillis();

        String oldPassword = DigestUtil.sha256Hex((DigestUtil.sha512Hex(PASSWORD_TEMP)));

        oldPassword = MyRsaUtil.rsaEncrypt(oldPassword, RSA_PUBLIC_KEY);

        String originNewPassword = MyRsaUtil.rsaEncrypt(NEW_PASSWORD_TEMP, RSA_PUBLIC_KEY);

        String newPassword = DigestUtil.sha256Hex((DigestUtil.sha512Hex(NEW_PASSWORD_TEMP)));

        newPassword = MyRsaUtil.rsaEncrypt(newPassword, RSA_PUBLIC_KEY);

        SignSignInNameUpdatePasswordDTO dto = new SignSignInNameUpdatePasswordDTO();
        dto.setOldPassword(oldPassword);
        dto.setNewPassword(newPassword);
        dto.setOriginNewPassword(originNewPassword);

        String bodyStr = HttpRequest.post(apiEndpoint + "/sign/signInName/updatePassword").body(JSONUtil.toJsonStr(dto))
            .header("Authorization", jwt).execute().body();

        log.info("登录名-修改密码：耗时：{}，bodyStr：{}", calcCostMs(currentTs), bodyStr);

    }

    /**
     * 登录名-用户名账号密码登录
     */
    private static String signInNameSignIn(String apiEndpoint, String signInName) {

        long currentTs = System.currentTimeMillis();

        String password = DigestUtil.sha256Hex((DigestUtil.sha512Hex(PASSWORD_TEMP)));

        password = MyRsaUtil.rsaEncrypt(password, RSA_PUBLIC_KEY);

        SignSignInNameSignInPasswordDTO dto = new SignSignInNameSignInPasswordDTO();
        dto.setPassword(password);
        dto.setSignInName(signInName);

        String bodyStr =
            HttpRequest.post(apiEndpoint + "/sign/signInName/sign/in/password").body(JSONUtil.toJsonStr(dto)).execute()
                .body();

        log.info("登录名-用户名账号密码登录：耗时：{}，bodyStr：{}", calcCostMs(currentTs), bodyStr);

        return JSONUtil.parseObj(bodyStr).getStr("data");

    }

    /**
     * 登录名-注册
     */
    private static SignSignInNameSignUpDTO signInNameSignUp(String apiEndpoint, String signInName) {

        long currentTs = System.currentTimeMillis();

        String originPassword = MyRsaUtil.rsaEncrypt(PASSWORD_TEMP, RSA_PUBLIC_KEY);

        String password = DigestUtil.sha256Hex((DigestUtil.sha512Hex(PASSWORD_TEMP)));

        password = MyRsaUtil.rsaEncrypt(password, RSA_PUBLIC_KEY);

        SignSignInNameSignUpDTO dto = new SignSignInNameSignUpDTO();
        dto.setPassword(password);
        dto.setOriginPassword(originPassword);
        dto.setSignInName(signInName);

        String bodyStr =
            HttpRequest.post(apiEndpoint + "/sign/signInName/sign/up").body(JSONUtil.toJsonStr(dto)).execute().body();

        log.info("登录名-注册：耗时：{}，bodyStr：{}", calcCostMs(currentTs), bodyStr);

        return dto;

    }

    /**
     * 计算花费的时间
     */
    private static long calcCostMs(long currentTs) {

        return System.currentTimeMillis() - currentTs;

    }

}
