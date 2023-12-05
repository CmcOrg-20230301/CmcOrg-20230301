package com.cmcorg20230301.be.engine.generate.util.apitest.sign;

import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import cn.hutool.setting.Setting;
import com.cmcorg20230301.be.engine.generate.util.apitest.ApiTestHelper;
import com.cmcorg20230301.be.engine.model.model.dto.NotBlankCodeDTO;
import com.cmcorg20230301.be.engine.model.model.vo.SignInVO;
import com.cmcorg20230301.be.engine.security.util.MyRsaUtil;
import com.cmcorg20230301.be.engine.sign.email.model.dto.*;
import lombok.extern.slf4j.Slf4j;

/**
 * 邮箱，登录注册相关接口测试工具
 */
@Slf4j
public class ApiTestSignEmailUtil {

    // 执行，接口的地址，备注：最后面不要加斜杠 /
    //    private static final String API_ENDPOINT = "http://43.154.37.130:10001";
    private static final String API_ENDPOINT = "http://127.0.0.1:10001";

    // 配置文件
    private static final Setting SETTING = new Setting("sign.setting");

    // 邮箱
    private static final String EMAIL = SETTING.getStr("email");

    // 新邮箱
    private static final String NEW_EMAIL = SETTING.getStr("newEmail");

    // 密码
    private static final String PASSWORD_TEMP = "Ik123456";

    // 新密码
    private static final String NEW_PASSWORD_TEMP = "Ik1234567";

    // 新密码-2
    private static final String NEW_PASSWORD_2_TEMP = "Ik12345678";

    public static void main(String[] args) {

        // 执行
        exec(API_ENDPOINT, EMAIL, PASSWORD_TEMP, NEW_EMAIL, NEW_PASSWORD_TEMP, ApiTestHelper.RSA_PUBLIC_KEY,
            NEW_PASSWORD_2_TEMP);

    }

    /**
     * 执行
     */
    private static void exec(String apiEndpoint, String email, String passwordTemp, String newEmail,
        String newPasswordTemp, String rsaPublicKey, String newPassword2Temp) {

        // 邮箱-注册-发送验证码
        emailSignUpSendCode(apiEndpoint, email);

        String code = ApiTestHelper.getStrFromScanner("请输入验证码");

        // 邮箱-注册
        emailSignUp(apiEndpoint, email, passwordTemp, rsaPublicKey, code);

        // 邮箱-账号密码登录
        SignInVO signInVO = emailSignIn(apiEndpoint, email, passwordTemp, rsaPublicKey);

        // 邮箱-修改密码-发送验证码
        emailUpdatePasswordSendCode(apiEndpoint, signInVO.getJwt());

        code = ApiTestHelper.getStrFromScanner("请输入验证码");

        // 邮箱-修改密码
        emailUpdatePassword(apiEndpoint, signInVO.getJwt(), newPasswordTemp, rsaPublicKey, code);

        // 邮箱-账号密码登录
        signInVO = emailSignIn(apiEndpoint, email, newPasswordTemp, rsaPublicKey);

        // 邮箱-修改邮箱-发送验证码
        emailUpdateAccountSendCode(apiEndpoint, signInVO.getJwt());

        // 邮箱-注册-发送验证码
        emailSignUpSendCode(apiEndpoint, newEmail);

        code = ApiTestHelper.getStrFromScanner("请输入旧邮箱验证码");

        String newCode = ApiTestHelper.getStrFromScanner("请输入新邮箱验证码");

        // 邮箱-修改邮箱
        emailUpdateAccount(apiEndpoint, signInVO.getJwt(), newEmail, code, newCode);

        // 邮箱-忘记密码-发送验证码
        emailForgetPasswordSendCode(apiEndpoint, newEmail);

        code = ApiTestHelper.getStrFromScanner("请输入验证码");

        // 邮箱-忘记密码
        emailForgetPassword(apiEndpoint, newEmail, code, newPassword2Temp, rsaPublicKey);

        // 邮箱-账号密码登录
        signInVO = emailSignIn(apiEndpoint, newEmail, newPassword2Temp, rsaPublicKey);

        // 邮箱-账号注销-发送验证码
        emailSignDeleteSendCode(apiEndpoint, signInVO.getJwt());

        code = ApiTestHelper.getStrFromScanner("请输入验证码");

        // 邮箱-账号注销
        emailSignDelete(apiEndpoint, signInVO.getJwt(), code);

    }

