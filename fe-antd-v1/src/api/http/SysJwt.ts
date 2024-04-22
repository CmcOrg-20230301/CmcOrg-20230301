import {$http, IHttpConfig} from "@/util/HttpUtil";

export interface SysJwtRefreshSignInRefreshTokenDTO {
    tenantId?: string // 租户主键 id，format：int64
    refreshToken?: string // refreshToken
}

export interface SignInVO {
    jwtExpireTs?: string // jwt过期时间戳，format：int64
    jwt?: string // jwt
    tenantId?: string // 租户主键 id，format：int64
    jwtRefreshToken?: string // jwtRefreshToken
}

// 通过：refreshToken登录
export function SysJwtRefreshSignInRefreshToken(form: SysJwtRefreshSignInRefreshTokenDTO, config?: IHttpConfig) {
    return $http.myPost<SignInVO>('/sys/jwt/refresh/sign/in/refreshToken', form, config)
}
