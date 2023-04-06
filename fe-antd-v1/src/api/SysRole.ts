export interface SysRoleInsertOrUpdateDTO {
    userIdSet?: array // 用户 idSet
    defaultFlag?: boolean // 是否是默认角色，备注：只会有一个默认角色
    name?: string // 角色名，不能重复
    menuIdSet?: array // 菜单 idSet
    remark?: string // 备注
    id?: string // 主键 id，format：int64
    enableFlag?: boolean // 是否启用
}

export interface NotNullId {
    id?: string // 主键id，format：int64
}

export interface NotEmptyIdSet {
    idSet?: array // 主键 idSet
}
