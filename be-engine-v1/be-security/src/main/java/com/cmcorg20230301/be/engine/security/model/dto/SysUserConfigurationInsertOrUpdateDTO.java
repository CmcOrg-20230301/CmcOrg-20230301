package com.cmcorg20230301.be.engine.security.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SysUserConfigurationInsertOrUpdateDTO {

    @Schema(description = "是否启用：用户名注册功能")
    private Boolean signInNameSignUpEnable;

    @Schema(description = "是否启用：邮箱注册功能")
    private Boolean emailSignUpEnable;

    @Schema(description = "是否启用：手机号码注册功能")
    private Boolean phoneSignUpEnable;

}