    /**
     * 邮箱-账号注销
     */
    private static void emailSignDelete(String apiEndpoint, String jwt, String code) {

        long currentTs = System.currentTimeMillis();

        NotBlankCodeDTO dto = new NotBlankCodeDTO();
        dto.setCode(code);

        String bodyStr = HttpRequest.post(apiEndpoint + "/sign/email/signDelete").header("Authorization", jwt)
            .body(JSONUtil.toJsonStr(dto)).execute().body();

        log.info("邮箱-账号注销：耗时：{}，bodyStr：{}", ApiTestHelper.calcCostMs(currentTs), bodyStr);

    }

    /**
     * 邮箱-账号注销-发送验证码
     */
    private static void emailSignDeleteSendCode(String apiEndpoint, String jwt) {

        long currentTs = System.currentTimeMillis();

        String bodyStr =
            HttpRequest.post(apiEndpoint + "/sign/email/signDelete/sendCode").header("Authorization", jwt).execute()
                .body();

        log.info("邮箱-账号注销-发送验证码：耗时：{}，bodyStr：{}", ApiTestHelper.calcCostMs(currentTs), bodyStr);

    }

    /**
     * 邮箱-忘记密码
     */
    private static void emailForgetPassword(String apiEndpoint, String newEmail, String code, String newPassword2Temp,
        String rsaPublicKey) {

        long currentTs = System.currentTimeMillis();

        String originPassword = MyRsaUtil.rsaEncrypt(newPassword2Temp, rsaPublicKey);

        String password = DigestUtil.sha256Hex((DigestUtil.sha512Hex(newPassword2Temp)));

        password = MyRsaUtil.rsaEncrypt(password, rsaPublicKey);

        SignEmailForgetPasswordDTO dto = new SignEmailForgetPasswordDTO();
        dto.setCode(code);
        dto.setNewPassword(password);
        dto.setOriginNewPassword(originPassword);
        dto.setEmail(newEmail);

        String bodyStr =
            HttpRequest.post(apiEndpoint + "/sign/email/forgetPassword").body(JSONUtil.toJsonStr(dto)).execute().body();

        log.info("邮箱-忘记密码：耗时：{}，bodyStr：{}", ApiTestHelper.calcCostMs(currentTs), bodyStr);

    }

    /**
     * 邮箱-忘记密码-发送验证码
     */
    private static void emailForgetPasswordSendCode(String apiEndpoint, String newEmail) {

        long currentTs = System.currentTimeMillis();

        EmailNotBlankDTO dto = new EmailNotBlankDTO();
        dto.setEmail(newEmail);

        String bodyStr =
            HttpRequest.post(apiEndpoint + "/sign/email/forgetPassword/sendCode").body(JSONUtil.toJsonStr(dto))
                .execute().body();

        log.info("邮箱-忘记密码-发送验证码：耗时：{}，bodyStr：{}", ApiTestHelper.calcCostMs(currentTs), bodyStr);

    }

    /**
     * 邮箱-修改邮箱
     */
    private static void emailUpdateAccount(String apiEndpoint, String jwt, String newEmail, String code,
        String newCode) {

        long currentTs = System.currentTimeMillis();

        SignEmailUpdateAccountDTO dto = new SignEmailUpdateAccountDTO();
        dto.setNewEmail(newEmail);
        dto.setOldEmailCode(code);
        dto.setNewEmailCode(newCode);

        String bodyStr = HttpRequest.post(apiEndpoint + "/sign/email/updateAccount").body(JSONUtil.toJsonStr(dto))
            .header("Authorization", jwt).execute().body();

        log.info("邮箱-修改邮箱：耗时：{}，bodyStr：{}", ApiTestHelper.calcCostMs(currentTs), bodyStr);

    }

    /**
     * 邮箱-修改邮箱-发送验证码
     */
    private static void emailUpdateAccountSendCode(String apiEndpoint, String jwt) {

        long currentTs = System.currentTimeMillis();

        String bodyStr =
            HttpRequest.post(apiEndpoint + "/sign/email/updateAccount/sendCode").header("Authorization", jwt).execute()
                .body();

        log.info("邮箱-修改邮箱-发送验证码：耗时：{}，bodyStr：{}", ApiTestHelper.calcCostMs(currentTs), bodyStr);

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
            HttpRequest.post(apiEndpoint + "/sign/email/updatePassword/sendCode").header("Authorization", jwt).execute()
                .body();

        log.info("邮箱-修改密码-发送验证码：耗时：{}，bodyStr：{}", ApiTestHelper.calcCostMs(currentTs), bodyStr);

    }

    /**
     * 邮箱-账号密码登录
     */
    private static SignInVO emailSignIn(String apiEndpoint, String email, String passwordTemp, String rsaPublicKey) {

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

        return JSONUtil.parseObj(bodyStr).get("data", SignInVO.class);

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
