package com.cmcorg20230301.engine.be.generate.util;

import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import com.cmcorg20230301.engine.be.security.util.MyRsaUtil;
import com.cmcorg20230301.engine.be.sign.email.model.dto.EmailNotBlankDTO;
import com.cmcorg20230301.engine.be.sign.email.model.dto.SignEmailBindAccountDTO;
import com.cmcorg20230301.engine.be.sign.signinname.model.dto.*;
import lombok.extern.slf4j.Slf4j;

/**
 * 登录名，登录注册相关接口测试工具
 */
@Slf4j
public class ApiTestSignSignInNameUtil {

    // 执行，接口的地址，备注：最后面不要加斜杠 /
    //    private static final String API_ENDPOINT = "http://43.154.37.130:10001";
    private static final String API_ENDPOINT = "http://127.0.0.1:10001";

    // 登录名
    private static final String SIGN_IN_NAME = "cxk";

    // 新登录名
    private static final String NEW_SIGN_IN_NAME = "cxk2";

    // 密码
    private static final String PASSWORD_TEMP = "Ik123456";

    // 新密码
    private static final String NEW_PASSWORD_TEMP = "Ik1234567";

    // 邮箱
    private static final String EMAIL = "dimensional_logic@qq.com";

    public static void main(String[] args) {

        // 执行
        exec(API_ENDPOINT, SIGN_IN_NAME, PASSWORD_TEMP, NEW_SIGN_IN_NAME, NEW_PASSWORD_TEMP,
            ApiTestHelper.RSA_PUBLIC_KEY, EMAIL);

    }

    /**
     * 执行
     */
    private static void exec(String apiEndpoint, String signInName, String passwordTemp, String newSignInName,
        String newPasswordTemp, String rsaPublicKey, String email) {

        // 登录名-注册
        signInNameSignUp(apiEndpoint, signInName, passwordTemp, rsaPublicKey);

        // 登录名-用户名账号密码登录
        String jwt = signInNameSignIn(apiEndpoint, signInName, passwordTemp, rsaPublicKey);

        // 登录名-修改密码
        signInNameUpdatePassword(apiEndpoint, jwt, passwordTemp, newPasswordTemp, rsaPublicKey);

        // 登录名-用户名账号密码登录
        jwt = signInNameSignIn(apiEndpoint, signInName, newPasswordTemp, rsaPublicKey);

        // 登录名-修改账号
        signInNameUpdateAccount(apiEndpoint, jwt, newSignInName, newPasswordTemp, rsaPublicKey);

        // 登录名-用户名账号密码登录
        jwt = signInNameSignIn(apiEndpoint, newSignInName, newPasswordTemp, rsaPublicKey);

        // 绑定邮箱-发送验证码
        emailBindAccountSendCode(apiEndpoint, jwt, email);

        String code = ApiTestHelper.getStringFromScanner("请输入验证码");

        // 绑定邮箱
        emailBindAccount(apiEndpoint, jwt, email, code);

        // 登录名-用户名账号密码登录
        jwt = signInNameSignIn(apiEndpoint, newSignInName, newPasswordTemp, rsaPublicKey);

        // 登录名-账号注销
        signInNameSignDelete(apiEndpoint, newPasswordTemp, jwt, rsaPublicKey);

    }

    /**
     * 绑定邮箱
     */
    private static void emailBindAccount(String apiEndpoint, String jwt, String email, String code) {

        long currentTs = System.currentTimeMillis();

        SignEmailBindAccountDTO dto = new SignEmailBindAccountDTO();
        dto.setCode(code);
        dto.setEmail(email);

        String bodyStr = HttpRequest.post(apiEndpoint + "/sign/email/bindAccount").header("Authorization", jwt)
            .body(JSONUtil.toJsonStr(dto)).execute().body();

        log.info("绑定邮箱：耗时：{}，bodyStr：{}", ApiTestHelper.calcCostMs(currentTs), bodyStr);

    }

    /**
     * 绑定邮箱-发送验证码
     */
    private static void emailBindAccountSendCode(String apiEndpoint, String jwt, String email) {

        long currentTs = System.currentTimeMillis();

        EmailNotBlankDTO dto = new EmailNotBlankDTO();
        dto.setEmail(email);

        String bodyStr = HttpRequest.post(apiEndpoint + "/sign/email/bindAccount/sendCode").header("Authorization", jwt)
            .body(JSONUtil.toJsonStr(dto)).execute().body();

        log.info("绑定邮箱-发送验证码：耗时：{}，bodyStr：{}", ApiTestHelper.calcCostMs(currentTs), bodyStr);

    }

