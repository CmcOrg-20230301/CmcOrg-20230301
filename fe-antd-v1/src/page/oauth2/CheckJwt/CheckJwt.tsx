import {useEffect} from "react";
import {getAppNav} from "@/MyApp";
import PathConstant from "@/model/constant/PathConstant";
import LocalStorageKey from "@/model/constant/LocalStorageKey";
import SessionStorageKey from "@/model/constant/SessionStorageKey";
import {GetTenantIdFromStorage} from "@/util/CommonUtil";

function GoBlank() {
    getAppNav()(PathConstant.BLANK_PATH, {state: {showText: '跳转失败：参数不存在'}})
}

export interface ICheckJwt {

    hasJwtUrl: string // 有 jwt时跳转的地址

    noJwtUrl: string // 没有 jwt时跳转的地址

    mainUri: string, // 主页地址，例如：/admin

    mainRedirectUri: string // 主页跳转地址，例如：/admin/sys/dict，主要用在：BlankLayout里

    tenantId: string // 租户主键 id，用于：判断 jwt存在时，存储的 tenantId是否和 本次的 tenantId一致，如果不一致

}

// 检查：是否有 jwt，如果有，则直接跳转到，有 jwt时指定的页面，如果没有，跳转到，没有 jwt时的页面
export default function () {

    useEffect(() => {

        let search = window.location.search;

        if (!search) {

            GoBlank();
            return

        }

        search = search.split("?")[1]

        let form: ICheckJwt = {} as ICheckJwt

        search.split("&").forEach(item => {

            let splitArr = item.split("=");

            form[splitArr[0]] = splitArr[1]

        })

        let jwt = localStorage.getItem(LocalStorageKey.JWT);

        const jwtExpireTime = localStorage.getItem(LocalStorageKey.JWT_EXPIRE_TIME);

        if (jwtExpireTime) {

            const time = new Date(Number(jwtExpireTime)).getTime();

            const currentTime = new Date().getTime();

            if (currentTime >= time) { // 如果：jwt过期了

                jwt = null

            }

        } else {

            jwt = null

        }

        if (jwt) {

            if (form.tenantId) {

                const storageTenantId = GetTenantIdFromStorage();

                if (storageTenantId !== form.tenantId) {

                    jwt = null

                }

            }

        }

        let hasJwtUrl = form.hasJwtUrl;

        if (hasJwtUrl) {

            hasJwtUrl = decodeURIComponent(hasJwtUrl)

        }

        let noJwtUrl = form.noJwtUrl;

        if (noJwtUrl) {

            noJwtUrl = decodeURIComponent(noJwtUrl)

            localStorage.setItem(LocalStorageKey.NO_JWT_URI, noJwtUrl)

        }

        if (jwt) {

            if (hasJwtUrl?.startsWith('http')) {

                window.location.href = hasJwtUrl!

            } else {

                localStorage.setItem(LocalStorageKey.MAIN_URI, form.mainUri!)
                localStorage.setItem(LocalStorageKey.MAIN_REDIRECT_URI, form.mainRedirectUri!)

                if (hasJwtUrl) {
                    sessionStorage.setItem(SessionStorageKey.OAUTH2_REDIRECT_URI, hasJwtUrl)
                }

                getAppNav()(form.mainUri)

            }

        } else {

            if (noJwtUrl?.startsWith('http')) {

                window.location.href = noJwtUrl!

            } else {

                getAppNav()(noJwtUrl)

            }

        }


    }, [])

    return <div>...</div>;

}
