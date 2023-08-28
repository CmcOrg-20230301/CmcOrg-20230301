package com.cmcorg20230301.be.engine.cache.properties;

import com.cmcorg20230301.be.engine.model.model.constant.PropertiesPrefixConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = PropertiesPrefixConstant.CACHE)
public class MyCacheProperties {

    @Schema(description = "需要缓存的数据库名")
    private String databaseName = "be_engine_v1";

}