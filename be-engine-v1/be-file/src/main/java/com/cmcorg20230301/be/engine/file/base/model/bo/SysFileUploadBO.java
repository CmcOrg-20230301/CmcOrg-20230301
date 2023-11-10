package com.cmcorg20230301.be.engine.file.base.model.bo;

import com.cmcorg20230301.be.engine.security.model.interfaces.ISysFileUploadType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class SysFileUploadBO {

    @Schema(description = "文件")
    private MultipartFile file;

    @Schema(description = "文件上传的类型")
    private ISysFileUploadType uploadType;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "额外信息（json格式）")
    private String extraJson;

}
