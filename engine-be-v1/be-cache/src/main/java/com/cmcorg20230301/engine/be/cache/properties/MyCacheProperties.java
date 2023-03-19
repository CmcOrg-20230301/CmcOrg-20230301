package com.cmcorg20230301.engine.be.cache.properties;

import com.cmcorg20230301.engine.be.model.model.constant.PropertiesPrefixConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = PropertiesPrefixConstant.CACHE)
public class MyCacheProperties {

    @Schema(description = "需要缓存的数据库名")
    private String databaseName = "engine_be_v1";

}
