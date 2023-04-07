export interface NotNullId {
    id?: string // 主键id，required：true，format：int64
}

export interface SysDictInsertOrUpdateDTO {
    orderNo?: number // 排序号（值越大越前面，默认为 0），format：int32
    name?: string // 字典/字典项 名，required：true
    remark?: string // 备注
    id?: string // 主键 id，format：int64
    type?: string // 字典类型：1 字典 2 字典项，required：true
    dictKey?: string // 字典 key（不能重复），字典项要冗余这个 key，目的：方便操作，required：true
    value?: number // 字典项 value（数字 123...）备注：字典为 -1，format：int32
    enableFlag?: boolean // 是否启用
}

export interface NotEmptyIdSet {
    idSet?: string[] // 主键 idSet，required：true，format：int64
}

export interface ChangeNumberDTO {
    idSet?: string[] // 主键 idSet，required：true，format：int64
    number?: string // 需要改变的数值，required：true，format：int64
}
