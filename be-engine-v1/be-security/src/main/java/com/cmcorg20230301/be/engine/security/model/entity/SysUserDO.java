package com.cmcorg20230301.be.engine.security.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_user")
@Data
@Schema(description = "主表：用户")
public class SysUserDO extends BaseEntity {

    @Schema(description = "正常/冻结")
    private Boolean enableFlag;

    @Schema(description = "是否注销，未使用，而是采取直接删除的方式，目的：防止数据量越来越大")
    private Boolean delFlag;

    @Schema(description = "父节点id（顶级则为0）")
    private Long parentId;

    @TableField(exist = false)
    @Schema(description = "子节点")
    private List<SysUserDO> children;

    @Schema(description = "密码，可为空，如果为空，则登录时需要提示【进行忘记密码操作】")
    private String password;

    @Schema(description = "邮箱，可以为空")
    private String email;

    @Schema(description = "登录名，可以为空")
    private String signInName;

    @Schema(description = "手机号，可以为空")
    private String phone;

    @Schema(
        description = "微信 appId，可以为空，wxAppId + wxOpenId 全租户唯一，备注：因为微信对不同的公众号或者小程序，会提供相同的 wxAppId，所以需要加上 wxOpenId，进行唯一性检查")
    private String wxAppId;

    @Schema(description = "微信 openId，可以为空，wxAppId + wxOpenId 全租户唯一")
    private String wxOpenId;

    @Schema(description = "微信 unionId，可以为空，wxUnionId 全租户唯一")
    private String wxUnionId;

}
