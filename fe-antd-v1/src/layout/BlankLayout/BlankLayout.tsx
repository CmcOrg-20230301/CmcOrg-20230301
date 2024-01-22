import {Outlet} from "react-router-dom";
import {UseEffectLoadSysMenuUserSelfMenuList, UseEffectLoadUserSelfInfo} from "@/util/UseEffectUtil";
import {GetAppNav} from "@/MyApp";
import LocalStorageKey from "@/model/constant/LocalStorageKey";

// 空白布局
export default function () {

    // 加载菜单
    UseEffectLoadSysMenuUserSelfMenuList((data, firstFlag) => {

        if (firstFlag) {

            const mainRedirectUri = localStorage.getItem(LocalStorageKey.MAIN_REDIRECT_URI);

            if (mainRedirectUri) {

                GetAppNav()(mainRedirectUri)

            }

        }

    });

    // 加载：用户数据
    UseEffectLoadUserSelfInfo()

    return <Outlet/>

}