    /**
     * 登录名-账号注销
     */
    private static void signInNameSignDelete(String apiEndpoint, String currentPasswordTemp, String jwt,
        String rsaPublicKey) {

        long currentTs = System.currentTimeMillis();

        String currentPassword = DigestUtil.sha256Hex((DigestUtil.sha512Hex(currentPasswordTemp)));

        currentPassword = MyRsaUtil.rsaEncrypt(currentPassword, rsaPublicKey);

        SignSignInNameSignDeleteDTO dto = new SignSignInNameSignDeleteDTO();
        dto.setCurrentPassword(currentPassword);

        String bodyStr = HttpRequest.post(apiEndpoint + "/sign/signInName/signDelete").body(JSONUtil.toJsonStr(dto))
            .header("Authorization", jwt).execute().body();

        log.info("登录名-账号注销：耗时：{}，bodyStr：{}", ApiTestHelper.calcCostMs(currentTs), bodyStr);

    }

    /**
     * 登录名-修改账号
     */
    private static void signInNameUpdateAccount(String apiEndpoint, String jwt, String newSignInName,
        String currentPasswordTemp, String rsaPublicKey) {

        long currentTs = System.currentTimeMillis();

        String currentPassword = DigestUtil.sha256Hex((DigestUtil.sha512Hex(currentPasswordTemp)));

        currentPassword = MyRsaUtil.rsaEncrypt(currentPassword, rsaPublicKey);

        SignSignInNameUpdateAccountDTO dto = new SignSignInNameUpdateAccountDTO();
        dto.setNewSignInName(newSignInName);
        dto.setCurrentPassword(currentPassword);

        String bodyStr = HttpRequest.post(apiEndpoint + "/sign/signInName/updateAccount").body(JSONUtil.toJsonStr(dto))
            .header("Authorization", jwt).execute().body();

        log.info("登录名-修改账号：耗时：{}，bodyStr：{}", ApiTestHelper.calcCostMs(currentTs), bodyStr);

    }

    /**
     * 登录名-修改密码
     */
    private static void signInNameUpdatePassword(String apiEndpoint, String jwt, String passwordTemp,
        String newPasswordTemp, String rsaPublicKey) {

        long currentTs = System.currentTimeMillis();

        String oldPassword = DigestUtil.sha256Hex((DigestUtil.sha512Hex(passwordTemp)));

        oldPassword = MyRsaUtil.rsaEncrypt(oldPassword, rsaPublicKey);

        String originNewPassword = MyRsaUtil.rsaEncrypt(newPasswordTemp, rsaPublicKey);

        String newPassword = DigestUtil.sha256Hex((DigestUtil.sha512Hex(newPasswordTemp)));

        newPassword = MyRsaUtil.rsaEncrypt(newPassword, rsaPublicKey);

        SignSignInNameUpdatePasswordDTO dto = new SignSignInNameUpdatePasswordDTO();
        dto.setOldPassword(oldPassword);
        dto.setNewPassword(newPassword);
        dto.setOriginNewPassword(originNewPassword);

        String bodyStr = HttpRequest.post(apiEndpoint + "/sign/signInName/updatePassword").body(JSONUtil.toJsonStr(dto))
            .header("Authorization", jwt).execute().body();

        log.info("登录名-修改密码：耗时：{}，bodyStr：{}", ApiTestHelper.calcCostMs(currentTs), bodyStr);

    }

    /**
     * 登录名-用户名账号密码登录
     */
    private static String signInNameSignIn(String apiEndpoint, String signInName, String passwordTemp,
        String rsaPublicKey) {

        long currentTs = System.currentTimeMillis();

        String password = DigestUtil.sha256Hex((DigestUtil.sha512Hex(passwordTemp)));

        password = MyRsaUtil.rsaEncrypt(password, rsaPublicKey);

        SignSignInNameSignInPasswordDTO dto = new SignSignInNameSignInPasswordDTO();
        dto.setPassword(password);
        dto.setSignInName(signInName);

        String bodyStr =
            HttpRequest.post(apiEndpoint + "/sign/signInName/sign/in/password").body(JSONUtil.toJsonStr(dto)).execute()
                .body();

        log.info("登录名-用户名账号密码登录：耗时：{}，bodyStr：{}", ApiTestHelper.calcCostMs(currentTs), bodyStr);

        return JSONUtil.parseObj(bodyStr).getStr("data");

    }

    /**
     * 登录名-注册
     */
    private static void signInNameSignUp(String apiEndpoint, String signInName, String passwordTemp,
        String rsaPublicKey) {

        long currentTs = System.currentTimeMillis();

        String originPassword = MyRsaUtil.rsaEncrypt(passwordTemp, rsaPublicKey);

        String password = DigestUtil.sha256Hex((DigestUtil.sha512Hex(passwordTemp)));

        password = MyRsaUtil.rsaEncrypt(password, rsaPublicKey);

        SignSignInNameSignUpDTO dto = new SignSignInNameSignUpDTO();
        dto.setPassword(password);
        dto.setOriginPassword(originPassword);
        dto.setSignInName(signInName);

        String bodyStr =
            HttpRequest.post(apiEndpoint + "/sign/signInName/sign/up").body(JSONUtil.toJsonStr(dto)).execute().body();

        log.info("登录名-注册：耗时：{}，bodyStr：{}", ApiTestHelper.calcCostMs(currentTs), bodyStr);

    }

}
