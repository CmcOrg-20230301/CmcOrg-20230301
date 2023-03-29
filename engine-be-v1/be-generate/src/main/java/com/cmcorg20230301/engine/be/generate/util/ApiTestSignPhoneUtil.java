package com.cmcorg20230301.engine.be.generate.util;

import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import cn.hutool.setting.Setting;
import com.cmcorg20230301.engine.be.model.model.dto.NotBlankCodeDTO;
import com.cmcorg20230301.engine.be.security.util.MyRsaUtil;
import com.cmcorg20230301.engine.be.sign.phone.model.dto.*;
import lombok.extern.slf4j.Slf4j;

/**
 * 手机号，登录注册相关接口测试工具
 */
@Slf4j
public class ApiTestSignPhoneUtil {

    // 执行，接口的地址，备注：最后面不要加斜杠 /
    //    private static final String API_ENDPOINT = "http://43.154.37.130:10001";
    private static final String API_ENDPOINT = "http://127.0.0.1:10001";

    // 配置文件
    private static final Setting SETTING = new Setting("sign.setting");

    // 手机号
    public static final String PHONE = SETTING.getStr("phone");

    // 新手机号
    private static final String NEW_PHONE = SETTING.getStr("newPhone");

    // 密码
    private static final String PASSWORD_TEMP = "Ik123456";

    // 新密码
    private static final String NEW_PASSWORD_TEMP = "Ik1234567";

    // 新密码-2
    private static final String NEW_PASSWORD_2_TEMP = "Ik12345678";

    public static void main(String[] args) {

        // 执行
        exec(API_ENDPOINT, PHONE, PASSWORD_TEMP, NEW_PHONE, NEW_PASSWORD_TEMP, ApiTestHelper.RSA_PUBLIC_KEY,
            NEW_PASSWORD_2_TEMP);

    }

    /**
     * 执行
     */
    private static void exec(String apiEndpoint, String phone, String passwordTemp, String newPhone,
        String newPasswordTemp, String rsaPublicKey, String newPassword2Temp) {

        // 手机号-注册-发送验证码
        phoneSignUpSendCode(apiEndpoint, phone);

        String code = ApiTestHelper.getStringFromScanner("请输入验证码");

        // 手机号-注册
        phoneSignUp(apiEndpoint, phone, passwordTemp, rsaPublicKey, code);

        // 手机号-账号密码登录
        String jwt = phoneSignInPassword(apiEndpoint, phone, passwordTemp, rsaPublicKey);

        // 手机号-修改密码-发送验证码
        phoneUpdatePasswordSendCode(apiEndpoint, jwt);

        code = ApiTestHelper.getStringFromScanner("请输入验证码");

        // 手机号-修改密码
        phoneUpdatePassword(apiEndpoint, jwt, code, newPasswordTemp, rsaPublicKey);

        // 手机号-账号密码登录
        jwt = phoneSignInPassword(apiEndpoint, phone, newPasswordTemp, rsaPublicKey);

        // 手机号-修改手机-发送验证码
        phoneUpdateAccountSendCode(apiEndpoint, jwt);

        // 手机号-注册-发送验证码
        phoneSignUpSendCode(apiEndpoint, newPhone);

        code = ApiTestHelper.getStringFromScanner("请输入旧手机验证码");

        String newCode = ApiTestHelper.getStringFromScanner("请输入新手机验证码");

        // 手机号-修改手机
        phoneUpdateAccount(apiEndpoint, jwt, newPhone, code, newCode);

        // 手机号-忘记密码-发送验证码
        phoneForgetPasswordSendCode(apiEndpoint, newPhone);

        code = ApiTestHelper.getStringFromScanner("请输入验证码");

        // 手机号-忘记密码
        phoneForgetPassword(apiEndpoint, newPhone, code, newPassword2Temp, rsaPublicKey);

        //        // 手机号-账号密码登录
        //        jwt = phoneSignInPassword(apiEndpoint, newPhone, newPassword2Temp, rsaPublicKey);

        // 手机号-账号注销-发送验证码
        phoneSignDeleteSendCode(apiEndpoint, jwt);

        code = ApiTestHelper.getStringFromScanner("请输入验证码");

        // 手机号-账号注销
        phoneSignDelete(apiEndpoint, jwt, code);

    }

