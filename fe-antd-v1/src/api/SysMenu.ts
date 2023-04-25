import {SortOrder} from "antd/es/table/interface";
import MyOrderDTO from "@/model/dto/MyOrderDTO";
import $http from "@/util/HttpUtil";
import {AxiosRequestConfig} from "axios";

export interface SysMenuPageDTO {
    redirect?: string // 重定向，优先级最高
    linkFlag?: boolean // 是否外链，即，打开页面会在一个新的窗口打开
    auths?: string // 权限，多个可用逗号拼接，例如：menu:insertOrUpdate,menu:page,menu:deleteByIdSet,menu:infoById
    pageSize?: string // 每页显示条数，format：int64
    authFlag?: boolean // 是否是权限菜单，权限菜单：不显示，只代表菜单权限
    parentId?: string // 父节点id（顶级则为0），format：int64
    showFlag?: boolean // 是否显示在 左侧的菜单栏里面，如果为 false，也可以通过 $router.push()访问到
    path?: string // 页面的 path，备注：相同父菜单下，子菜单 path不能重复
    current?: string // 第几页，format：int64
    router?: string // 路由
    name?: string // 菜单名
    firstFlag?: boolean // 是否是起始页面，备注：只能存在一个 firstFlag === true 的菜单
    enableFlag?: boolean // 是否启用
    order?: MyOrderDTO // 排序字段
    sort?: Record<string, SortOrder> // 排序字段（只在前端使用，实际传值：order）
}

export interface SysMenuDO {
    redirect?: string // 重定向，优先级最高
    linkFlag?: boolean // 是否外链，即，打开页面会在一个新的窗口打开
    orderNo?: number // 排序号（值越大越前面，默认为 0），format：int32
    auths?: string // 权限，多个可用逗号拼接，例如：menu:insertOrUpdate,menu:page,menu:deleteByIdSet,menu:infoById
    icon?: string // 图标
    updateTime?: string // 修改时间，format：date-time
    remark?: string // 备注
    delFlag?: boolean // 是否逻辑删除
    authFlag?: boolean // 是否是权限菜单，权限菜单：不显示，只代表菜单权限
    version?: number // 乐观锁，format：int32
    parentId?: string // 父节点id（顶级则为0），format：int64
    showFlag?: boolean // 是否显示在 左侧的菜单栏里面，如果为 false，也可以通过 $router.push()访问到
    updateId?: string // 修改人id，format：int64
    path?: string // 页面的 path，备注：相同父菜单下，子菜单 path不能重复
    router?: string // 路由
    createTime?: string // 创建时间，format：date-time
    children?: SysMenuDO[] // 子节点
    createId?: string // 创建人id，format：int64
    name?: string // 菜单名
    id?: string // 主键id，format：int64
    firstFlag?: boolean // 是否是起始页面，备注：只能存在一个 firstFlag === true 的菜单
    enableFlag?: boolean // 是否启用
}

// 分页排序查询
export function SysMenuPage(form: SysMenuPageDTO, config?: AxiosRequestConfig) {
    return $http.myProPagePost<SysMenuDO>('/sys/menu/page', form, config)
}

// 查询：树结构
export function SysMenuTree(form: SysMenuPageDTO, config?: AxiosRequestConfig) {
    return $http.myProTreePost<SysMenuDO[]>('/sys/menu/tree', form, config)
}

export interface NotNullId {
    id?: string // 主键id，required：true，format：int64
}

export interface SysMenuInfoByIdVO {
    redirect?: string // 重定向，优先级最高
    linkFlag?: boolean // 是否外链，即，打开页面会在一个新的窗口打开
    orderNo?: number // 排序号（值越大越前面，默认为 0），format：int32
    auths?: string // 权限，多个可用逗号拼接，例如：menu:insertOrUpdate,menu:page,menu:deleteByIdSet,menu:infoById
    icon?: string // 图标
    updateTime?: string // 修改时间，format：date-time
    remark?: string // 备注
    delFlag?: boolean // 是否逻辑删除
    authFlag?: boolean // 是否是权限菜单，权限菜单：不显示，只代表菜单权限
    version?: number // 乐观锁，format：int32
    parentId?: string // 父节点id（顶级则为0），format：int64
    showFlag?: boolean // 是否显示在 左侧的菜单栏里面，如果为 false，也可以通过 $router.push()访问到
    updateId?: string // 修改人id，format：int64
    path?: string // 页面的 path，备注：相同父菜单下，子菜单 path不能重复
    router?: string // 路由
    createTime?: string // 创建时间，format：date-time
    children?: SysMenuDO[] // 子节点
    createId?: string // 创建人id，format：int64
    name?: string // 菜单名
    roleIdSet?: string[] // 角色 idSet，format：int64
    id?: string // 主键id，format：int64
    firstFlag?: boolean // 是否是起始页面，备注：只能存在一个 firstFlag === true 的菜单
    enableFlag?: boolean // 是否启用
}

// 通过主键id，查看详情
export function SysMenuInfoById(form: NotNullId, config?: AxiosRequestConfig) {
    return $http.myProPost<SysMenuInfoByIdVO>('/sys/menu/infoById', form, config)
}

export interface ChangeNumberDTO {
    idSet?: string[] // 主键 idSet，required：true，format：int64
    number?: string // 需要改变的数值，required：true，format：int64
}

// 通过主键 idSet，加减排序号
export function SysMenuAddOrderNo(form: ChangeNumberDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sys/menu/addOrderNo', form, config)
}

export interface NotEmptyIdSet {
    idSet?: string[] // 主键 idSet，required：true，format：int64
}

// 批量删除
export function SysMenuDeleteByIdSet(form: NotEmptyIdSet, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sys/menu/deleteByIdSet', form, config)
}

// 获取：当前用户绑定的菜单
export function SysMenuUserSelfMenuList(config?: AxiosRequestConfig) {
    return $http.myPost<SysMenuDO[]>('/sys/menu/userSelfMenuList', undefined, config)
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
    name?: string // 菜单名，required：true
    roleIdSet?: string[] // 角色 idSet，format：int64
    id?: string // 主键 id，format：int64
    firstFlag?: boolean // 是否是起始页面，备注：只能存在一个 firstFlag === true 的菜单
    enableFlag?: boolean // 是否启用
}

// 新增/修改
export function SysMenuInsertOrUpdate(form: SysMenuInsertOrUpdateDTO, config?: AxiosRequestConfig) {
    return $http.myPost<string>('/sys/menu/insertOrUpdate', form, config)
}
