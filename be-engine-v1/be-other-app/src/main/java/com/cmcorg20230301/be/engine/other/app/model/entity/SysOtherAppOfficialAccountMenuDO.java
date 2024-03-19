package com.cmcorg20230301.be.engine.other.app.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cmcorg20230301.be.engine.other.app.model.enums.SysOtherAppOfficialAccountMenuButtonTypeEnum;
import com.cmcorg20230301.be.engine.other.app.model.interfaces.ISysOtherAppOfficialAccountMenuType;
import com.cmcorg20230301.be.engine.security.model.entity.BaseEntityTree;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_other_app_official_account_menu")
@Data
@Schema(description = "子表：公众号的菜单等相关配置，主表：第三方应用相关配置（sys_other_app）")
public class SysOtherAppOfficialAccountMenuDO extends BaseEntityTree<SysOtherAppOfficialAccountMenuDO> {

    @Schema(description = "所属的，第三方应用相关配置主键 id")
    private Long otherAppId;

    @Schema(description = "菜单名，备注：一级菜单最多4个汉字，二级菜单最多8个汉字，多出来的部分将会以 ... 代替")
    private String name;

    /**
     * {@link ISysOtherAppOfficialAccountMenuType}
     */
    @Schema(description = "类型")
    private Integer type;

    @Schema(description = "按钮类型")
    private SysOtherAppOfficialAccountMenuButtonTypeEnum buttonType;

    @Schema(description = "如果是按钮，则表示按钮的 key，如果是链接，则表示是 url，如果是小程序，则表示 appid")
    private String value;

    @Schema(description = "小程序的页面路径")
    private String pagepath;

    @Schema(description = "回复的内容，备注：一般是点击按钮之后，回复的内容")
    private String replyContent;

}
