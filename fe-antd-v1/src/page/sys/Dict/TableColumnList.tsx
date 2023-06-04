import {YesNoDict} from "@/util/DictUtil";
import {Dropdown, Tag} from "antd";
import {EllipsisOutlined} from "@ant-design/icons/lib";
import {ActionType, ProColumns} from "@ant-design/pro-components";
import {SysDictDeleteByIdSet, SysDictDO, SysDictInsertOrUpdateDTO} from "@/api/SysDict";
import {ExecConfirm, ToastSuccess} from "@/util/ToastUtil";
import {CalcOrderNo} from "@/util/TreeUtil";

const TableColumnList = (currentForm: React.MutableRefObject<SysDictInsertOrUpdateDTO | null>, setFormOpen: React.Dispatch<React.SetStateAction<boolean>>, actionRef: React.RefObject<ActionType | undefined>): ProColumns<SysDictDO>[] => [

    {
        title: '序号',
        dataIndex: 'index',
        valueType: 'index',
        width: 90,
    },

    {title: 'key', dataIndex: 'dictKey', ellipsis: true,},

    {
        title: '类别', dataIndex: "type",
        render: (dom, entity) =>
            <Tag color={entity.type === 1 ? 'purple' : 'green'}>{entity.type === 1 ? '字典' : '字典项'}</Tag>
    },

    {title: '名称', dataIndex: 'name', ellipsis: true,},

    {title: 'value', dataIndex: 'value', ellipsis: true, width: 90,},

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

                entity.type === 1 &&

                <Dropdown

                    key="3"

                    menu={{

                        items: [

                            {

                                key: '1',

                                label: <a onClick={() => {

                                    currentForm.current = {dictKey: entity.dictKey, type: 2, value: 1}

                                    CalcOrderNo(currentForm.current!, entity, ({item}) => {

                                        if (item!.value! >= currentForm.current!.value!) {

                                            currentForm.current!.value = Number(item!.value) + 1 // 如果存在字典项，那么则取最大的 value + 1

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
