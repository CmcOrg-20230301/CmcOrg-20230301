package com.cmcorg20230301.be.engine.security.properties;

import cn.hutool.crypto.digest.DigestUtil;
import com.cmcorg20230301.be.engine.model.model.constant.PropertiesPrefixConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = PropertiesPrefixConstant.SECURITY)
@RefreshScope
public class SecurityProperties {

    @Schema(description = "jwt 密钥前缀")
    private String jwtSecretPre =
        "202e5c4e94c60b8e96cc6c8c2471309c11123a39ef996dd5ab3b180ba9a0ddcefe99123edeff516e1d3d264f8dde85eaf6ace1ea236d826fda32080d00f64b47ad0111";

    @Schema(description = "是否允许：admin登录")
    private Boolean adminEnable = true;

    @Schema(description = "admin 的昵称")
    private String adminNickname = "admin";

    @Schema(description = "admin 的密码，默认为 suancai，下面是 suancai经过 sha加密之后的字符串，加密次数和方法和前端需进行统一，输入 suancai即可登录，也可以使用本类的 generateAdminPassword方法，快速生成新的 admin密码")
    private String adminPassword = "89750f4648ab240704529a1504ac8bbb4c85abd9b88522cef992eee8eb2304b2";

    @Schema(description = "是否启用：用户名注册功能，默认启用")
    private Boolean signInNameSignUpEnable = true;

    @Schema(description = "是否启用：邮箱注册功能，默认启用")
    private Boolean emailSignUpEnable = true;

    @Schema(description = "是否启用：手机号码注册功能，默认启用")
    private Boolean phoneSignUpEnable = true;

    @Schema(description = "是否启用：ipFilter，默认启用")
    private Boolean ipFilterEnable = true;

    public static void main(String[] args) {

        generateAdminPassword();

    }

    /**
     * 生成 admin的密码
     * 备注：需要和前端一致：先 512，然后再 256
     */
    private static void generateAdminPassword() {

        String password = "suancai";

        password = DigestUtil.sha256Hex((DigestUtil.sha512Hex(password)));

        System.out.println(password); // 89750f4648ab240704529a1504ac8bbb4c85abd9b88522cef992eee8eb2304b2

    }

}
