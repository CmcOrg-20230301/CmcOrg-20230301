package com.cmcorg20230301.be.engine.security.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "树形结构实体类基类")
public class BaseEntityTree<T> extends BaseEntity {

    @Schema(description = "排序号（值越大越前面，默认为 0）")
    private Integer orderNo;

    @Schema(description = "父节点id（顶级则为0）")
    private Long parentId;

    @TableField(exist = false)
    @Schema(description = "子节点")
    private List<T> children;

}
