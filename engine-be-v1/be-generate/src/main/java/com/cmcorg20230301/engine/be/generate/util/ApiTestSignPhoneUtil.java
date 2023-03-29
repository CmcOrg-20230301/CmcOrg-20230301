package com.cmcorg20230301.engine.be.generate.util;

import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import cn.hutool.setting.Setting;
import com.cmcorg20230301.engine.be.security.util.MyRsaUtil;
import com.cmcorg20230301.engine.be.sign.phone.model.dto.PhoneNotBlankDTO;
import com.cmcorg20230301.engine.be.sign.phone.model.dto.SignPhoneSignUpDTO;
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
    private static final String PHONE = SETTING.getStr("phone");

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
