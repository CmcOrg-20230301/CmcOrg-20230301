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

}
