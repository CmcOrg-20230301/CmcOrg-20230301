package com.cmcorg20230301.be.engine.other.app.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cmcorg20230301.be.engine.other.app.model.enums.SysOtherAppTypeEnum;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_other_app")
@Data
@Schema(description = "主表：第三方应用相关配置")
public class SysOtherAppDO extends BaseEntity {

    @Schema(description = "第三方应用类型")
    private SysOtherAppTypeEnum type;

    @Schema(description = "第三方应用名")
    private String name;

    @Schema(description = "第三方应用的 appId，备注：同一租户不能重复，不同租户可以重复")
    private String appId;

    @Schema(description = "第三方应用的 secret")
    private String secret;

    @Schema(description = "用户点击关注之后，回复的内容，备注：如果取关然后再关注，也会回复该内容")
    private String subscribeReplyContent;

    @Schema(description = "二维码，备注：不是二维码图片的地址，而是二维码解码之后的值")
    private String qrCode;

}
