import React from "react";
import {Outlet} from "react-router-dom";
import {UseEffectLoadSysMenuUserSelfMenuList} from "@/util/UseEffectUtil";
import {getAppNav} from "@/MyApp";
import LocalStorageKey from "@/model/constant/LocalStorageKey";

// 空白布局
export default function () {

    // 加载菜单
    UseEffectLoadSysMenuUserSelfMenuList(() => {

        const mainRedirectUri = localStorage.getItem(LocalStorageKey.MAIN_REDIRECT_URI);

        if (mainRedirectUri) {

            getAppNav()(mainRedirectUri)

        }

    });

    return <Outlet/>

}