    /**
     * 手机号-账号注销
     */
    private static void phoneSignDelete(String apiEndpoint, String jwt, String code) {

        long currentTs = System.currentTimeMillis();

        NotBlankCodeDTO dto = new NotBlankCodeDTO();
        dto.setCode(code);

        String bodyStr = HttpRequest.post(apiEndpoint + "/sign/phone/signDelete").header("Authorization", jwt)
            .body(JSONUtil.toJsonStr(dto)).execute().body();

        log.info("手机号-账号注销：耗时：{}，bodyStr：{}", ApiTestHelper.calcCostMs(currentTs), bodyStr);

    }

    /**
     * 手机号-账号注销-发送验证码
     */
    private static void phoneSignDeleteSendCode(String apiEndpoint, String jwt) {

        long currentTs = System.currentTimeMillis();

        String bodyStr =
            HttpRequest.post(apiEndpoint + "/sign/phone/signDelete/sendCode").header("Authorization", jwt).execute()
                .body();

        log.info("手机号-账号注销-发送验证码：耗时：{}，bodyStr：{}", ApiTestHelper.calcCostMs(currentTs), bodyStr);

    }

    /**
     * 手机号-忘记密码
     */
    private static void phoneForgetPassword(String apiEndpoint, String newPhone, String code, String newPassword2Temp,
        String rsaPublicKey) {

        long currentTs = System.currentTimeMillis();

        String originPassword = MyRsaUtil.rsaEncrypt(newPassword2Temp, rsaPublicKey);

        String password = DigestUtil.sha256Hex((DigestUtil.sha512Hex(newPassword2Temp)));

        password = MyRsaUtil.rsaEncrypt(password, rsaPublicKey);

        SignPhoneForgetPasswordDTO dto = new SignPhoneForgetPasswordDTO();
        dto.setCode(code);
        dto.setNewPassword(password);
        dto.setOriginNewPassword(originPassword);
        dto.setPhone(newPhone);

        String bodyStr =
            HttpRequest.post(apiEndpoint + "/sign/phone/forgetPassword").body(JSONUtil.toJsonStr(dto)).execute().body();

        log.info("手机号-忘记密码：耗时：{}，bodyStr：{}", ApiTestHelper.calcCostMs(currentTs), bodyStr);

    }

    /**
     * 手机号-忘记密码-发送验证码
     */
    private static void phoneForgetPasswordSendCode(String apiEndpoint, String newPhone) {

        long currentTs = System.currentTimeMillis();

        PhoneNotBlankDTO dto = new PhoneNotBlankDTO();
        dto.setPhone(newPhone);

        String bodyStr =
            HttpRequest.post(apiEndpoint + "/sign/phone/forgetPassword/sendCode").body(JSONUtil.toJsonStr(dto))
                .execute().body();

        log.info("手机号-忘记密码-发送验证码：耗时：{}，bodyStr：{}", ApiTestHelper.calcCostMs(currentTs), bodyStr);

    }

    /**
     * 手机号-修改手机
     */
    private static void phoneUpdateAccount(String apiEndpoint, String jwt, String newPhone, String code,
        String newCode) {

        long currentTs = System.currentTimeMillis();

        SignPhoneUpdateAccountDTO dto = new SignPhoneUpdateAccountDTO();
        dto.setNewPhone(newPhone);
        dto.setOldPhoneCode(code);
        dto.setNewPhoneCode(newCode);

        String bodyStr = HttpRequest.post(apiEndpoint + "/sign/phone/updateAccount").body(JSONUtil.toJsonStr(dto))
            .header("Authorization", jwt).execute().body();

        log.info("手机号-修改手机：耗时：{}，bodyStr：{}", ApiTestHelper.calcCostMs(currentTs), bodyStr);

    }

    /**
     * 手机号-修改手机-发送验证码
     */
    private static void phoneUpdateAccountSendCode(String apiEndpoint, String jwt) {

        long currentTs = System.currentTimeMillis();

        String bodyStr =
            HttpRequest.post(apiEndpoint + "/sign/phone/updateAccount/sendCode").header("Authorization", jwt).execute()
                .body();

        log.info("手机号-修改手机-发送验证码：耗时：{}，bodyStr：{}", ApiTestHelper.calcCostMs(currentTs), bodyStr);

    }

