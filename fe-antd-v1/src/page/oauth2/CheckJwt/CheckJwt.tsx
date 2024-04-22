import {useEffect} from "react";
import {GetAppNav} from "@/MyApp";
import PathConstant from "@/model/constant/PathConstant";
import LocalStorageKey from "@/model/constant/LocalStorageKey";
import SessionStorageKey from "@/model/constant/SessionStorageKey";
import {GetTenantIdFromStorage} from "@/util/CommonUtil";
import {GetServerTimestamp} from "@/util/DateUtil.ts";
import {MyLocalStorage, MySessionStorage} from "@/util/StorageUtil.ts";

function GoBlank() {
    GetAppNav()(PathConstant.BLANK_PATH, {state: {showText: '跳转失败：参数不存在'}})
}

export interface ICheckJwt {

    hasJwtUrl?: string // 有 jwt时跳转的地址

    noJwtUrl?: string // 没有 jwt时跳转的地址

    mainUri?: string, // 主页地址，例如：/admin

    mainRedirectUri?: string // 主页跳转地址，例如：/admin/sys/dict，主要用在：BlankLayout里

    tenantId?: string // 租户主键 id，用于：判断 jwt存在时，存储的 tenantId是否和 本次的 tenantId一致，如果不一致，则需要重新登录

    otherAppId?: string // 第三方应用 id，也需要和存储的 otherAppId进行比较，如果不一致，则需要重新登录

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

        let jwt = MyLocalStorage.getItem(LocalStorageKey.JWT);

        const jwtExpireTsStr = MyLocalStorage.getItem(LocalStorageKey.JWT_EXPIRE_TS);

        if (jwtExpireTsStr) {

            const jwtExpireTs = Number(jwtExpireTsStr) || 0;

            const currentTime = GetServerTimestamp();

            if (currentTime >= jwtExpireTs) { // 如果：jwt过期了

                jwt = null

            }

        } else {

            jwt = null

        }

        if (jwt) {

            if (form.tenantId) {

                const storageTenantId = GetTenantIdFromStorage();

                if (storageTenantId !== form.tenantId) { // 如果和 jwt里面的租户 id不一致

                    jwt = null

                }

            }

        }

        if (jwt) {

            if (form.otherAppId) {

                const otherAppId = MyLocalStorage.getItem(LocalStorageKey.OTHER_APP_ID);

                if (otherAppId !== form.otherAppId) { // 如果和 存储的第三方应用 id不一致

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

            MyLocalStorage.setItem(LocalStorageKey.NO_JWT_URI, noJwtUrl)

        }

        if (form.mainUri) {
            MyLocalStorage.setItem(LocalStorageKey.MAIN_URI, form.mainUri)
        }

        if (form.mainRedirectUri) {
            MyLocalStorage.setItem(LocalStorageKey.MAIN_REDIRECT_URI, form.mainRedirectUri)
        }

        if (form.otherAppId) {
            MyLocalStorage.setItem(LocalStorageKey.OTHER_APP_ID, form.otherAppId)
        }

        if (hasJwtUrl) {
            MySessionStorage.setItem(SessionStorageKey.OAUTH2_REDIRECT_URI, hasJwtUrl)
        }

        if (jwt) {

            if (hasJwtUrl?.startsWith('http')) {

                window.location.href = hasJwtUrl!

            } else {

                GetAppNav()(form.mainUri || PathConstant.TOP_PATH)

            }

        } else {

            if (noJwtUrl?.startsWith('http')) {

                window.location.href = noJwtUrl!

            } else {

                GetAppNav()(noJwtUrl || PathConstant.TOP_PATH)

            }

        }


    }, [])

    return <div>...</div>;

}
