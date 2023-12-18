import {DoGetDictTreeList, GetDictList, NoFormGetDictTreeList, YesNoDict} from "@/util/DictUtil";
import {ActionType, ModalForm, ProColumns, ProFormTreeSelect} from "@ant-design/pro-components";
import {
    NotNullIdAndNotEmptyLongSet,
    SysTenantDeleteByIdSet,
    SysTenantDeleteTenantAllMenu,
    SysTenantDictList,
    SysTenantDO,
    SysTenantDoSyncMenu,
    SysTenantGetSyncMenuInfo,
    SysTenantInsertOrUpdateDTO
} from "@/api/http/SysTenant";
import {ExecConfirm, ToastSuccess} from "@/util/ToastUtil";
import {CalcOrderNo} from "@/util/TreeUtil";
import {EllipsisOutlined} from "@ant-design/icons";
import {Dropdown, TreeSelect} from "antd";
import React from "react";
import {GetTenantIdFromStorage, SearchTransform, SetTenantIdToStorage} from "@/util/CommonUtil";
import {SignOut} from "@/util/UserUtil";
import CommonConstant from "@/model/constant/CommonConstant";
import {ItemType} from "antd/es/menu/hooks/useItems";

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
        title: '租户id', dataIndex: 'id', ellipsis: true, width: 150,
        render: (dom, entity) =>

            <a title={"登录租户"} onClick={() => {

                SetTenantIdToStorage(entity.id!);

                SignOut();

            }}>{entity.id}</a>,

    },

    {title: '租户名', dataIndex: 'name', ellipsis: true, width: 120,},

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

    {title: '用户数量', dataIndex: 'userCount', width: 90,},

    {title: '菜单数量', dataIndex: 'refMenuCount', width: 90,},

    {title: '字典数量', dataIndex: 'dictCount', width: 90,},

    {title: '参数数量', dataIndex: 'paramCount', width: 90,},

    {title: '备注', dataIndex: 'remark', ellipsis: true, width: 90,},

    {

        title: '操作',
        dataIndex: 'option',
        valueType: 'option',
        width: 120,

        render: (dom, entity) => {

            const dropdownMenuItemArr: ItemType[] = [

                {
                    key: '1',
                    label: <a onClick={() => {

                        currentForm.current = {parentId: entity.id}

                        CalcOrderNo(currentForm.current, entity)

                        setFormOpen(true)

                    }}>
                        添加下级
                    </a>
                }

            ]

            if (GetTenantIdFromStorage() === CommonConstant.TOP_TENANT_ID_STR) {

                dropdownMenuItemArr.push(
                    {
                        key: '2',
                        label: <SysTenantSyncMenuModalForm id={entity.id!} actionRef={actionRef}
                                                           titlePre={entity.name!}/>
                    }
                )

            }

            dropdownMenuItemArr.push(
                {
                    key: '3',
                    danger: true,
                    label: <a onClick={() => {

                        ExecConfirm(() => {

                            return SysTenantDeleteTenantAllMenu({idSet: [entity.id!]}).then(res => {

                                ToastSuccess(res.msg)
                                actionRef.current?.reload()

                            })

                        }, undefined, '确定删除该租户所有菜单吗？')

                    }}>
                        删除所有菜单
                    </a>
                }
            )

            return [

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

                        items: dropdownMenuItemArr

                    }}

                >

                    <a><EllipsisOutlined/></a>

                </Dropdown>

            ]

        },

    },

];

export default TableColumnList

const SysTenantSyncMenuTitle = "新增菜单"

interface ISysTenantSyncMenuModalForm {

    id: string

    titlePre: string

    actionRef: React.RefObject<ActionType | undefined>

}

export function SysTenantSyncMenuModalForm(props: ISysTenantSyncMenuModalForm) {

    return <ModalForm<NotNullIdAndNotEmptyLongSet>

        modalProps={{
            maskClosable: false,
            destroyOnClose: true
        }}

        isKeyPressSubmit

        width={CommonConstant.MODAL_FORM_WIDTH}

        title={props.titlePre + " - " + SysTenantSyncMenuTitle}

        trigger={<a>{SysTenantSyncMenuTitle}</a>}

        onFinish={async (form) => {

            await SysTenantDoSyncMenu({

                ...form,
                id: props.id

            }).then(res => {

                ToastSuccess(res.msg)
                props.actionRef.current?.reload()

            })

            return true

        }}
    >

        <ProFormTreeSelect

            fieldProps={
                {
                    treeNodeFilterProp: 'title',
                    maxTagCount: 'responsive',
                    treeCheckable: true,
                    showCheckedStrategy: TreeSelect.SHOW_ALL,
                }
            }

            allowClear

            name="valueSet"
            label="新增菜单"

            request={() => DoGetDictTreeList(SysTenantGetSyncMenuInfo({id: props.id}))}

            placeholder="选择需要新增的菜单"

            rules={[{required: true, message: '请选择需要新增的菜单'}]}

        />

    </ModalForm>

}
