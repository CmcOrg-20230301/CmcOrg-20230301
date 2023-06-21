import {ProSchemaValueEnumType, RequestData} from "@ant-design/pro-components";
import {ListToTree} from "./TreeUtil";
import MyPageDTO from "@/model/dto/MyPageDTO";
import {AxiosRequestConfig} from "axios";
import {SysDictListByDictKey} from "@/api/SysDict";

// 将 map转换为 下拉选 list
export function NumberTextMapToSelectList(map: Map<string, { text: string }>) {

    const resultList: DictLongListVO[] = []

    map.forEach((value, key) => {

        resultList.push({
            value: key,
            label: value.text,
        })

    })

    return resultList;

}

// 将 map转换为 下拉选 list
export function NumberStringMapToSelectList(map: Map<string, string>) {

    const resultList: DictLongListVO[] = []

    map.forEach((value, key) => {

        resultList.push({
            value: key,
            label: value,
        })

    })

    return resultList;

}

export const YesNoDict = new Map<boolean, ProSchemaValueEnumType>();
YesNoDict.set(true, {text: '是', status: 'success'})
YesNoDict.set(false, {text: '否', status: 'error'})

export const YesNoBaseDict = new Map<boolean, ProSchemaValueEnumType>();
YesNoBaseDict.set(true, {text: '是'})
YesNoBaseDict.set(false, {text: '否'})

// 根据 list和 value，获取字典的 label值
export function getByValueFromDictList(
    dictList: DictLongListVO [],
    value: string,
    defaultValue: string = '-'
) {

    let res: string | undefined = defaultValue

    dictList.some((item) => {

        if (item.value === value) {

            res = item.label
            return true // 结束当前循环

        }

    })

    return res

}

// 根据 list和 valueList，获取字典的 labelList值
export function getByValueFromDictListPro(
    dictList: DictLongListVO [],
    valueList?: string[],
    defaultValue: string = '-',
    separator: string = '，'
) {

    let resList: string[] = []

    if (dictList && valueList && valueList.length) {

        dictList.forEach((item) => {

            if (valueList.includes(item.value)) {

                resList.push(item.label)

            }

        })
    }

    return resList.length ? resList.join(separator) : defaultValue

}

// 通用的，获取字典集合，方法返回值
export interface DictLongListVO {

    label: string // 显示用
    value: string // 传值用

}

// 通用的，获取字典集合，接口返回值
interface IDictResult {

    name?: string
    id?: string

}

// 通用的，获取字典集合
export function GetDictList<T extends IDictResult>(requestFunction: (value: MyPageDTO | any, config?: AxiosRequestConfig) => Promise<RequestData<T>>) {

    return new Promise<DictLongListVO[]>(resolve => {

        requestFunction({pageSize: '-1'}).then(res => {

            let dictList: DictLongListVO[] = []

            if (res.data) {

                dictList = res.data.map(item => ({

                    label: item.name!,
                    value: item.id!,

                }));

            }

            resolve(dictList)

        })

    })

}

// 通用的，获取字典树集合，方法返回值
export interface IMyTree extends DictLongListVO {

    id: string
    key: string
    label: string // 备注：和 title是一样的值
    title: string
    parentId: string
    orderNo: number
    children?: IMyTree []

}

// 通用的，获取字典树集合，接口返回值
interface IDictTreeResult {

    name?: string
    id?: string
    parentId?: string
    orderNo?: number

}

// 通用的，获取字典树集合
export function GetDictTreeList<T extends IDictTreeResult>(requestFunction: (value: MyPageDTO, config?: AxiosRequestConfig) => Promise<RequestData<T>>, toTreeFlag: boolean = true) {

    return new Promise<IMyTree[]>(resolve => {

        requestFunction({pageSize: '-1'}).then(res => {

            let dictList: IMyTree[] = []

            if (res.data) {

                dictList = res.data.map(item => ({

                    id: item.id!,
                    key: item.id!,
                    value: item.id!,
                    label: item.name!,
                    title: item.name!,
                    parentId: item.parentId!,
                    orderNo: item.orderNo!,

                }));

            }

            if (toTreeFlag) {

                resolve(ListToTree(dictList))

            } else {

                resolve(dictList)

            }

        })

    })

}

// 通过 key，获取：字典集合
export function GetDictListByKey(dictKey: string) {

    return new Promise<DictLongListVO[]>(async resolve => {

        await SysDictListByDictKey({dictKey}).then(res => {

            let dictList: DictLongListVO[] = []

            if (res.data) {

                dictList = res.data.map(item => ({

                    label: item.name!,

                    value: item.id!,

                }));

            }

            resolve(dictList)

        })

    })

}
