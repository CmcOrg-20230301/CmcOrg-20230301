package com.cmcorg20230301.be.engine.im.session.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityNoIdSuper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_im_session_ref_user")
@Data
@Schema(description = "关联表：会话，用户")
public class SysImSessionRefUserDO extends BaseEntityNoIdSuper {

    @TableId(type = IdType.INPUT)
    @Schema(description = "主键 id")
    private Long id;

    @Schema(description = "用户主键 id")
    private Long userId;

    @Schema(description = "会话主键 id")
    private Long sessionId;

    @Schema(description = "是否逻辑删除，暂时未使用")
    private Boolean delFlag;

    @Schema(description = "我对会话的备注")
    private String remark;

    @Schema(description = "我在会话的昵称，备注：为空则表示，需要获取用户的实时昵称")
    private String sessionNickname;

    @Schema(description = "我最后一次打开该会话的时间戳")
    private Long lastOpenTs;

    @Schema(description = "是否显示在会话列表")
    private Boolean showFlag;

    @Schema(description = "是私聊时，关联的另外一个用户的主键 id，其他类型时，该值为：-1")
    private Long privateChatRefUserId;

    @Schema(description = "群聊时是否没有被禁言，或者私聊时是否没有被删除")
    private Boolean enableFlag;

    @Schema(description = "是否被拉黑")
    private Boolean blockFlag;

}