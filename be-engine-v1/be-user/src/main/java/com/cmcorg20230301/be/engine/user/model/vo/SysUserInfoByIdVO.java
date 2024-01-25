package com.cmcorg20230301.be.engine.user.model.vo;

import com.cmcorg20230301.be.engine.security.model.entity.SysUserDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysUserInfoByIdVO extends SysUserDO {

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "个人简介")
    private String bio;

    @Schema(description = "头像 fileId（文件主键 id）")
    private Long avatarFileId;

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

}
