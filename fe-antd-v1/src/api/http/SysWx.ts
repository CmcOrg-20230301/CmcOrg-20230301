import $http from "@/util/HttpUtil";
import {AxiosRequestConfig} from "axios";

export interface NotNullId {
    id?: string // 主键 id，required：true，format：int64
}

// 微信公众号：同步菜单
export function SysWxOfficialAccountUpdateMenu(form: NotNullId, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sys/wx/officialAccount/updateMenu', form, config)
}
