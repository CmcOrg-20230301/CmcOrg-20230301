import {YesNoDict} from "@/util/DictUtil";
import {ActionType} from "@ant-design/pro-components";
import {SysDictDeleteByIdSet, SysDictDO, SysDictInsertOrUpdateDTO} from "@/api/SysDict";
import {ExecConfirm, ToastSuccess} from "@/util/ToastUtil";
import {ProSchema} from "@ant-design/pro-utils";

const TableColumnList = (currentForm: React.MutableRefObject<SysDictInsertOrUpdateDTO | null>, setFormOpen: React.Dispatch<React.SetStateAction<boolean>>, actionRef: React.RefObject<ActionType | undefined>): ProSchema<SysDictDO>[] => [

    {
        title: '序号',
        dataIndex: 'index',
        valueType: 'index',
        width: 90,
    },


    {title: '排序号', dataIndex: 'orderNo', ellipsis: true, width: 90,},

    {
        title: '修改时间',
        dataIndex: 'updateTime',
        hideInSearch: true,
        valueType: 'fromNow',
    },

    {title: '备注', dataIndex: 'remark', ellipsis: true, width: 90,},

    {
        title: '是否逻辑删除',
        dataIndex: 'delFlag',
        valueEnum: YesNoDict
    },

    {title: '字典类型：1 字典 2 字典项', dataIndex: 'type', ellipsis: true, width: 90,},

    {title: '乐观锁', dataIndex: 'version', ellipsis: true, width: 90,},

    {title: '字典 key', dataIndex: 'dictKey', ellipsis: true, width: 90,},

    {title: '修改人id', dataIndex: 'updateId', ellipsis: true, width: 90,},

    {
        title: '创建时间',
        dataIndex: 'createTime',
        hideInSearch: true,
        valueType: 'fromNow',
    },

    {title: '创建人id', dataIndex: 'createId', ellipsis: true, width: 90,},

    {title: '字典/字典项 名', dataIndex: 'name', ellipsis: true, width: 90,},

    {title: '主键id', dataIndex: 'id', ellipsis: true, width: 90,},

    {
        title: '是否启用',
        dataIndex: 'enableFlag',
        valueEnum: YesNoDict
    },

    {title: '字典项 value', dataIndex: 'value', ellipsis: true, width: 90,},


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

        ],

    },

];

export default TableColumnList
