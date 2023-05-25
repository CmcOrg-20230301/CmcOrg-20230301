package com.cmcorg20230301.engine.be.socket.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cmcorg20230301.engine.be.security.model.entity.BaseEntity;
import com.cmcorg20230301.engine.be.socket.model.enums.SysSocketEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_socket")
@Data
@Schema(description = "v20230301：主表：socket")
public class SysSocketDO extends BaseEntity {

    @Schema(description = "协议")
    private String scheme;

    @Schema(description = "主机")
    private String host;

    @Schema(description = "端口")
    private Integer port;

    @Schema(description = "socket类型")
    private SysSocketEnum type;

}
