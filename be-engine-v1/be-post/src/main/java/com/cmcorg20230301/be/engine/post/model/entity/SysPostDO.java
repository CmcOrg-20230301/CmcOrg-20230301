package com.cmcorg20230301.be.engine.post.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityTree;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_post")
@Data
@Schema(description = "主表：岗位")
public class SysPostDO extends BaseEntityTree<SysPostDO> {

    @Schema(description = "岗位名")
    private String name;

}
