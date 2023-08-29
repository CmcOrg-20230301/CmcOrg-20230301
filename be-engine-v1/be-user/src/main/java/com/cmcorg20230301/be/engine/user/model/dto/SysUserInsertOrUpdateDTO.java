package com.cmcorg20230301.be.engine.user.model.dto;

import com.cmcorg20230301.be.engine.model.model.annotation.NotBlankPattern;
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
    @NotBlankPattern(regexp = BaseRegexConstant.SIGN_IN_NAME_REGEXP)
    @Schema(description = "登录名")
    private String signInName;

    @Size(max = 200)
    @NotBlankPattern(regexp = BaseRegexConstant.EMAIL)
    @Schema(description = "邮箱")
    private String email;

    @Size(max = 100)
    @NotBlankPattern(regexp = BaseRegexConstant.PHONE)
    @Schema(description = "手机号码")
    private String phone;

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

}
