package com.cmcorg20230301.engine.be.generate.util;

import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import com.cmcorg20230301.engine.be.security.model.vo.ApiResultVO;
import com.cmcorg20230301.engine.be.security.util.MyRsaUtil;
import com.cmcorg20230301.engine.be.sign.signinname.model.dto.SignSignInNameSignInPasswordDTO;
import com.cmcorg20230301.engine.be.sign.signinname.model.dto.SignSignInNameSignUpDTO;
import lombok.extern.slf4j.Slf4j;

/**
 * 登录注册相关接口测试工具
 */
@Slf4j
public class ApiTestSignUtil {

    // 执行，接口的地址，备注：最后面不要加斜杠 /
    private static final String API_ENDPOINT = "http://43.154.37.130:10001";
    //    private static final String API_ENDPOINT = "http://127.0.0.1.130:10001";

    // rsa公钥
    private static final String RSA_PUBLIC_KEY =
        "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDadmaCaffN63JC5QsMK/+le5voCB4DzOsV9xOBZgGJyqnizh9/UcFkIoRae5rebdWUtnPO4CTgdJbuSvu/TtIIPj9De5/wiJilFAWd1Ve7qGaxxTxqWwFNp7p/FLr0YpMeBjOylds9GyA1cnjIqruNdYv+qRZnseE0Sq2WEZus9QIDAQAB";

    // 登录名
    private static final String SIGN_IN_NAME = "cxk";

    // 密码
    private static final String PASSWORD_TEMP = "Ik123456";

    public static void main(String[] args) {

        // 用户名注册
        //        signInNameSignUp(API_ENDPOINT, SIGN_IN_NAME);

        // 用户名账号密码登录
        signInNameSignIn(API_ENDPOINT, SIGN_IN_NAME);

    }

    /**
     * 用户名账号密码登录
     */
    private static void signInNameSignIn(String apiEndpoint, String signInName) {

        String password = DigestUtil.sha256Hex((DigestUtil.sha512Hex(PASSWORD_TEMP)));

        password = MyRsaUtil.rsaEncrypt(password, RSA_PUBLIC_KEY);

        SignSignInNameSignInPasswordDTO signSignInNameSignInPasswordDTO = new SignSignInNameSignInPasswordDTO();
        signSignInNameSignInPasswordDTO.setPassword(password);
        signSignInNameSignInPasswordDTO.setSignInName(signInName);

        String bodyStr = HttpRequest.post(apiEndpoint + "/sign/signInName/sign/in/password")
            .body(JSONUtil.toJsonStr(signSignInNameSignInPasswordDTO)).execute().body();

        ApiResultVO<String> apiResultVO = JSONUtil.toBean(bodyStr, ApiResultVO.class);

        if (apiResultVO.getCode() != 200) {
            apiResultVO.end();
        }

        log.info("用户名账号密码登录：bodyStr：{}", bodyStr);

    }

    /**
     * 用户名注册
     */
    private static SignSignInNameSignUpDTO signInNameSignUp(String apiEndpoint, String signInName) {

        String originPassword = MyRsaUtil.rsaEncrypt(PASSWORD_TEMP, RSA_PUBLIC_KEY);

        String password = DigestUtil.sha256Hex((DigestUtil.sha512Hex(PASSWORD_TEMP)));

        password = MyRsaUtil.rsaEncrypt(password, RSA_PUBLIC_KEY);

        SignSignInNameSignUpDTO signSignInNameSignUpDTO = new SignSignInNameSignUpDTO();
        signSignInNameSignUpDTO.setPassword(password);
        signSignInNameSignUpDTO.setOriginPassword(originPassword);
        signSignInNameSignUpDTO.setSignInName(signInName);

        String bodyStr =
            HttpRequest.post(apiEndpoint + "/sign/signInName/sign/up").body(JSONUtil.toJsonStr(signSignInNameSignUpDTO))
                .execute().body();

        ApiResultVO<String> apiResultVO = JSONUtil.toBean(bodyStr, ApiResultVO.class);

        if (apiResultVO.getCode() != 200) {
            apiResultVO.end();
        }

        log.info("用户名注册：bodyStr：{}", bodyStr);

        return signSignInNameSignUpDTO;

    }

}
