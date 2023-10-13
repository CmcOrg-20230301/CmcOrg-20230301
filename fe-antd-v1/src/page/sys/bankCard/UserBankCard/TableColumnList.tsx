import {DoGetDictList, GetDictList, NoFormGetDictTreeList} from "@/util/DictUtil";
import {ActionType, ProColumns} from "@ant-design/pro-components";
import {SysUserWalletDO} from "@/api/http/SysUserWallet";
import {SysTenantDictList} from "@/api/http/SysTenant";
import {TreeSelect} from "antd";
import {SearchTransform} from "@/util/CommonUtil";
import {SysUserDictList} from "@/api/http/SysUser";
import {SysUserBankCardDictListOpenBankName, SysUserBankCardDO} from "@/api/http/SysUserBankCard";
import React from "react";

const TableColumnList = (currentForm: React.MutableRefObject<SysUserWalletDO>, actionRef: React.RefObject<ActionType | undefined>, setFormOpen: React.Dispatch<React.SetStateAction<boolean>>): ProColumns<SysUserWalletDO>[] => [

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

    {title: '卡号', dataIndex: 'bankCardNo', ellipsis: true, width: 90, order: 800,},

    {
        title: '开户行', dataIndex: 'openBankName', ellipsis: true, width: 90,
        fieldProps: {
            allowClear: true,
            showSearch: true,
        },
        valueType: 'select',
        request: () => {
            return DoGetDictList(SysUserBankCardDictListOpenBankName())
        },
    },

    {title: '支行', dataIndex: 'branchBankName', ellipsis: true, width: 90,},

    {title: '收款人姓名', dataIndex: 'payeeName', ellipsis: true, width: 90,},

    {
        title: '创建时间',
        dataIndex: 'createTime',
        hideInSearch: true,
        valueType: 'fromNow',
        width: 90,
        sorter: true,
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

        title: '操作',
        dataIndex: 'option',
        valueType: 'option',
        width: 90,

        render: (dom, entity, index) => {

            return [

                <a key="1" onClick={() => {

                    currentForm.current = {id: entity.id} as SysUserBankCardDO
                    setFormOpen(true)

                }}>查看</a>,

            ]

        },

    },

];

export default TableColumnList
