package com.cmcorg20230301.be.engine.other.app.properties;

import com.cmcorg20230301.be.engine.model.model.constant.PropertiesPrefixConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = PropertiesPrefixConstant.OFFICIAL_ACCOUNT)
@RefreshScope
public class SysOtherAppOfficialAccountProperties {

    @Schema(description = "token：一般为 32位无横杠的 uuid")
    private String token;

    @Schema(description = "回调消息加解密参数是AES密钥的Base64编码，用于解密回调消息内容对应的密文")
    private String encodingAesKey;

    @Schema(description = "企业 id")
    private String corpId;

}