    /**
     * 手机号-修改密码
     */
    private static void phoneUpdatePassword(String apiEndpoint, String jwt, String code, String newPasswordTemp,
        String rsaPublicKey) {

        long currentTs = System.currentTimeMillis();

        String originNewPassword = MyRsaUtil.rsaEncrypt(newPasswordTemp, rsaPublicKey);

        String newPassword = DigestUtil.sha256Hex((DigestUtil.sha512Hex(newPasswordTemp)));

        newPassword = MyRsaUtil.rsaEncrypt(newPassword, rsaPublicKey);

        SignPhoneUpdatePasswordDTO dto = new SignPhoneUpdatePasswordDTO();
        dto.setCode(code);
        dto.setNewPassword(newPassword);
        dto.setOriginNewPassword(originNewPassword);

        String bodyStr = HttpRequest.post(apiEndpoint + "/sign/phone/updatePassword").body(JSONUtil.toJsonStr(dto))
            .header("Authorization", jwt).execute().body();

        log.info("手机号-修改密码：耗时：{}，bodyStr：{}", ApiTestHelper.calcCostMs(currentTs), bodyStr);

    }

    /**
     * 手机号-修改密码-发送验证码
     */
    private static void phoneUpdatePasswordSendCode(String apiEndpoint, String jwt) {

        long currentTs = System.currentTimeMillis();

        String bodyStr =
            HttpRequest.post(apiEndpoint + "/sign/phone/updatePassword/sendCode").header("Authorization", jwt).execute()
                .body();

        log.info("手机号-修改密码-发送验证码：耗时：{}，bodyStr：{}", ApiTestHelper.calcCostMs(currentTs), bodyStr);

    }

    /**
     * 手机号-账号密码登录
     */
    private static String phoneSignInPassword(String apiEndpoint, String phone, String passwordTemp,
        String rsaPublicKey) {

        long currentTs = System.currentTimeMillis();

        String password = DigestUtil.sha256Hex((DigestUtil.sha512Hex(passwordTemp)));

        password = MyRsaUtil.rsaEncrypt(password, rsaPublicKey);

        SignPhoneSignInPasswordDTO dto = new SignPhoneSignInPasswordDTO();
        dto.setPassword(password);
        dto.setPhone(phone);

        String bodyStr =
            HttpRequest.post(apiEndpoint + "/sign/phone/sign/in/password").body(JSONUtil.toJsonStr(dto)).execute()
                .body();

        log.info("手机号-账号密码登录：耗时：{}，bodyStr：{}", ApiTestHelper.calcCostMs(currentTs), bodyStr);

        return JSONUtil.parseObj(bodyStr).getStr("data");

    }

    /**
     * 手机号-注册
     */
    private static void phoneSignUp(String apiEndpoint, String phone, String passwordTemp, String rsaPublicKey,
        String code) {

        long currentTs = System.currentTimeMillis();

        String originPassword = MyRsaUtil.rsaEncrypt(passwordTemp, rsaPublicKey);

        String password = DigestUtil.sha256Hex((DigestUtil.sha512Hex(passwordTemp)));

        password = MyRsaUtil.rsaEncrypt(password, rsaPublicKey);

        SignPhoneSignUpDTO dto = new SignPhoneSignUpDTO();
        dto.setCode(code);
        dto.setPassword(password);
        dto.setOriginPassword(originPassword);
        dto.setPhone(phone);

        String bodyStr =
            HttpRequest.post(apiEndpoint + "/sign/phone/sign/up").body(JSONUtil.toJsonStr(dto)).execute().body();

        log.info("手机号-注册：耗时：{}，bodyStr：{}", ApiTestHelper.calcCostMs(currentTs), bodyStr);

    }

    /**
     * 手机号-注册-发送验证码
     */
    private static void phoneSignUpSendCode(String apiEndpoint, String phone) {

        long currentTs = System.currentTimeMillis();

        PhoneNotBlankDTO dto = new PhoneNotBlankDTO();
        dto.setPhone(phone);

        String bodyStr =
            HttpRequest.post(apiEndpoint + "/sign/phone/sign/up/sendCode").body(JSONUtil.toJsonStr(dto)).execute()
                .body();

        log.info("手机号-注册-发送验证码：耗时：{}，bodyStr：{}", ApiTestHelper.calcCostMs(currentTs), bodyStr);

    }

}
