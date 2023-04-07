export interface NotNullId {
    id?: string // 主键id，required：true，format：int64
}

export interface SysParamInsertOrUpdateDTO {
    name?: string // 配置名，以 id为不变值进行使用，不要用此属性，required：true
    remark?: string // 备注
    id?: string // 主键 id，format：int64
    value?: string // 值，required：true
    enableFlag?: boolean // 是否启用
}

export interface NotEmptyIdSet {
    idSet?: string[] // 主键 idSet，required：true，format：int64
}
