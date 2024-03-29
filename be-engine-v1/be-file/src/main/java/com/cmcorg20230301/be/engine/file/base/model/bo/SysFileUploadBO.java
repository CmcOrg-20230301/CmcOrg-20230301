package com.cmcorg20230301.be.engine.file.base.model.bo;

import com.cmcorg20230301.be.engine.security.model.interfaces.ISysFileUploadType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

@Data
public class SysFileUploadBO {

    @NotNull
    @Schema(description = "文件")
    private MultipartFile file;

    @NotNull
    @Schema(description = "文件上传的类型")
    private ISysFileUploadType uploadType;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "额外信息（json格式）")
    private String extraJson;

    @NotNull
    @Schema(description = "用户主键 id")
    private Long userId;

    @NotNull
    @Schema(description = "租户主键 id")
    private Long tenantId;

    @Schema(description = "关联的 id")
    private Long refId;

}
