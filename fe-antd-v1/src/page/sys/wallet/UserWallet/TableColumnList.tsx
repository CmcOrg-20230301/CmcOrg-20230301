import {DoGetDictList, GetDictList, NoFormGetDictTreeList, YesNoDict} from "@/util/DictUtil";
import {ActionType, ProColumns} from "@ant-design/pro-components";
import {ExecConfirm, ToastSuccess} from "@/util/ToastUtil";
import {
    SysUserWalletDO,
    SysUserWalletFrozenByIdSet,
    SysUserWalletPageDTO,
    SysUserWalletThawByIdSet
} from "@/api/http/SysUserWallet";
import {SysTenantDictList} from "@/api/http/SysTenant";
import {TreeSelect} from "antd";
import {SearchTransform} from "@/util/CommonUtil";
import {SysUserDictList} from "@/api/http/SysUser";

const TableColumnList = (currentForm: React.MutableRefObject<SysUserWalletDO>, actionRef: React.RefObject<ActionType | undefined>): ProColumns<SysUserWalletDO>[] => [

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
        title: '用户', dataIndex: 'id', ellipsis: true, width: 90, valueType: 'select',
        request: () => {
            return DoGetDictList(SysUserDictList({addAdminFlag: true}))
        },
        fieldProps: {
            allowClear: true,
            showSearch: true,
        },
    },

    {
        title: '可提现',
        dataIndex: 'withdrawableMoney',
        valueType: 'money',
        ellipsis: true,
        width: 90,
        sorter: true,
        hideInSearch: true,
        fieldProps: {
            precision: 2, // 小数点精度
        },
    },

    {
        title: '可提现',
        dataIndex: 'withdrawableMoneyRange',
        hideInTable: true,
        valueType: 'digitRange',
        search: {

            transform: (value) => {

                return {

                    beginWithdrawableMoney: value[0],
                    endWithdrawableMoney: value[1],

                } as SysUserWalletPageDTO

            }

        }
    },

    {
        title: '更新时间',
        dataIndex: 'updateTime',
        hideInSearch: true,
        valueType: 'fromNow',
        width: 90,
        sorter: true,
        defaultSortOrder: 'descend',
    },

    {
        title: '更新时间',
        dataIndex: 'updateTimeRange',
        hideInTable: true,
        valueType: 'dateTimeRange',
        search: {

            transform: (value) => {

                return {

                    utBeginTime: value[0],
                    utEndTime: value[1],

                } as SysUserWalletPageDTO

            }

        }
    },

    {
        title: '是否可用',
        dataIndex: 'enableFlag',
        valueEnum: YesNoDict,
        width: 90,
    },

    {

        title: '操作',
        dataIndex: 'option',
        valueType: 'option',
        width: 90,

        render: (dom, entity, index) => {

            return [

                <a key="1" className={entity.enableFlag ? 'red3' : 'green2'} onClick={() => {

                    ExecConfirm(() => {

                        return entity.enableFlag ? SysUserWalletFrozenByIdSet({idSet: [entity.id!]}).then(res => {

                            ToastSuccess(res.msg)
                            actionRef.current?.reload()

                        }) : SysUserWalletThawByIdSet({idSet: [entity.id!]}).then(res => {

                            ToastSuccess(res.msg)
                            actionRef.current?.reload()

                        })

                    }, undefined, `确定${entity.enableFlag ? '冻结' : '解冻'}序号为【${index + 1}】的用户吗？`)

                }}>{entity.enableFlag ? '冻结' : '解冻'}</a>,

            ]

        },

    },

];

export default TableColumnList
