package com.cmcorg20230301.be.engine.security.model.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "实体类基类-没有主键 id")
public class BaseEntityNoId extends BaseEntityNoIdSuper {

    // 如果要加 @Version，建议继承本类，再给子类的字段上面加这个注解，因为乐观锁的需求没有那么多
    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "乐观锁")
    private Integer version;

    @Schema(description = "是否启用")
    private Boolean enableFlag;

    // 如果要加 @TableLogic，建议继承本类，再给子类的字段上面加这个注解，因为不是每个业务都需要逻辑删除，不建议使用逻辑删除，而是采用直接删除，删除的数据，可以存到其他地方，不然数据量会越来越大
    @Schema(description = "是否逻辑删除")
    private Boolean delFlag;

    @Schema(description = "备注")
    private String remark;

}
