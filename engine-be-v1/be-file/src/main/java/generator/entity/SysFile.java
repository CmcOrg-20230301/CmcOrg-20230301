package generator.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cmcorg20230301.engine.be.security.model.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_file")
@Data
@Schema(description = "主表：文件")
public class SysFile extends BaseEntity {

    @Schema(description = "归属者用户主键 id（拥有全部权限）")
    private Long belongId;

    @Schema(description = "桶名，例如：be-bucket")
    private String bucketName;

    @Schema(description = "文件完整路径（包含文件类型，不包含请求端点），例如：/avatar/uuid.xxx")
    private String uri;

    @Schema(description = "文件原始名（包含文件类型）")
    private String originFileName;

    @Schema(description = "新的文件名（包含文件类型），例如：uuid.xxx")
    private String newFileName;

    @Schema(description = "文件类型（不含点），备注：这个是读取文件流的头部信息获得文件类型")
    private String fileExtName;

    @Schema(description = "额外信息（json格式）")
    private String extraJson;

    @Schema(description = "文件上传类型：101 头像 201 文件系统-文件")
    private Integer uploadType;

    @Schema(description = "存放文件的服务器类型：101 阿里云oss 201 minio")
    private Integer savaType;

    @Schema(description = "上级文件夹的文件主键 id，默认为 0")
    private Long parentId;

    @Schema(description = "类型：1 文件夹 2 文件")
    private Integer type;

    @Schema(description = "展示用的文件名，默认为：原始文件名（包含文件类型）")
    private String showFileName;

}
