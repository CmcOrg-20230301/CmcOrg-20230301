package com.cmcorg20230301.be.engine.user.model.dto;

import com.cmcorg20230301.be.engine.model.model.annotation.NotCheckBlankPattern;
import com.cmcorg20230301.be.engine.model.model.constant.BaseRegexConstant;
import com.cmcorg20230301.be.engine.model.model.dto.BaseTenantInsertOrUpdateDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysUserInsertOrUpdateDTO extends BaseTenantInsertOrUpdateDTO {

    @Size(max = 20)
    @NotCheckBlankPattern(regexp = BaseRegexConstant.SIGN_IN_NAME_REGEXP)
    @Schema(description = "登录名")
    private String signInName;

    @Size(max = 200)
    @NotCheckBlankPattern(regexp = BaseRegexConstant.EMAIL)
    @Schema(description = "邮箱")
    private String email;

    @Size(max = 100)
    @NotCheckBlankPattern(regexp = BaseRegexConstant.PHONE)
    @Schema(description = "手机号码")
    private String phone;

    @Schema(description = "微信 appId")
    private String wxAppId;

    @Schema(description = "微信 openId")
    private String wxOpenId;

    @Schema(description = "前端加密之后的密码")
    private String password;

    @Schema(description = "前端加密之后的原始密码")
    private String originPassword;

    @Pattern(regexp = BaseRegexConstant.NICK_NAME_REGEXP)
    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "个人简介")
    private String bio;

    @Schema(description = "正常/冻结")
    private Boolean enableFlag;

    @Schema(description = "角色 idSet")
    private Set<Long> roleIdSet;

    @Schema(description = "部门 idSet")
    private Set<Long> deptIdSet;

    @Schema(description = "岗位 idSet")
    private Set<Long> postIdSet;

    @Schema(description = "租户 idSet")
    private Set<Long> tenantIdSet;

    @Schema(description = "是否允许登录：后台管理系统")
    private Boolean manageSignInFlag;

    @Schema(description = "企业微信-微信客服：当会话状态为：0 未处理时，是否自动交给智能助手接待，默认：true")
    private Boolean sysWxWorkKfAutoAssistantFlag;

}
