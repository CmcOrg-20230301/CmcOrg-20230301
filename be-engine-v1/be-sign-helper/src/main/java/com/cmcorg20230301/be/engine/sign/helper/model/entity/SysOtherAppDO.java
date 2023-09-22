package com.cmcorg20230301.be.engine.sign.helper.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntity;
import com.cmcorg20230301.be.engine.sign.helper.model.enums.SysOtherAppEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_other_app")
@Data
@Schema(description = "v20230301：主表：第三方应用相关配置")
public class SysOtherAppDO extends BaseEntity {

    @Schema(description = "第三方应用类型")
    private SysOtherAppEnum type;

    @Schema(description = "第三方应用名，备注：同一租户不能重复，不同租户可以重复")
    private String name;

    @Schema(description = "第三方应用的 appId，备注：同一租户不能重复，不同租户可以重复")
    private String appId;

    @Schema(description = "第三方应用的 secret")
    private String secret;

}
