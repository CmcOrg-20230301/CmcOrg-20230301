import {$http, IHttpConfig} from "@/util/HttpUtil";

export interface NotNullId {
    id?: string // 主键 id，required：true，format：int64
}

// 微信公众号：同步菜单
export function SysWxOfficialAccountUpdateMenu(form: NotNullId, config?: IHttpConfig) {
    return $http.myPost<string>('/sys/wx/officialAccount/updateMenu', form, config)
}
