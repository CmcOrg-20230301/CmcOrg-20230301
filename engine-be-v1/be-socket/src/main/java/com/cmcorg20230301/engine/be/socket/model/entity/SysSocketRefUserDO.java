package com.cmcorg20230301.engine.be.socket.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cmcorg20230301.engine.be.security.model.entity.BaseEntity;
import com.cmcorg20230301.engine.be.socket.model.enums.SysSocketTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_socket_ref_user")
@Data
@Schema(description = "v20230301：关联表：socket，用户")
public class SysSocketRefUserDO extends BaseEntity {

    @Schema(description = "用户主键 id")
    private Long userId;

    @Schema(description = "socket主键 id")
    private Long socketId;

    @Schema(description = "冗余字段，昵称")
    private String nickname;

    @Schema(description = "冗余字段，协议")
    private String scheme;

    @Schema(description = "冗余字段，主机")
    private String host;

    @Schema(description = "冗余字段，端口")
    private Integer port;

    @Schema(description = "冗余字段，socket类型")
    private SysSocketTypeEnum type;

}
