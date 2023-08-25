package com.cmcorg20230301.engine.be.post.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@TableName(value = "sys_post_ref_user")
@Data
@Schema(description = "关联表：岗位，用户")
public class SysPostRefUserDO {

    @Schema(description = "租户id")
    private Long tenantId;

    @TableId
    @Schema(description = "岗位主键 id")
    private Long postId;

    @Schema(description = "用户主键 id")
    private Long userId;

}
