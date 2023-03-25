package com.cmcorg20230301.engine.be.generate.util;

import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import com.cmcorg20230301.engine.be.security.util.MyRsaUtil;
import com.cmcorg20230301.engine.be.sign.email.model.dto.EmailNotBlankDTO;
import com.cmcorg20230301.engine.be.sign.email.model.dto.SignEmailSignInPasswordDTO;
import com.cmcorg20230301.engine.be.sign.email.model.dto.SignEmailSignUpDTO;
import com.cmcorg20230301.engine.be.sign.email.model.dto.SignEmailUpdatePasswordDTO;
import lombok.extern.slf4j.Slf4j;

/**
 * 邮箱，登录注册相关接口测试工具
 */
@Slf4j
public class ApiTestSignEmailUtil {

    // 执行，接口的地址，备注：最后面不要加斜杠 /
    //    private static final String API_ENDPOINT = "http://43.154.37.130:10001";
    private static final String API_ENDPOINT = "http://127.0.0.1:10001";

    // 邮箱
    private static final String EMAIL = "dimensional_logic@qq.com";

    // 新邮箱
    private static final String NEW_EMAIL = "dimlt@vip.qq.com";

    // 密码
    private static final String PASSWORD_TEMP = "Ik123456";

    // 新密码
    private static final String NEW_PASSWORD_TEMP = "Ik1234567";

    public static void main(String[] args) {

        // 执行
        exec(API_ENDPOINT, EMAIL, PASSWORD_TEMP, NEW_EMAIL, NEW_PASSWORD_TEMP, ApiTestHelper.RSA_PUBLIC_KEY);

    }

    /**
     * 执行
     */
    private static void exec(String apiEndpoint, String email, String passwordTemp, String newEmail,
        String newPasswordTemp, String rsaPublicKey) {

        // 邮箱-注册-发送验证码
        emailSignUpSendCode(apiEndpoint, email);

        String code = ApiTestHelper.getStringFromScanner("请输入验证码");

        // 邮箱-注册
        emailSignUp(apiEndpoint, email, passwordTemp, rsaPublicKey, code);

        // 邮箱-账号密码登录
        String jwt = emailSignIn(apiEndpoint, email, passwordTemp, rsaPublicKey);

        // 邮箱-修改密码-发送验证码
        emailUpdatePasswordSendCode(apiEndpoint, jwt);

        code = ApiTestHelper.getStringFromScanner("请输入验证码");

        // 邮箱-修改密码
        emailUpdatePassword(apiEndpoint, jwt, newPasswordTemp, rsaPublicKey, code);

    }

    /**
     * 邮箱-修改密码
     */
    private static void emailUpdatePassword(String apiEndpoint, String jwt, String newPasswordTemp, String rsaPublicKey,
        String code) {

        long currentTs = System.currentTimeMillis();

        String originNewPassword = MyRsaUtil.rsaEncrypt(newPasswordTemp, rsaPublicKey);

        String newPassword = DigestUtil.sha256Hex((DigestUtil.sha512Hex(newPasswordTemp)));

        newPassword = MyRsaUtil.rsaEncrypt(newPassword, rsaPublicKey);

        SignEmailUpdatePasswordDTO dto = new SignEmailUpdatePasswordDTO();
        dto.setCode(code);
        dto.setNewPassword(newPassword);
        dto.setOriginNewPassword(originNewPassword);

        String bodyStr = HttpRequest.post(apiEndpoint + "/sign/email/updatePassword").body(JSONUtil.toJsonStr(dto))
            .header("Authorization", jwt).execute().body();

        log.info("邮箱-修改密码：耗时：{}，bodyStr：{}", ApiTestHelper.calcCostMs(currentTs), bodyStr);

    }

    /**
     * 邮箱-修改密码-发送验证码
     */
    private static void emailUpdatePasswordSendCode(String apiEndpoint, String jwt) {

        long currentTs = System.currentTimeMillis();

        String bodyStr =
            HttpRequest.post(apiEndpoint + "/sign/email/sign/up/sendCode").header("Authorization", jwt).execute()
                .body();

        log.info("邮箱-修改密码-发送验证码：耗时：{}，bodyStr：{}", ApiTestHelper.calcCostMs(currentTs), bodyStr);

    }

    /**
     * 邮箱-账号密码登录
     */
    private static String emailSignIn(String apiEndpoint, String email, String passwordTemp, String rsaPublicKey) {

        long currentTs = System.currentTimeMillis();

        String password = DigestUtil.sha256Hex((DigestUtil.sha512Hex(passwordTemp)));

        password = MyRsaUtil.rsaEncrypt(password, rsaPublicKey);

        SignEmailSignInPasswordDTO dto = new SignEmailSignInPasswordDTO();
        dto.setPassword(password);
        dto.setEmail(email);

        String bodyStr =
            HttpRequest.post(apiEndpoint + "/sign/email/sign/in/password").body(JSONUtil.toJsonStr(dto)).execute()
                .body();

        log.info("邮箱-账号密码登录：耗时：{}，bodyStr：{}", ApiTestHelper.calcCostMs(currentTs), bodyStr);

        return JSONUtil.parseObj(bodyStr).getStr("data");

    }

    /**
     * 邮箱-注册-发送验证码
     */
    private static void emailSignUpSendCode(String apiEndpoint, String email) {

        long currentTs = System.currentTimeMillis();

        EmailNotBlankDTO dto = new EmailNotBlankDTO();
        dto.setEmail(email);

        String bodyStr =
            HttpRequest.post(apiEndpoint + "/sign/email/sign/up/sendCode").body(JSONUtil.toJsonStr(dto)).execute()
                .body();

        log.info("邮箱-注册-发送验证码：耗时：{}，bodyStr：{}", ApiTestHelper.calcCostMs(currentTs), bodyStr);

    }

    /**
     * 邮箱-注册
     */
    private static void emailSignUp(String apiEndpoint, String email, String passwordTemp, String rsaPublicKey,
        String code) {

        long currentTs = System.currentTimeMillis();

        String originPassword = MyRsaUtil.rsaEncrypt(passwordTemp, rsaPublicKey);

        String password = DigestUtil.sha256Hex((DigestUtil.sha512Hex(passwordTemp)));

        password = MyRsaUtil.rsaEncrypt(password, rsaPublicKey);

        SignEmailSignUpDTO dto = new SignEmailSignUpDTO();
        dto.setCode(code);
        dto.setPassword(password);
        dto.setOriginPassword(originPassword);
        dto.setEmail(email);

        String bodyStr =
            HttpRequest.post(apiEndpoint + "/sign/email/sign/up").body(JSONUtil.toJsonStr(dto)).execute().body();

        log.info("邮箱-注册：耗时：{}，bodyStr：{}", ApiTestHelper.calcCostMs(currentTs), bodyStr);

    }

}
