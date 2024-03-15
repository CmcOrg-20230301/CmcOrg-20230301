package com.cmcorg20230301.be.engine.im.session.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cmcorg20230301.be.engine.im.session.model.configuration.ISysImSessionContentType;
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

    @Schema(description = "头像 fileId（文件主键 id）")
    private Long avatarFileId;

    @Schema(description = "最后一次接受到消息时的时间戳，默认为：-1，备注：该字段用于：排序")
    private Long lastReceiveContentTs;

    @TableField(exist = false)
    @Schema(description = "未读消息的总数量")
    private Integer unreadContentTotal;

    @TableField(exist = false)
    @Schema(description = "最后一条消息")
    private String lastContent;

    /**
     * {@link ISysImSessionContentType}
     */
    @TableField(exist = false)
    @Schema(description = "最后一条消息的内容类型")
    private Integer lastContentType;

    @TableField(exist = false)
    @Schema(description = "最后一条消息的创建时间戳")
    private Long lastContentCreateTs;

    @TableField(exist = false)
    @Schema(description = "私聊关联的另外一个用户主键 id")
    private Long privateChatRefUserId;

    @TableField(exist = false)
    @Schema(description = "我最后一次打开该会话的时间戳")
    private Long lastOpenTs;

    @TableField(exist = false)
    @Schema(description = "该会话的展示头像地址")
    private String showAvatarFileId;

    @TableField(exist = false)
    @Schema(description = "该会话的展示名称")
    private String showName;

}