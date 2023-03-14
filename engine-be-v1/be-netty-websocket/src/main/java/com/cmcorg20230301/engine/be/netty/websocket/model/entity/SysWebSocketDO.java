package com.cmcorg20230301.engine.be.netty.websocket.model.entity;

import com.admin.common.model.entity.BaseEntityThree;
import com.admin.common.model.enums.SysRequestCategoryEnum;
import com.admin.common.util.IpUtil;
import com.admin.websocket.model.enums.SysWebSocketTypeEnum;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_web_socket")
@Data
@ApiModel(description = "WebSocket 连接记录主表")
public class SysWebSocketDO extends BaseEntityThree {

    /**
     * {@link IpUtil#getRegion()}
     */
    @ApiModelProperty(value = "IpUtil.getRegion() 获取到的 ip所处区域")
    private String region;

    @ApiModelProperty(value = "ip")
    private String ip;

    @ApiModelProperty(value = "浏览器和浏览器版本，用 / 分隔表示")
    private String browser;

    @ApiModelProperty(value = "操作系统")
    private String os;

    @ApiModelProperty(value = "是否是移动端网页，true 是 false 否")
    private Boolean mobileFlag;

    @ApiModelProperty(value = "状态：1 在线 2 隐身")
    private SysWebSocketTypeEnum type;

    @ApiModelProperty(value = "本次 WebSocket 连接的服务器的 ip:port")
    private String server;

    @ApiModelProperty(value = "请求类别：1 H5（网页端） 2 APP（移动端） 3 PC（桌面程序） 4 微信小程序")
    private SysRequestCategoryEnum category;

    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "连接中/断开连接")
    private Boolean enableFlag;

    @ApiModelProperty(value = "jwtHash，用于匹配 redis中存储的 jwtHash")
    private String jwtHash;

}
