package com.cmcorg20230301.be.engine.im.session.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cmcorg20230301.be.engine.im.session.model.configuration.ISysImSessionType;
import com.cmcorg20230301.be.engine.im.session.model.enums.SysImSessionApplyStatusEnum;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoIdSuper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_im_session_apply")
@Data
@Schema(description = "主表：会话申请")
public class SysImSessionApplyDO extends BaseEntityNoIdSuper {

    @TableId(type = IdType.INPUT)
    @Schema(description = "主键 id")
    private Long id;

    @Schema(description = "用户主键 id")
    private Long userId;

    @Schema(description = "会话主键 id")
    private Long sessionId;

    @Schema(description = "会话是私聊时，申请目标用户的主键 id，其他类型时，该值为：-1")
    private Long privateChatApplyTargetUserId;

    @Schema(description = "是否启用")
    private Boolean enableFlag;

    @Schema(description = "是否逻辑删除")
    private Boolean delFlag;

    @Schema(description = "申请加入会话的备注")
    private String remark;

    /**
     * {@link ISysImSessionType}
     */
    @Schema(description = "冗余字段，会话类型：101 私聊 201 群聊 301 客服")
    private Integer sessionType;

    @Schema(description = "状态：101 申请中 201 已通过 301 已拒绝")
    private SysImSessionApplyStatusEnum status;

    @Schema(description = "是否显示在申请列表")
    private Boolean showFlag;

    @Schema(description = "拒绝理由")
    private String rejectReason;

}