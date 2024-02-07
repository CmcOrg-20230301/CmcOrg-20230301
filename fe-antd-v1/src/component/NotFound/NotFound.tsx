import PathConstant from "@/model/constant/PathConstant";
import SessionStorageKey from "@/model/constant/SessionStorageKey";
import LocalStorageKey from "@/model/constant/LocalStorageKey";
import {GetTenantIdFromStorage} from "@/util/CommonUtil";
import {Navigate} from "react-router-dom";

// 404页面
export default function () {

    const pathname = window.location.pathname;

    console.log("NotFound", pathname)

    if (!sessionStorage.getItem(SessionStorageKey.NOT_FOUND_REDIRECT_PATH)) {

        if (pathname !== PathConstant.TOP_PATH) { // 目的：防止无限循环

            console.log('NotFound-setItem：', pathname)

            sessionStorage.setItem(SessionStorageKey.NOT_FOUND_REDIRECT_PATH, pathname)

        }

    } else {

        console.log('NotFound-removeItem：', sessionStorage.getItem(SessionStorageKey.NOT_FOUND_REDIRECT_PATH))

        sessionStorage.removeItem(SessionStorageKey.NOT_FOUND_REDIRECT_PATH)

    }

    const jwt = localStorage.getItem(LocalStorageKey.JWT);

    const mainUri = localStorage.getItem(LocalStorageKey.MAIN_URI) || PathConstant.ADMIN_PATH;

    const tenantId = GetTenantIdFromStorage();

    const noJwtUri = localStorage.getItem(LocalStorageKey.NO_JWT_URI) || `${PathConstant.SIGN_IN_PATH}?tenantId=${tenantId}`;

    if (jwt) {

        if (mainUri?.startsWith('http')) {

            window.location.href = mainUri

            return null;

        } else {

            return <Navigate to={mainUri}/>

        }

    } else {

        if (noJwtUri?.startsWith('http')) {

            window.location.href = noJwtUri

            return null;

        } else {

            return <Navigate to={noJwtUri}/>

        }

    }

}
