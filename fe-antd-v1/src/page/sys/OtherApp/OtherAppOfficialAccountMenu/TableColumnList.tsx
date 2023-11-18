import {GetDictList, NoFormGetDictTreeList, YesNoDict} from "@/util/DictUtil";
import {ActionType, ProColumns} from "@ant-design/pro-components";

import {ExecConfirm, ToastSuccess} from "@/util/ToastUtil";
import {CalcOrderNo} from "@/util/TreeUtil";
import {EllipsisOutlined} from "@ant-design/icons";
import {Dropdown, TreeSelect, Typography} from "antd";
import React from "react";
import {SysTenantDictList} from "@/api/http/SysTenant";
import {SearchTransform} from "@/util/CommonUtil";
import {
    SysOtherAppOfficialAccountMenuDeleteByIdSet,
    SysOtherAppOfficialAccountMenuDO,
    SysOtherAppOfficialAccountMenuInsertOrUpdateDTO
} from "@/api/http/SysOtherApp";
import CommonConstant from "@/model/constant/CommonConstant";
import {SysOtherAppOfficialAccountMenuButtonTypeEnumDict} from "@/model/enum/SysOtherAppOfficialAccountMenuButtonTypeEnum";

const TableColumnList = (currentForm: React.MutableRefObject<SysOtherAppOfficialAccountMenuInsertOrUpdateDTO>, setFormOpen: React.Dispatch<React.SetStateAction<boolean>>, actionRef: React.RefObject<ActionType | undefined>): ProColumns<SysOtherAppOfficialAccountMenuDO>[] => [

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

    {title: '菜单名', dataIndex: 'name', ellipsis: true, width: 90,},

    {
        title: '菜单类型',
        dataIndex: 'buttonType',
        valueEnum: SysOtherAppOfficialAccountMenuButtonTypeEnumDict,
        width: 90,
    },

    {
        title: '值', dataIndex: 'value', ellipsis: true, width: 200, render: (dom, entity) => {

            const subText = entity.value!.substring(0, CommonConstant.TOOLTIP_STR_LENGTH)

            return <Typography.Text ellipsis={{tooltip: true}} style={{width: 200}}>{subText}</Typography.Text>

        }
    },

    {
        title: '回复内容', dataIndex: 'replyContent', ellipsis: true, width: 200, render: (dom, entity) => {

            const subText = entity.replyContent!.substring(0, CommonConstant.TOOLTIP_STR_LENGTH)

            return <Typography.Text ellipsis={{tooltip: true}} style={{width: 200}}>{subText}</Typography.Text>

        }
    },

    {title: '排序号', dataIndex: 'orderNo', ellipsis: true, hideInSearch: true, width: 120,},

    {
        title: '创建时间',
        dataIndex: 'createTime',
        hideInSearch: true,
        valueType: 'fromNow',
        width: 90,
    },

    {
        title: '修改时间',
        dataIndex: 'updateTime',
        hideInSearch: true,
        valueType: 'fromNow',
        width: 90,
    },

    {
        title: '是否启用',
        dataIndex: 'enableFlag',
        valueEnum: YesNoDict,
        width: 90,
    },

    {title: '备注', dataIndex: 'remark', ellipsis: true, width: 200,},

    {

        title: '操作',
        dataIndex: 'option',
        valueType: 'option',
        width: 120,

        render: (dom, entity) => [

            <a key="1" onClick={() => {

                currentForm.current = {id: entity.id} as SysOtherAppOfficialAccountMenuInsertOrUpdateDTO
                setFormOpen(true)

            }}>编辑</a>,

            <a key="2" className={"red3"} onClick={() => {

                ExecConfirm(() => {

                    return SysOtherAppOfficialAccountMenuDeleteByIdSet({idSet: [entity.id!]}).then(res => {

                        ToastSuccess(res.msg)
                        actionRef.current?.reload()

                    })

                }, undefined, `确定删除【${entity.name}】吗？`)

            }}>删除</a>,

            entity.parentId === CommonConstant.TOP_PARENT_ID_STR && <Dropdown

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
