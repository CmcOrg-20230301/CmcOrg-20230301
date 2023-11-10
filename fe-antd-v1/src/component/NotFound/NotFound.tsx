import {Navigate} from 'react-router-dom';
import PathConstant from "@/model/constant/PathConstant";
import SessionStorageKey from "@/model/constant/SessionStorageKey";
import LocalStorageKey from "@/model/constant/LocalStorageKey";
import {GetTenantIdFromStorage} from "@/util/CommonUtil";

// 404页面
export default function () {

    console.log("NotFound")

    if (!sessionStorage.getItem(SessionStorageKey.ADMIN_REDIRECT_PATH)) {

        sessionStorage.setItem(SessionStorageKey.ADMIN_REDIRECT_PATH, window.location.pathname)

    } else {

        sessionStorage.removeItem(SessionStorageKey.ADMIN_REDIRECT_PATH)

    }

    const jwt = localStorage.getItem(LocalStorageKey.JWT);

    const tenantId = GetTenantIdFromStorage();

    return <Navigate
        to={jwt ? PathConstant.ADMIN_PATH : `${PathConstant.SIGN_IN_PATH}?tenantId=${tenantId}`}/>

}
