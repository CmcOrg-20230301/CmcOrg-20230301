package com.cmcorg20230301.be.engine.security.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_user")
@Data
@Schema(description = "主表：用户")
public class SysUserDO extends BaseEntity {

    @Schema(description = "正常/冻结")
    private Boolean enableFlag;

    @Schema(description = "是否注销，未使用，而是采取直接删除的方式，目的：防止数据量越来越大")
    private Boolean delFlag;

    @Schema(description = "密码，可为空，如果为空，则登录时需要提示【进行忘记密码操作】")
    private String password;

    @Schema(description = "邮箱，可以为空")
    private String email;

    @Schema(description = "登录名，可以为空")
    private String signInName;

    @Schema(description = "手机号，可以为空")
    private String phone;

    @Schema(description = "微信 openId，可以为空")
    private String wxOpenId;

}
