package com.cmcorg20230301.engine.be.file.base.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cmcorg20230301.engine.be.security.model.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_file_auth")
@Data
@Schema(description = "子表：文件操作权限，主表：文件")
public class SysFileAuth extends BaseEntity {

    @Schema(description = "文件主键 id")
    private Long fileId;

    @Schema(description = "此权限拥有者的 userId")
    private Long userId;

    @Schema(description = "是否可读：0 否 1 是")
    private Integer readFlag;

    @Schema(description = "是否可写：0 否 1 是")
    private Integer writeFlag;

}
