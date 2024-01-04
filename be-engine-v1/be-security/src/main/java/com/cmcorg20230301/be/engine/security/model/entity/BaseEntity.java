package com.cmcorg20230301.be.engine.security.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "实体类基类")
public class BaseEntity extends BaseEntityNoId {

    /**
     * 这里是自定义的主键 id
     */
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键 id")
    private Long id;

}
