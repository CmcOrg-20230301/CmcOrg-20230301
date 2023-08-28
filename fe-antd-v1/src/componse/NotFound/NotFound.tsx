import {Navigate} from 'react-router-dom';
import PathConstant from "@/model/constant/PathConstant";
import SessionStorageKey from "@/model/constant/SessionStorageKey";
import LocalStorageKey from "@/model/constant/LocalStorageKey";
import {GetTenantId} from "@/util/CommonUtil";

// 404页面
export default function () {

    const tenantId = GetTenantId();

    console.log("NotFound")

    if (!sessionStorage.getItem(SessionStorageKey.ADMIN_REDIRECT_PATH)) {
        sessionStorage.setItem(SessionStorageKey.ADMIN_REDIRECT_PATH, window.location.pathname)
    }

    const jwt = localStorage.getItem(LocalStorageKey.JWT);

    return <Navigate
        to={jwt ? PathConstant.ADMIN_PATH : `${PathConstant.SIGN_IN_PATH}?tenantId=${tenantId}`}/>

}
