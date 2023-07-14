package com.cmcorg20230301.engine.be.file.base.model.dto;

import com.cmcorg20230301.engine.be.file.base.model.enums.SysFileUploadTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class SysFileUploadDTO {

    @Schema(description = "文件")
    private MultipartFile file;

    @Schema(description = "文件上传的类型")
    private SysFileUploadTypeEnum uploadType;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "额外信息（json格式）")
    private String extraJson;

}
