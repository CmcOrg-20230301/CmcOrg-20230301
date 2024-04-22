import {$http, IHttpConfig} from "@/util/HttpUtil";

export interface SignInBrowserCodeDTO {
    code?: string // 第三方应用 code，required：true
    appId?: string // 第三方应用 appId，required：true
    tenantId?: string // 租户 id，可以为空，为空则表示：默认租户：0，format：int64
}

export interface SignInVO {
    jwtExpireTs?: string // jwt过期时间戳，format：int64
    jwt?: string // jwt
    tenantId?: string // 租户主键 id，format：int64
    jwtRefreshToken?: string // jwtRefreshToken
}

// 浏览器：企业微信 code登录
export function SignWxWorkSignInBrowserCode(form: SignInBrowserCodeDTO, config?: IHttpConfig) {
    return $http.myPost<SignInVO>('/sign/wxWork/sign/in/browser/code', form, config)
}
