export interface NotNullId {
    id?: string // 主键id，format：int64
}

export interface ChangeNumberDTO {
    idSet?: array // 主键 idSet
    number?: string // 需要改变的数值，format：int64
}

export interface NotEmptyIdSet {
    idSet?: array // 主键 idSet
}

export interface SysMenuInsertOrUpdateDTO {
    redirect?: string // 重定向，优先级最高
    orderNo?: number // 排序号（值越大越前面，默认为 0），format：int32
    auths?: string // 权限，多个可用逗号拼接，例如：menu:insertOrUpdate,menu:page,menu:deleteByIdSet,menu:infoById
    icon?: string // 图标
    remark?: string // 备注
    authFlag?: boolean // 是否是权限菜单，权限菜单：不显示，只代表菜单权限
    parentId?: string // 父节点id（顶级则为0），format：int64
    showFlag?: boolean // 是否显示在 左侧的菜单栏里面，如果为 false，也可以通过 $router.push()访问到
    path?: string // 页面的 path，备注：相同父菜单下，子菜单 path不能重复
    router?: string // 路由
    name?: string // 菜单名
    roleIdSet?: array // 角色 idSet
    id?: string // 主键 id，format：int64
    firstFlag?: boolean // 是否是起始页面，备注：只能存在一个 firstFlag === true 的菜单
    enableFlag?: boolean // 是否启用
}
