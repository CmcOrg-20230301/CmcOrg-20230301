import {$http, IHttpConfig} from "@/util/HttpUtil";

export interface SysUserConfigurationDO {
    emailSignUpEnable?: boolean // 是否启用：邮箱注册功能，默认启用
    signInNameSignUpEnable?: boolean // 是否启用：用户名注册功能，默认启用
    id?: string // 租户主键 id，format：int64
    phoneSignUpEnable?: boolean // 是否启用：手机号码注册功能，默认启用
}

// 通过主键id，查看详情
export function SysUserConfigurationInfoById(config?: IHttpConfig) {
    return $http.myProPost<SysUserConfigurationDO>('/sys/userConfiguration/infoById', undefined, config)
}

export interface SysUserConfigurationInsertOrUpdateDTO {
    emailSignUpEnable?: boolean // 是否启用：邮箱注册功能
    signInNameSignUpEnable?: boolean // 是否启用：用户名注册功能
    phoneSignUpEnable?: boolean // 是否启用：手机号码注册功能
}

// 新增/修改
export function SysUserConfigurationInsertOrUpdate(form: SysUserConfigurationInsertOrUpdateDTO, config?: IHttpConfig) {
    return $http.myPost<string>('/sys/userConfiguration/insertOrUpdate', form, config)
}
