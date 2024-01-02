import {GetDictList, GetDictListByKey, NoFormGetDictTreeList, YesNoDict} from "@/util/DictUtil";
import {ActionType, ProColumns} from "@ant-design/pro-components";
import {SysOtherAppDeleteByIdSet, SysOtherAppDO, SysOtherAppInsertOrUpdateDTO} from "@/api/http/SysOtherApp";
import {ExecConfirm, ToastSuccess} from "@/util/ToastUtil";
import {SysTenantDictList} from "@/api/http/SysTenant";
import {Dropdown, QRCode, TreeSelect, Typography} from "antd";
import {SearchTransform} from "@/util/CommonUtil";
import CommonConstant from "@/model/constant/CommonConstant";
import {EllipsisOutlined} from "@ant-design/icons";
import React from "react";
import PathConstant from "@/model/constant/PathConstant";
import {GoPage} from "@/layout/AdminLayout/AdminLayout";

const TableColumnList = (currentForm: React.MutableRefObject<SysOtherAppInsertOrUpdateDTO>, setFormOpen: React.Dispatch<React.SetStateAction<boolean>>, actionRef: React.RefObject<ActionType | undefined>): ProColumns<SysOtherAppDO>[] => [

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
            return GetDictListByKey('sys_other_app_type')
        },
        fieldProps: {
            allowClear: true,
            showSearch: true,
        },
    },

    {title: '名称', dataIndex: 'name', ellipsis: true, width: 150,},

    {title: 'appId', dataIndex: 'appId', ellipsis: true, width: 150,},

    {
        title: '创建时间',
        dataIndex: 'createTime',
        hideInSearch: true,
        sorter: true,
        valueType: 'fromNow',
        width: 90,
    },

    {
        title: '修改时间',
        dataIndex: 'updateTime',
        hideInSearch: true,
        sorter: true,
        valueType: 'fromNow',
        width: 90,
    },

    {
        title: '是否启用',
        dataIndex: 'enableFlag',
        valueEnum: YesNoDict,
        width: 90,
    },

    {
        title: '关注回复', dataIndex: 'subscribeReplyContent', ellipsis: true, width: 200, render: (dom, entity) => {

            const subText = entity.subscribeReplyContent!.substring(0, CommonConstant.TOOLTIP_STR_LENGTH)

            return <Typography.Text ellipsis={{tooltip: true}} style={{width: 200}}>{subText}</Typography.Text>

        }
    },

    {
        title: '二维码', dataIndex: 'qrCode', width: CommonConstant.TABLE_QR_CODE_WIDTH, render: (dom, entity) => {

            return entity.qrCode &&
                <QRCode value={entity.qrCode} size={CommonConstant.TABLE_QR_CODE_WIDTH} bordered={false}/>

        }
    },

    {title: '备注', dataIndex: 'remark', ellipsis: true, width: 120,},

    {

        title: '操作',
        dataIndex: 'option',
        valueType: 'option',
        width: 120,

        render: (dom, entity) => [

            <a key="1" onClick={() => {

                currentForm.current = {id: entity.id} as SysOtherAppInsertOrUpdateDTO
                setFormOpen(true)

            }}>编辑</a>,

            <a key="2" className={"red3"} onClick={() => {

                ExecConfirm(async () => {

                    await SysOtherAppDeleteByIdSet({idSet: [entity.id!]}).then(res => {

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

                                GoPage(PathConstant.SYS_OTHER_APP_OFFICIAL_ACCOUNT_MENU_PATH, {
                                    state: {
                                        otherAppId: entity.id
                                    }
                                })

                            }}>
                                公众号菜单配置
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
