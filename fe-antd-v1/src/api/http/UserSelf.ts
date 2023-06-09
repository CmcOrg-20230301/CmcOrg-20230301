import $http from "@/util/HttpUtil";
import {AxiosRequestConfig} from "axios";

export interface UserSelfUpdateInfoDTO {
    nickname?: string // 昵称，正则表达式：^[\u4E00-\u9FA5A-Za-z0-9_-]{2,20}$
    bio?: string // 个人简介
}

// 当前用户：基本信息：修改
export function UserSelfUpdateInfo(form: UserSelfUpdateInfoDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/user/self/updateInfo', form, config)
}

// 当前用户：重置头像
export function UserSelfResetAvatar(config?: AxiosRequestConfig) {
    return $http.myPost<string>('/user/self/resetAvatar', undefined, config)
}

export interface UserSelfInfoVO {
    passwordFlag?: boolean // 是否有密码，用于前端显示，修改密码/设置密码
    phone?: string // 手机号码，会脱敏
    createTime?: string // 账号注册时间，format：date-time
    wxOpenId?: string // 微信 openId，会脱敏
    avatarFileId?: string // 头像 fileId（文件主键 id），format：int64
    signInName?: string // 登录名，会脱敏
    nickname?: string // 昵称，正则表达式：^[\u4E00-\u9FA5A-Za-z0-9_-]{2,20}$
    bio?: string // 个人简介
    email?: string // 邮箱，会脱敏
}

// 获取：当前用户，基本信息
export function UserSelfInfo(config?: AxiosRequestConfig) {
    return $http.myPost<UserSelfInfoVO>('/user/self/info', undefined, config)
}

// 当前用户：刷新jwt私钥后缀
export function UserSelfRefreshJwtSecretSuf(config?: AxiosRequestConfig) {
    return $http.myPost<string>('/user/self/refreshJwtSecretSuf', undefined, config)
}
