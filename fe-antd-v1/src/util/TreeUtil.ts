interface IFlatTree {

    children?: any[]

}

// 扁平化树结构，备注：hasParentFlag：是否需要父节点，checkFun：return true的节点，才需要添加
export function FlatTree<T extends IFlatTree>(
    data?: T[],
    hasParentFlag: boolean = true,
    checkFun?: (item: T) => boolean
) {

    let resultList: T[] = []

    if (!data) {
        return resultList
    }

    data.forEach((item) => {

        let flag = true

        if (checkFun) {
            flag = checkFun(item) // 过滤不要的节点
        }

        if (flag) {

            const hasChildren = item.children && item.children.length

            if (hasChildren) {

                if (hasParentFlag) {

                    resultList.push(JSON.parse(JSON.stringify({...item, children: null}))) // 添加

                }

                // 组装数据
                resultList = resultList.concat(
                    FlatTree(item.children, hasParentFlag, checkFun)
                )

            } else {

                resultList.push(JSON.parse(JSON.stringify({...item, children: null}))) // 添加

            }

        }

    })

    return resultList

}

// 获取树结构，所有 有子节点的 id集合
export function GetIdListForHasChildrenNode(data: any[]) {

    let resultList: string[] = []

    data.forEach((item) => {

        if (item.children && item.children.length) {

            resultList.push(item.id)
            resultList = resultList.concat(GetIdListForHasChildrenNode(item.children))

        }

    })

    return resultList

}

// childrenFlag：true：children始终为 [] false：children为空时，children = ''
export function ListToTree(
    list: any[],
    pid: string | number = '0',
    childrenFlag: boolean = true,
    childrenName: string = 'children'
) {

    const resultList: any[] = [] // 本方法返回值
    const listMap = new Map<string | number, any>(); // 把 list的所有元素转换为：id -> 元素，格式

    list.forEach((item) => {

        let mapDTO = listMap.get(item.id);

        if (mapDTO) {

            // 如果存在 当前元素，则补充其他属性
            mapDTO = {...item, ...mapDTO}

        } else {

            mapDTO = item;

            if (!mapDTO![childrenName]) { // 避免：mapDTO里面原来就有 children

                // 如果 item不存在 children
                if (childrenFlag) {

                    mapDTO![childrenName] = []

                } else {

                    mapDTO![childrenName] = ''

                }

            }

        }

        listMap.set(mapDTO.id, mapDTO);

        if (mapDTO.parentId === pid) {

            resultList.push(mapDTO) // 添加到返回值里
            return

        }

        // 把自己添加到：父节点的 children上
        let parentDTO = listMap.get(mapDTO.parentId);

        if (parentDTO) {

            if (parentDTO[childrenName]) {
                parentDTO[childrenName].push(mapDTO)
            } else {
                parentDTO[childrenName] = [mapDTO]
            }

        } else {

            listMap.set(mapDTO.parentId, {[childrenName]: [mapDTO]})

        }

    })

    // 如果，顶层的节点不是 0，则需要找到顶层节点的 id
    ListToTreeHandleResultList(resultList, listMap);

    return resultList

}

// 如果，顶层的节点不是 0，则需要找到顶层节点的 id
function ListToTreeHandleResultList(resultList: any[], listMap: Map<string | number, any>) {

    if (listMap.size === 0 || resultList.length !== 0) {
        return;
    }

    // 处理：topIdSet：通过：父级 id分组，value：子级 idSet
    const groupParentIdMap = new Map<string | number, Set<string | number>>();

    listMap.forEach((value) => {

        let set = groupParentIdMap.get(value.parentId);

        if (set) {

            set.add(value.id!)

        } else {

            set = new Set<string | number>();

            set.add(value.id)

            groupParentIdMap.set(value.parentId, set)

        }

    })

    groupParentIdMap.forEach((value, key) => {

        if (!groupParentIdMap.has(key)) { // 如果：不存在该父节点，则表示是：顶层节点

            value.forEach(subValue => {

                resultList.push(listMap.get(subValue)); // 添加：顶层节点

            })

        }

    })

}

interface ICalcOrderNoForm {

    orderNo?: number

}

interface ICalcOrderNoRecord {

    children?: ICalcOrderNoForm[]

}

export const DefaultOrderNo = 10000 // 默认 orderNo为 10000

// 计算 orderNo
export function CalcOrderNo<T extends ICalcOrderNoForm, D extends ICalcOrderNoRecord>(
    form: T,
    record: D,
    calcFun?: ({
                   item,
                   form,
                   record,
               }:
                   {
                       item: T
                       form: T
                       record: D
                   }) => void
) {

    if (record.children && record.children.length) {

        let orderNo = Number(record.children[0].orderNo) - 100 // 默认为第一个子节点元素减去 100

        form.orderNo = orderNo < 0 ? 0 : orderNo

        // 如果存在子节点，那么取最小的 orderNo - 100，如果 减完之后小于零，则为 0
        record.children.forEach((item) => {

            if (calcFun) {
                calcFun({item: item as T, form, record}) // 计算其他属性
            }

            // orderNo <= 0 的不进行计算
            if (

                item.orderNo &&
                form.orderNo &&
                item.orderNo > 0 &&
                item.orderNo <= form.orderNo

            ) {

                orderNo = item.orderNo - 100
                form.orderNo = orderNo < 0 ? 0 : orderNo

            }

        })

    } else {

        if (form.orderNo === undefined || form.orderNo === null) {
            form.orderNo = DefaultOrderNo
        }

    }

}
