package com.cmcorg20230301.be.engine.socket.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntity;
import com.cmcorg20230301.be.engine.socket.model.enums.SysSocketTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_socket")
@Data
@Schema(description = "v20230301：主表：socket")
public class SysSocketDO extends BaseEntity {

    @Schema(description = "协议：例如：ws://，wss://，http://，https://，等")
    private String scheme;

    @Schema(description = "主机")
    private String host;

    @Schema(description = "端口")
    private Integer port;

    @Schema(description = "路径，备注：以 / 开头")
    private String path;

    @Schema(description = "socket类型")
    private SysSocketTypeEnum type;

}
