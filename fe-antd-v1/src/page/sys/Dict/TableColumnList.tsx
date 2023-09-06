import {GetDictList, NoFormGetDictTreeList, YesNoDict} from "@/util/DictUtil";
import {Dropdown, Tag, TreeSelect} from "antd";
import {EllipsisOutlined} from "@ant-design/icons/lib";
import {ActionType, ProColumns} from "@ant-design/pro-components";
import {SysDictDeleteByIdSet, SysDictDO, SysDictInsertOrUpdateDTO} from "@/api/http/SysDict";
import {ExecConfirm, ToastSuccess} from "@/util/ToastUtil";
import {CalcOrderNo} from "@/util/TreeUtil";
import {SysTenantDictList} from "@/api/http/SysTenant";
import {SearchTransform} from "@/util/CommonUtil";
import {DictTypeDict} from "@/page/sys/Dict/SchemaFormColumnList";

const TableColumnList = (currentForm: React.MutableRefObject<SysDictInsertOrUpdateDTO>, setFormOpen: React.Dispatch<React.SetStateAction<boolean>>, actionRef: React.RefObject<ActionType | undefined>): ProColumns<SysDictDO>[] => [

    {
        title: '序号',
        dataIndex: 'index',
        valueType: 'index',
        width: 90,
    },

    {
        title: '租户', dataIndex: 'tenantId', ellipsis: true, width: 90, hideInSearch: true, valueType: 'select',
        request: () => {
            return GetDictList(SysTenantDictList)
        }
    },

    {
        title: '租户', dataIndex: 'tenantIdSet', ellipsis: true, width: 90, hideInTable: true, valueType: 'treeSelect',
        fieldProps: {
            placeholder: '请选择',
            allowClear: true,
            treeNodeFilterProp: 'title',
            maxTagCount: 'responsive',
            treeCheckable: true,
            showCheckedStrategy: TreeSelect.SHOW_ALL,
            treeCheckStrictly: true,
        },
        request: () => {
            return NoFormGetDictTreeList(SysTenantDictList, true, '-1')
        },
        search: {
            transform: (valueArr: { label: string, value: string }[]) =>
                SearchTransform(valueArr, 'tenantIdSet')
        }
    },

    {title: 'key', dataIndex: 'dictKey', ellipsis: true,},

    {
        title: '类别', dataIndex: "type", valueEnum: DictTypeDict,
        render: (dom, entity) =>
            <Tag color={entity.type as any === 1 ? 'purple' : 'green'}>{entity.type as any === 1 ? '字典' : '字典项'}</Tag>
    },

    {title: '名称', dataIndex: 'name', ellipsis: true,},

    {title: '值', dataIndex: 'value', ellipsis: true, width: 90,},

    {title: '排序号', dataIndex: 'orderNo', ellipsis: true, hideInSearch: true,},

    {
        title: '创建时间',
        dataIndex: 'createTime',
        hideInSearch: true,
        valueType: 'fromNow',
    },

    {
        title: '修改时间',
        dataIndex: 'updateTime',
        hideInSearch: true,
        valueType: 'fromNow',
    },

    {
        title: '系统内置',
        dataIndex: 'systemFlag',
        valueEnum: YesNoDict
    },

    {
        title: '是否启用',
        dataIndex: 'enableFlag',
        valueEnum: YesNoDict
    },

    {title: '备注', dataIndex: 'remark', ellipsis: true, width: 90,},

    {

        title: '操作',
        dataIndex: 'option',
        valueType: 'option',

        render: (dom, entity) => [

            <a key="1" onClick={() => {

                currentForm.current = {id: entity.id} as SysDictInsertOrUpdateDTO
                setFormOpen(true)

            }}>编辑</a>,

            <a key="2" className={"red3"} onClick={() => {

                ExecConfirm(() => {

                    return SysDictDeleteByIdSet({idSet: [entity.id!]}).then(res => {

                        ToastSuccess(res.msg)
                        actionRef.current?.reload()

                    })

                }, undefined, `确定删除【${entity.name}】吗？`)

            }}>删除</a>,

            (

                entity.type as any === 1 &&

                <Dropdown

                    key="3"

                    menu={{

                        items: [

                            {

                                key: '1',

                                label: <a onClick={() => {

                                    currentForm.current = {dictKey: entity.dictKey, type: 2 as any, value: 1}

                                    CalcOrderNo(currentForm.current, entity, ({item}) => {

                                        if (item!.value! >= currentForm.current.value!) {

                                            currentForm.current.value = Number(item!.value) + 1 // 如果存在字典项，那么则取最大的 value + 1

                                        }

                                    })

                                    setFormOpen(true)

                                }}>

                                    添加字典项

                                </a>,

                            },
                        ]

                    }}

                >

                    <a><EllipsisOutlined/></a>

                </Dropdown>

            )

        ],

    },

];

export default TableColumnList
