import {SortOrder} from "antd/es/table/interface";
import MyOrderDTO from "@/model/dto/MyOrderDTO";
import $http from "@/util/HttpUtil";
import {AxiosRequestConfig} from "axios";

export interface AddOrderNoDTO {

    number: number // 统一加减的数值
    idSet: number[] // 主键 idSet

}

// 通过主键 idSet，加减排序号
export function SysMenuAddOrderNo(form: AddOrderNoDTO, config?: AxiosRequestConfig) {

    return $http.myPost<string>('/sys/menu/addOrderNo', form, config)

}

export interface NotEmptyIdSet {

    idSet: number[] // 主键 idSet

}

// 批量删除
export function SysMenuDeleteByIdSet(form: NotEmptyIdSet, config?: AxiosRequestConfig) {

    return $http.myPost<string>('/sys/menu/deleteByIdSet', form, config)

}

export interface NotNullId {

    id: number // 主键id {"min":1}

}

export interface SysMenuInfoByIdVO {

    roleIdSet?: number[] // 角色 idSet
    name?: string // 菜单名
    path?: string // 页面的 path，备注：相同父菜单下，子菜单 path不能重复
    router?: string // 路由
    auths?: string // 权限，多个可用逗号拼接，例如：menu:insertOrUpdate,menu:page,menu:deleteByIdSet,menu:infoById
    authFlag?: boolean // 是否是权限菜单，权限菜单：不显示，只代表菜单权限
    showFlag?: boolean // 是否显示在 左侧的菜单栏里面，如果为 false，也可以通过 $router.push()访问到
    linkFlag?: boolean // 是否外链，即，打开页面会在一个新的窗口打开
    redirect?: string // 重定向，优先级最高
    firstFlag?: boolean // 是否是起始页面，备注：只能存在一个 firstFlag === true 的菜单
    icon?: string // 图标
    orderNo?: number // 排序号（值越大越前面，默认为 0）
    parentId?: number // 父节点id（顶级则为0）
    children?: SysMenuInfoByIdVO[] // 子节点
    id?: number // 主键id
    createId?: number // 创建人id
    createTime?: string // 创建时间
    updateId?: number // 修改人id
    updateTime?: string // 修改时间
    version?: number // 乐观锁
    enableFlag?: boolean // 是否启用
    delFlag?: boolean // 是否逻辑删除
    remark?: string // 备注

}

// 通过主键id，查看详情
export function SysMenuInfoById(form: NotNullId, config?: AxiosRequestConfig) {

    return $http.myProPost<SysMenuInfoByIdVO>('/sys/menu/infoById', form, config)

}

export interface SysMenuInsertOrUpdateDTO {

    parentId?: number // 父节点id（顶级则为0）
    name: string // 菜单名
    path?: string // 页面的 path，备注：相同父菜单下，子菜单 path不能重复
    router?: string // 路由
    icon?: string // 图标
    auths?: string // 权限，多个可用逗号拼接，例如：menu:insertOrUpdate,menu:page,menu:deleteByIdSet,menu:infoById
    authFlag?: boolean // 是否是权限菜单，权限菜单：不显示，只代表菜单权限
    roleIdSet?: number[] // 角色 idSet
    enableFlag?: boolean // 是否启用
    firstFlag?: boolean // 是否是起始页面，备注：只能存在一个 firstFlag === true 的菜单
    orderNo?: number // 排序号（值越大越前面，默认为 0）
    showFlag?: boolean // 是否显示在 左侧的菜单栏里面，如果为 false，也可以通过 $router.push()访问到
    redirect?: string // 重定向，优先级最高
    remark?: string // 备注
    id?: number // 主键id {"min":1}

}

// 新增/修改
export function SysMenuInsertOrUpdate(form: SysMenuInsertOrUpdateDTO, config?: AxiosRequestConfig) {

    return $http.myPost<string>('/sys/menu/insertOrUpdate', form, config)

}

export interface SysMenuPageDTO {

    name?: string // 菜单名
    path?: string // 页面的 path，备注：相同父菜单下，子菜单 path不能重复
    parentId?: number // 父节点id（顶级则为0）
    auths?: string // 权限，多个可用逗号拼接，例如：menu:insertOrUpdate,menu:page,menu:deleteByIdSet,menu:infoById
    showFlag?: boolean // 是否显示在 左侧的菜单栏里面，如果为 false，也可以通过 $router.push()访问到
    enableFlag?: boolean // 是否启用
    linkFlag?: boolean // 是否外链，即，打开页面会在一个新的窗口打开
    router?: string // 路由
    redirect?: string // 重定向，优先级最高
    firstFlag?: boolean // 是否是起始页面，备注：只能存在一个 firstFlag === true 的菜单
    authFlag?: boolean // 是否是权限菜单，权限菜单：不显示，只代表菜单权限
    current?: number // 第几页
    pageSize?: number // 每页显示条数
    order?: MyOrderDTO // 排序字段
    sort?: Record<string, SortOrder> // 排序字段（只在前端使用，实际传值：order）

}

export interface SysMenuDO {

    name?: string // 菜单名
    path?: string // 页面的 path，备注：相同父菜单下，子菜单 path不能重复
    router?: string // 路由
    auths?: string // 权限，多个可用逗号拼接，例如：menu:insertOrUpdate,menu:page,menu:deleteByIdSet,menu:infoById
    authFlag?: boolean // 是否是权限菜单，权限菜单：不显示，只代表菜单权限
    showFlag?: boolean // 是否显示在 左侧的菜单栏里面，如果为 false，也可以通过 $router.push()访问到
    linkFlag?: boolean // 是否外链，即，打开页面会在一个新的窗口打开
    redirect?: string // 重定向，优先级最高
    firstFlag?: boolean // 是否是起始页面，备注：只能存在一个 firstFlag === true 的菜单
    icon?: string // 图标
    orderNo?: number // 排序号（值越大越前面，默认为 0）
    parentId?: number // 父节点id（顶级则为0）
    children?: SysMenuDO[] // 子节点
    id?: number // 主键id
    createId?: number // 创建人id
    createTime?: string // 创建时间
    updateId?: number // 修改人id
    updateTime?: string // 修改时间
    version?: number // 乐观锁
    enableFlag?: boolean // 是否启用
    delFlag?: boolean // 是否逻辑删除
    remark?: string // 备注

}

// 分页排序查询
export function SysMenuPage(form: SysMenuPageDTO, config?: AxiosRequestConfig) {

    return $http.myProPagePost<SysMenuDO>('/sys/menu/page', form, config)

}

// 查询：树结构
export function SysMenuTree(form: SysMenuPageDTO, config?: AxiosRequestConfig) {

    return $http.myProTreePost<SysMenuDO>('/sys/menu/tree', form, config)

}

// 获取：当前用户绑定的菜单
export function SysMenuUserSelfMenuList(config?: AxiosRequestConfig) {

    return $http.myProTreePost<SysMenuDO>('/sys/menu/userSelfMenuList', undefined, config)

}
