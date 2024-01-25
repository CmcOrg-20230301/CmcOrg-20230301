import PathConstant from "@/model/constant/PathConstant";
import {useEffect} from "react";
import {SignInSuccess} from "@/page/sign/SignIn/SignInUtil";
import {SignWxSignInBrowserCode, SignWxSignInBrowserCodeUserInfo} from "@/api/http/SignWx";
import {GetAppNav} from "@/MyApp";
import CommonConstant from "@/model/constant/CommonConstant";
import LocalStorageKey from "@/model/constant/LocalStorageKey";
import {ApiResultVO} from "@/util/HttpUtil";
import SessionStorageKey from "@/model/constant/SessionStorageKey";
import {SignInVO} from "@/api/http/SignSignInName";

export interface IOauth2WxForm {

    code?: string
    tenantId?: string
    appId?: string
    type?: '1' | '2' | '3'
    redirect?: string // 重定向地址

}

function GoBlank() {
    GetAppNav()(PathConstant.BLANK_PATH, {state: {showText: '微信跳转失败，请重新打开'}})
}

// 处理登录返回值
function HandleWxSign(res: ApiResultVO<SignInVO>, form: IOauth2WxForm) {

    const noJwtUri = localStorage.getItem(LocalStorageKey.NO_JWT_URI);

    SignInSuccess(res.data, undefined, false, false)

    if (noJwtUri) {

        localStorage.setItem(LocalStorageKey.NO_JWT_URI, noJwtUri)

    } else {

        localStorage.setItem(LocalStorageKey.NO_JWT_URI, PathConstant.BLANK_PATH + "?showText=登录过期，请重新打开页面")

    }

    if (form.redirect) {

        sessionStorage.setItem(SessionStorageKey.OAUTH2_REDIRECT_URI, form.redirect)

    }

    GetAppNav()(PathConstant.BLANK_LAYOUT_PATH)

}

// Oauth2Wx
export default function () {

    useEffect(() => {

        // type：1 静默授权 2 跳转获取用户信息 3 执行获取用户信息
        // 例如：?code=123&tenantId=456&appId=789&type=1
        // 备注：code字段微信那边会传递
        // 备注：不要用 state字段传递值，直接拼接到：redirect_uri 字段里，备注：记得 UrlEncode编码
        // 例如：https://cmcopen.top/oauth2/wx?tenantId=0&appId=wx7bb902caf641785d&type=1
        // UrlEncode编码之后：https%3A%2F%2Fcmcopen.top%2Foauth2%2Fwx%3FtenantId%3D0%26appId%3Dwx7bb902caf641785d%26type%3D1
        let search = window.location.search;

        if (!search) {

            GoBlank();
            return

        }

        // 使用微信跳转网页之后的 code，换取：本系统的jwt
        search = search.split("?")[1]

        let form: IOauth2WxForm = {}

        search.split("&").forEach(item => {

            let splitArr = item.split("=");

            form[splitArr[0]] = splitArr[1]

        })

        if (!form.code || !form.appId) {

            GoBlank();
            return

        }

        if (!form.type) {
            form.type = '1'
        }

        if (!form.tenantId) {
            form.tenantId = CommonConstant.TOP_TENANT_ID_STR
        }

        if (!form.redirect) {
            form.redirect = ''
        }

        localStorage.removeItem(LocalStorageKey.JWT) // 移除：jwt

        if (form.type === '2') { // 需要再跳转一次

            let url = window.location.origin + window.location.pathname;

            url = url + `?tenantId=${form.tenantId}&appId=${form.appId}&type=3&redirect=${form.redirect}`

            const urlEncode = encodeURIComponent(url);

            window.location.href = `https://open.weixin.qq.com/connect/oauth2/authorize?appid=${form.appId}&redirect_uri=${urlEncode}&response_type=code&scope=snsapi_userinfo#wechat_redirect`

        } else if (form.type === '3') { // 获取：用户信息，并注册

            // 登录，获取：jwt
            SignWxSignInBrowserCodeUserInfo(form).then(res => {

                HandleWxSign(res, form); // 处理登录返回值

            })

        } else {

            // 登录，获取：jwt
            SignWxSignInBrowserCode(form).then(res => {

                HandleWxSign(res, form); // 处理登录返回值

            })

        }

    }, [])

    return null;

}
