package com.cmcorg20230301.be.engine.file.base.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cmcorg20230301.be.engine.file.base.model.enums.SysFileStorageTypeEnum;
import com.cmcorg20230301.be.engine.file.base.model.enums.SysFileTypeEnum;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntity;
import com.cmcorg20230301.be.engine.security.model.enums.SysFileUploadTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_file")
@Data
@Schema(description = "主表：文件")
public class SysFileDO extends BaseEntity {

    @Schema(description = "归属者用户主键 id（拥有全部权限）")
    private Long belongId;

    @Schema(description = "桶名，例如：be-bucket")
    private String bucketName;

    @Schema(description = "文件完整路径（包含文件类型，不包含请求端点），例如：avatar/uuid.xxx")
    private String uri;

    @Schema(description = "文件原始名（包含文件类型）")
    private String originFileName;

    @Schema(description = "新的文件名（包含文件类型），例如：uuid.xxx")
    private String newFileName;

    @Schema(description = "文件类型（不含点），备注：这个是读取文件流的头部信息获得文件类型")
    private String fileExtName;

    @Schema(description = "额外信息（json格式）")
    private String extraJson;

    @Schema(description = "文件上传类型")
    private SysFileUploadTypeEnum uploadType;

    @Schema(description = "存放文件的服务器类型")
    private SysFileStorageTypeEnum storageType;

    @Schema(description = "上级文件夹的文件主键 id，默认为 0")
    private Long parentId;

    @Schema(description = "类型")
    private SysFileTypeEnum type;

    @Schema(description = "展示用的文件名，默认为：原始文件名（包含文件类型）")
    private String showFileName;

    @Schema(description = "引用的文件主键 id，没有则为 -1，如果有值，则文件地址从引用的文件里面获取，但是权限等信息，从本条数据获取")
    private Long refFileId;

    @Schema(description = "是否公开访问")
    private Boolean publicFlag;

}
