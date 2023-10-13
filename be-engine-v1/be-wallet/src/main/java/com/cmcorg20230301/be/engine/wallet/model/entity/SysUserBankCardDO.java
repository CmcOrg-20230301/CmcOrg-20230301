package com.cmcorg20230301.be.engine.wallet.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityTree;
import com.cmcorg20230301.be.engine.wallet.model.enums.SysOpenBankNameEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_user_bank_card")
@Data
@Schema(description = "子表：用户银行卡，主表：用户")
public class SysUserBankCardDO extends BaseEntityTree<SysUserBankCardDO> {

    @TableId(type = IdType.INPUT)
    @Schema(description = "用户主键 id")
    private Long id;

    @Schema(description = "卡号")
    private String bankCardNo;

    @Schema(description = "开户行")
    private SysOpenBankNameEnum openBankName;

    @Schema(description = "支行")
    private String branchBankName;

    @Schema(description = "收款人姓名")
    private String payeeName;

    @TableField(exist = false)
    @Schema(description = "上级 id，用于：租户银行卡列表的树形结构展示，没有其他用途")
    private Long parentId;

    @TableField(exist = false)
    @Schema(description = "为了：组装树结构，没有其他用途")
    private Integer orderNo;

}
