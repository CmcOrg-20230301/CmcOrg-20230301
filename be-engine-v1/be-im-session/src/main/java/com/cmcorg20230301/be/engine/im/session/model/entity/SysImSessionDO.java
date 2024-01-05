package com.cmcorg20230301.be.engine.im.session.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cmcorg20230301.be.engine.im.session.model.configuration.ISysImSessionType;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_im_session")
@Data
@Schema(description = "主表：会话")
public class SysImSessionDO extends BaseEntity {

    @Schema(description = "会话名")
    private String name;

    /**
     * {@link ISysImSessionType}
     */
    @Schema(description = "会话类型：101 私聊 201 群聊 301 客服")
    private Integer type;

    @Schema(description = "归属者主键 id（群主），备注：如果为客服类型时，群主必须是用户")
    private Long belongId;

}