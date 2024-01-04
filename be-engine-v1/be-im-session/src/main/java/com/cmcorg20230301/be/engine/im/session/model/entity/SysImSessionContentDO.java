package com.cmcorg20230301.be.engine.im.session.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cmcorg20230301.be.engine.im.session.model.configuration.ISysImSessionContentType;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_im_session_content")
@Data
@Schema(description = "子表：会话内容，主表：会话")
public class SysImSessionContentDO extends BaseEntity {

    @Schema(description = "会话主键 id")
    private Long sessionId;

    @Schema(description = "会话内容")
    private String content;

    @Schema(description = "是否显示在：用户会话列表中")
    private Boolean showFlag;

    /**
     * {@link ISysImSessionContentType}
     */
    @Schema(description = "内容类型")
    private Integer type;

}