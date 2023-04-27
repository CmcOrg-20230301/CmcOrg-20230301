package generator.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cmcorg20230301.engine.be.security.model.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_file_uri")
@Data
@Schema(description = "子表：文件访问uri，主表：文件")
public class SysFileUri extends BaseEntity {

    @Schema(description = "文件访问 uri（包含文件类型），不可重复，例如：/uuid.xxx")
    private String uri;

    @Schema(description = "文件主键 id")
    private Long fileId;

    @Schema(description = "是否公开访问：0 否 1 是")
    private Integer publicFlag;

    @Schema(description = "过期时间（年月日时分秒），备注：会定期清理，或者获取时发现过期了，则也会清理，-1表示永久")
    private LocalDateTime expireTime;

    @Schema(description = "冗余字段：文件归属者用户主键 id（拥有全部权限）")
    private Long fileBelongId;

}
