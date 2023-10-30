package com.cmcorg20230301.be.engine.wx.model.bo;

import cn.hutool.core.annotation.Alias;
import lombok.Data;

import java.util.List;

/**
 * 微信公众号，菜单
 */
@Data
public class SysOtherAppOfficialAccountMenuWxBO {

    public enum SysOtherAppOfficialAccountMenuWxType {

        click, // 用户点击click类型按钮后，微信服务器会通过消息接口推送消息类型为event的结构给开发者（参考消息接口指南），并且带上按钮中开发者填写的key值，开发者可以通过自定义的key值与用户进行交互
        view, // 跳转URL用户点击view类型按钮后，微信客户端将会打开开发者在按钮中填写的网页URL，可与网页授权获取用户基本信息接口结合，获得用户基本信息

        ;

    }

    private SysOtherAppOfficialAccountMenuWxType type; // 类型

    private String name; // 名称，备注：一级菜单最多4个汉字，二级菜单最多8个汉字，多出来的部分将会以 ... 代替

    private String key; // click

    private String url; // view

    @Alias(value = "sub_button")
    private List<SysOtherAppOfficialAccountMenuWxBO> subButton; // 子菜单，自定义菜单最多包括3个一级菜单，每个一级菜单最多包含5个二级菜单

}
