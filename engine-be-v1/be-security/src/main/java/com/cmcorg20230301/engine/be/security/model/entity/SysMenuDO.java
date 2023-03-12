package com.cmcorg20230301.engine.be.security.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_menu")
@Data
@Schema(description = "主表：菜单")
public class SysMenuDO extends BaseEntityTree<SysMenuDO> {

    @Schema(description = "菜单名")
    private String name;

    @Schema(description = "页面的 path，备注：相同父菜单下，子菜单 path不能重复")
    private String path;

    @Schema(description = "路由")
    private String router;

    @Schema(description = "权限，多个可用逗号拼接，例如：menu:insertOrUpdate,menu:page,menu:deleteByIdSet,menu:infoById")
    private String auths;

    @Schema(description = "是否是权限菜单，权限菜单：不显示，只代表菜单权限")
    private Boolean authFlag;

    @Schema(description = "是否显示在 左侧的菜单栏里面，如果为 false，也可以通过 $router.push()访问到")
    private Boolean showFlag;

    @Schema(description = "是否外链，即，打开页面会在一个新的窗口打开")
    private Boolean linkFlag;

    @Schema(description = "重定向，优先级最高")
    private String redirect;

    @Schema(description = "是否是起始页面，备注：只能存在一个 firstFlag === true 的菜单")
    private Boolean firstFlag;

    @Schema(description = "图标")
    private String icon;

}

