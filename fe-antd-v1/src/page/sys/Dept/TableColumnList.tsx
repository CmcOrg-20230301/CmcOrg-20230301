import {GetDictList, YesNoDict} from "@/util/DictUtil";
import {ActionType, ProColumns} from "@ant-design/pro-components";
import {SysDeptDeleteByIdSet, SysDeptDO, SysDeptInsertOrUpdateDTO} from "@/api/http/SysDept";
import {ExecConfirm, ToastSuccess} from "@/util/ToastUtil";
import {Dropdown} from "antd";
import {CalcOrderNo} from "@/util/TreeUtil";
import {EllipsisOutlined} from "@ant-design/icons";
import React from "react";
import {SysTenantDictList} from "@/api/http/SysTenant";

const TableColumnList = (currentForm: React.MutableRefObject<SysDeptInsertOrUpdateDTO>, setFormOpen: React.Dispatch<React.SetStateAction<boolean>>, actionRef: React.RefObject<ActionType | undefined>): ProColumns<SysDeptDO>[] => [

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

    {title: '部门名', dataIndex: 'name', ellipsis: true, width: 200,},

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

                currentForm.current = {id: entity.id} as SysDeptInsertOrUpdateDTO
                setFormOpen(true)

            }}>编辑</a>,

            <a key="2" className={"red3"} onClick={() => {

                ExecConfirm(() => {

                    return SysDeptDeleteByIdSet({idSet: [entity.id!]}).then(res => {

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
