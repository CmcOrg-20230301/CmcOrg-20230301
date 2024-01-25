import {Outlet} from "react-router-dom";
import {UseEffectLoadSysMenuUserSelfMenuList, UseEffectLoadUserSelfInfo} from "@/util/UseEffectUtil";
import React from "react";
import PathConstant from "@/model/constant/PathConstant.ts";
import SessionStorageKey from "@/model/constant/SessionStorageKey.ts";
import LocalStorageKey from "@/model/constant/LocalStorageKey.ts";
import {GetAppNav} from "@/MyApp.tsx";

// 空白布局
export default function () {

    // 加载菜单
    UseEffectLoadSysMenuUserSelfMenuList((menuList) => {

        const pathname = window.location.pathname;

        let notFoundRedirectPath = sessionStorage.getItem(SessionStorageKey.NOT_FOUND_REDIRECT_PATH);

        if (!notFoundRedirectPath) {

            if (pathname !== PathConstant.BLANK_LAYOUT_PATH) {

                notFoundRedirectPath = pathname

            }

        }

        if (notFoundRedirectPath) {

            sessionStorage.removeItem(SessionStorageKey.NOT_FOUND_REDIRECT_PATH)

            console.log('blank-notFoundRedirectPath', notFoundRedirectPath)

            if (menuList.some(item => item.path === notFoundRedirectPath)) { // 防止：死循环加载

                console.log('blank-notFoundRedirectPath-goPage', notFoundRedirectPath)

                GetAppNav()(notFoundRedirectPath)
                return

            }

        }

        const oauth2WxRedirectUri = sessionStorage.getItem(SessionStorageKey.OAUTH2_REDIRECT_URI)

        if (oauth2WxRedirectUri) {

            sessionStorage.removeItem(SessionStorageKey.OAUTH2_REDIRECT_URI)

            console.log('blank-oauth2WxRedirectUri', oauth2WxRedirectUri)

            if (menuList.some(item => item.path === oauth2WxRedirectUri)) { // 防止：死循环加载

                console.log('blank-oauth2WxRedirectUri-goPage', oauth2WxRedirectUri)

                GetAppNav()(oauth2WxRedirectUri)
                return

            }

        }

        const mainRedirectUri = localStorage.getItem(LocalStorageKey.MAIN_REDIRECT_URI);

        if (mainRedirectUri) {

            console.log('blank-mainRedirectUri', mainRedirectUri)

            if (menuList.some(item => item.path === mainRedirectUri)) { // 防止：死循环加载

                console.log('blank-mainRedirectUri-goPage', mainRedirectUri)

                GetAppNav()(mainRedirectUri)
                return

            }

        }

    });

    // 加载：用户数据
    UseEffectLoadUserSelfInfo()

    return <Outlet/>

}