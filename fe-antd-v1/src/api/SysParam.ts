export interface NotNullId {
    id?: string // 主键id，format：int64
}

export interface SysParamInsertOrUpdateDTO {
    name?: string // 配置名，以 id为不变值进行使用，不要用此属性
    remark?: string // 备注
    id?: string // 主键 id，format：int64
    value?: string // 值
    enableFlag?: boolean // 是否启用
}

export interface NotEmptyIdSet {
    idSet?: array // 主键 idSet
}
