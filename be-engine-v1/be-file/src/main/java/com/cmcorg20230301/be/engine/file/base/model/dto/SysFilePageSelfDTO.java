package com.cmcorg20230301.be.engine.file.base.model.dto;

import com.cmcorg20230301.be.engine.security.model.dto.MyTenantPageDTO;
import com.cmcorg20230301.be.engine.security.model.enums.SysUserTenantEnum;
import com.cmcorg20230301.be.engine.security.model.interfaces.ISysFileStorageType;
import com.cmcorg20230301.be.engine.security.model.interfaces.ISysFileUploadType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysFilePageSelfDTO extends MyTenantPageDTO {

    @Schema(description = "文件原始名（包含文件类型）")
    private String originFileName;

    /**
     * {@link ISysFileUploadType}
     */
    @Schema(description = "文件上传类型")
    private Integer uploadType;

    /**
     * {@link ISysFileStorageType}
     */
    @Schema(description = "存放文件的服务器类型")
    private Integer storageType;

    @Schema(description = "是否公开访问")
    private Boolean publicFlag;

    @Schema(description = "是否启用")
    private Boolean enableFlag;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "用户/租户")
    private SysUserTenantEnum sysUserTenantEnum;

}
