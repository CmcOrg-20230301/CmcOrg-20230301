import {GetDictList, NoFormGetDictTreeList, YesNoDict} from "@/util/DictUtil";
import {ActionType, ProColumns} from "@ant-design/pro-components";
import {SysTenantDeleteByIdSet, SysTenantDictList, SysTenantDO, SysTenantInsertOrUpdateDTO} from "@/api/http/SysTenant";
import {ExecConfirm, ToastSuccess} from "@/util/ToastUtil";
import {CalcOrderNo} from "@/util/TreeUtil";
import {EllipsisOutlined} from "@ant-design/icons";
import {Dropdown, TreeSelect} from "antd";
import React from "react";
import {SearchTransform, SetTenantIdToStorage} from "@/util/CommonUtil";
import {SignOut} from "@/util/UserUtil";

const TableColumnList = (currentForm: React.MutableRefObject<SysTenantInsertOrUpdateDTO>, setFormOpen: React.Dispatch<React.SetStateAction<boolean>>, actionRef: React.RefObject<ActionType | undefined>): ProColumns<SysTenantDO>[] => [

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
        title: '租户id', dataIndex: 'id', ellipsis: true, width: 200,
        render: (dom, entity) =>

            <a title={"登录租户"} onClick={() => {

                SetTenantIdToStorage(entity.id);

                SignOut();

            }}>{entity.id}</a>,

    },

    {title: '租户名', dataIndex: 'name', ellipsis: true, width: 200,},

    {title: '排序号', dataIndex: 'orderNo', ellipsis: true, hideInSearch: true, width: 120,},

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

    {title: '备注', dataIndex: 'remark', ellipsis: true, width: 200,},

    {

        title: '操作',
        dataIndex: 'option',
        valueType: 'option',

        render: (dom, entity) => [

            <a key="1" onClick={() => {

                currentForm.current = {id: entity.id} as SysTenantInsertOrUpdateDTO
                setFormOpen(true)

            }}>编辑</a>,

            <a key="2" className={"red3"} onClick={() => {

                ExecConfirm(() => {

                    return SysTenantDeleteByIdSet({idSet: [entity.id!]}).then(res => {

                        ToastSuccess(res.msg)
                        actionRef.current?.reload()

                    })

                }, undefined, `确定删除【${entity.name}】吗？`)

            }}>删除</a>,

            <Dropdown

                key="3"

                menu={{

                    items: [

                        {
                            key: '1',
                            label: <a onClick={() => {

                                currentForm.current = {parentId: entity.id}

                                CalcOrderNo(currentForm.current, entity)

                                setFormOpen(true)

                            }}>
                                添加下级
                            </a>,
                        },

                    ]

                }}

            >

                <a><EllipsisOutlined/></a>

            </Dropdown>

        ],

    },

];

export default TableColumnList
