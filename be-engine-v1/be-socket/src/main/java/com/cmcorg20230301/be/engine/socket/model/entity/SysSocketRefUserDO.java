package com.cmcorg20230301.be.engine.socket.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntity;
import com.cmcorg20230301.be.engine.security.model.enums.SysRequestCategoryEnum;
import com.cmcorg20230301.be.engine.socket.model.enums.SysSocketOnlineTypeEnum;
import com.cmcorg20230301.be.engine.socket.model.enums.SysSocketTypeEnum;
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

    @Schema(description = "冗余字段，用户昵称")
    private String nickname;

    @Schema(description = "冗余字段，协议")
    private String scheme;

    @Schema(description = "冗余字段，主机")
    private String host;

    @Schema(description = "冗余字段，端口")
    private Integer port;

    @Schema(description = "路径")
    private String path;

    @Schema(description = "冗余字段，socket类型")
    private SysSocketTypeEnum type;

    @Schema(description = "socket 在线状态")
    private SysSocketOnlineTypeEnum onlineType;

    @Schema(description = "ip")
    private String ip;

    @Schema(description = "Ip2RegionUtil.getRegion() 获取到的 ip所处区域")
    private String region;

    @Schema(description = "请求类别")
    private SysRequestCategoryEnum category;

    @Schema(description = "jwtHash")
    private String jwtHash;

    @Schema(description = "jwtHash未来过期的时间戳")
    private Long jwtHashExpireTs;

    @Schema(description = "User-Agent信息对象，json字符串")
    private String userAgentJsonStr;

}
