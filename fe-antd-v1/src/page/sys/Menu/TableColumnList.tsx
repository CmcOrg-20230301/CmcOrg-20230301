import {YesNoDict} from "@/util/DictUtil";
import {ActionType, ModalForm, ProColumns, ProFormText} from "@ant-design/pro-components";
import {SysMenuDeleteByIdSet, SysMenuDO, SysMenuInsertOrUpdate, SysMenuInsertOrUpdateDTO} from "@/api/http/SysMenu";
import {ExecConfirm, ToastSuccess} from "@/util/ToastUtil";
import React from "react";
import {CalcOrderNo, DefaultOrderNo} from "@/util/TreeUtil";
import CommonConstant from "@/model/constant/CommonConstant";
import {Dropdown} from "antd";
import {EllipsisOutlined} from "@ant-design/icons/lib";
import {RouterMapKeyList} from "@/router/RouterMap";

const QuicklyAddAuth = "快速添加权限"

const TableColumnList = (currentForm: React.MutableRefObject<SysMenuInsertOrUpdateDTO>, setFormOpen: React.Dispatch<React.SetStateAction<boolean>>, actionRef: React.RefObject<ActionType | undefined>): ProColumns<SysMenuDO>[] => [

    {
        title: '序号',
        dataIndex: 'index',
        valueType: 'index',
        width: 110,
    },

    {title: '菜单名', dataIndex: 'name', ellipsis: true, width: 90,},

    {title: '路径', dataIndex: 'path', ellipsis: true, width: 90,},

    {
        title: '路由',
        dataIndex: 'router',
        valueType: 'select',
        fieldProps: {
            options: RouterMapKeyList,
        },
    },

    {title: '权限', dataIndex: 'auths', ellipsis: true, width: 90,},

    {
        title: '权限菜单',
        dataIndex: 'authFlag',
        valueEnum: YesNoDict,
        width: 90,
    },

    {
        title: '是否外链',
        dataIndex: 'linkFlag',
        valueEnum: YesNoDict,
        width: 90,
    },

    {
        title: '是否显示',
        dataIndex: 'showFlag',
        valueEnum: YesNoDict,
        width: 90,
    },

    {title: '重定向', dataIndex: 'redirect', ellipsis: true, width: 90,},

    {
        title: '起始页面',
        dataIndex: 'firstFlag',
        valueEnum: YesNoDict,
        width: 90,
    },

    {title: '排序号', dataIndex: 'orderNo', ellipsis: true, hideInSearch: true, width: 90,},

    {
        title: '是否启用',
        dataIndex: 'enableFlag',
        valueEnum: YesNoDict,
        width: 90,
    },

    {

        title: '操作',
        dataIndex: 'option',
        valueType: 'option',

        render: (dom, entity) => [

            <a key="1" onClick={() => {

                currentForm.current = {id: entity.id} as SysMenuInsertOrUpdateDTO
                setFormOpen(true)

            }}>编辑</a>,

            <a key="2" className={"red3"} onClick={() => {

                ExecConfirm(() => {

                    return SysMenuDeleteByIdSet({idSet: [entity.id!]}).then(res => {

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

                        {
                            key: '2',
                            label:

                                <ModalForm<SysMenuInsertOrUpdateDTO>

                                    modalProps={{
                                        maskClosable: false
                                    }}

                                    isKeyPressSubmit

                                    width={CommonConstant.MODAL_FORM_WIDTH}

                                    title={QuicklyAddAuth}

                                    trigger={<a>{QuicklyAddAuth}</a>}

                                    onFinish={

                                        async (form) => {

                                            const formTemp: SysMenuInsertOrUpdateDTO = {

                                                parentId: entity.id,

                                                authFlag: true,

                                                enableFlag: true

                                            }

                                            await SysMenuInsertOrUpdate({

                                                ...formTemp,

                                                name: '新增修改',

                                                auths: form.auths + ":insertOrUpdate",

                                                orderNo: DefaultOrderNo

                                            }).then(() => {

                                                SysMenuInsertOrUpdate({

                                                    ...formTemp,

                                                    name: '列表查询',

                                                    auths: form.auths + ":page",

                                                    orderNo: DefaultOrderNo - 100

                                                }).then(() => {

                                                    SysMenuInsertOrUpdate({

                                                        ...formTemp,

                                                        name: '删除',

                                                        auths: form.auths + ":deleteByIdSet",

                                                        orderNo: DefaultOrderNo - 200

                                                    }).then(() => {

                                                        SysMenuInsertOrUpdate({

                                                            ...formTemp,

                                                            name: '查看详情',

                                                            auths: form.auths + ":infoById",

                                                            orderNo: DefaultOrderNo - 300

                                                        }).then(res => {

                                                            ToastSuccess(res.msg)

                                                            actionRef.current?.reload()

                                                        })
                                                    })
                                                })
                                            })

                                            return true

                                        }}

                                >

                                    <ProFormText name={"auths"} label={"权限前缀"} rules={[{required: true}]}/>

                                </ModalForm>,

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
