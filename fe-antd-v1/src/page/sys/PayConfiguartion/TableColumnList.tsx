import {GetDictList, GetDictListByKey, NoFormGetDictTreeList, YesNoDict} from "@/util/DictUtil";
import {ActionType, ProColumns} from "@ant-design/pro-components";
import {
    SysPayConfigurationDeleteByIdSet,
    SysPayConfigurationDO,
    SysPayConfigurationInsertOrUpdateDTO
} from "@/api/http/SysPayConfiguration";
import {ExecConfirm, ToastSuccess} from "@/util/ToastUtil";
import {SysTenantDictList} from "@/api/http/SysTenant";
import {TreeSelect} from "antd";
import {SearchTransform} from "@/util/CommonUtil";

const TableColumnList = (currentForm: React.MutableRefObject<SysPayConfigurationInsertOrUpdateDTO>, setFormOpen: React.Dispatch<React.SetStateAction<boolean>>, actionRef: React.RefObject<ActionType | undefined>): ProColumns<SysPayConfigurationDO>[] => [

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

    {
        title: '类型', dataIndex: 'type', ellipsis: true, width: 90, valueType: 'select',
        request: () => {
            return GetDictListByKey('sys_pay_type')
        },
        fieldProps: {
            allowClear: true,
            showSearch: true,
        },
    },

    {title: '名称', dataIndex: 'name', ellipsis: true, width: 90,},

    {title: 'appId', dataIndex: 'appId', ellipsis: true, width: 90,},

    {
        title: '默认支付',
        dataIndex: 'defaultFlag',
        valueEnum: YesNoDict,
        width: 90,
    },

    {
        title: '是否启用',
        dataIndex: 'enableFlag',
        valueEnum: YesNoDict,
        width: 90,
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

    {title: '备注', dataIndex: 'remark', ellipsis: true, width: 200,},

    {

        title: '操作',
        dataIndex: 'option',
        valueType: 'option',

        render: (dom, entity) => [

            <a key="1" onClick={() => {

                currentForm.current = {id: entity.id} as SysPayConfigurationInsertOrUpdateDTO
                setFormOpen(true)

            }}>编辑</a>,

            <a key="2" className={"red3"} onClick={() => {

                ExecConfirm(() => {

                    return SysPayConfigurationDeleteByIdSet({idSet: [entity.id!]}).then(res => {

                        ToastSuccess(res.msg)
                        actionRef.current?.reload()

                    })

                }, undefined, `确定删除【${entity.name}】吗？`)

            }}>删除</a>,

        ],

    },

];

export default TableColumnList
