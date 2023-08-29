import {GetDictList, YesNoDict} from "@/util/DictUtil";
import {ActionType, ProColumns} from "@ant-design/pro-components";
import {SysRoleDeleteByIdSet, SysRoleDO, SysRoleInsertOrUpdateDTO} from "@/api/http/SysRole";
import {ExecConfirm, ToastSuccess} from "@/util/ToastUtil";
import {SysTenantDictList} from "@/api/http/SysTenant";

const TableColumnList = (currentForm: React.MutableRefObject<SysRoleInsertOrUpdateDTO>, setFormOpen: React.Dispatch<React.SetStateAction<boolean>>, actionRef: React.RefObject<ActionType | undefined>): ProColumns<SysRoleDO>[] => [

    {
        title: '序号',
        dataIndex: 'index',
        valueType: 'index',
        width: 90,
    },

    {
        title: '租户', dataIndex: 'tenantId', ellipsis: true, width: 90, valueType: 'select',
        request: () => {
            return GetDictList(SysTenantDictList)
        }
    },

    {title: '角色名', dataIndex: 'name', ellipsis: true,},

    {
        title: '默认角色',
        dataIndex: 'defaultFlag',
        valueEnum: YesNoDict
    },

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

                currentForm.current = {id: entity.id} as SysRoleInsertOrUpdateDTO
                setFormOpen(true)

            }}>编辑</a>,

            <a key="2" className={"red3"} onClick={() => {

                ExecConfirm(() => {

                    return SysRoleDeleteByIdSet({idSet: [entity.id!]}).then(res => {

                        ToastSuccess(res.msg)
                        actionRef.current?.reload()

                    })

                }, undefined, `确定删除【${entity.name}】吗？`)

            }}>删除</a>,

        ],

    },

];

export default TableColumnList
