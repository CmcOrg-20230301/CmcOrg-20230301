import {Navigate} from 'react-router-dom';
import PathConstant from "@/model/constant/PathConstant";
import SessionStorageKey from "@/model/constant/SessionStorageKey";
import LocalStorageKey from "@/model/constant/LocalStorageKey";
import {GetTenantIdFromStorage} from "@/util/CommonUtil";

// 404页面
export default function () {

    console.log("NotFound", window.location.pathname)

    if (!sessionStorage.getItem(SessionStorageKey.ADMIN_REDIRECT_PATH)) {

        sessionStorage.setItem(SessionStorageKey.ADMIN_REDIRECT_PATH, window.location.pathname)

    } else {

        sessionStorage.removeItem(SessionStorageKey.ADMIN_REDIRECT_PATH)

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
