import {YesNoDict} from "@/util/DictUtil";
import {ActionType} from "@ant-design/pro-components";
import {SysMenuDeleteByIdSet, SysMenuDO, SysMenuInsertOrUpdateDTO} from "@/api/SysMenu";
import {ExecConfirm, ToastSuccess} from "@/util/ToastUtil";
import React from "react";
import {ProSchema} from "@ant-design/pro-utils";

const TableColumnList = (currentForm: React.MutableRefObject<SysMenuInsertOrUpdateDTO | null>, setFormOpen: React.Dispatch<React.SetStateAction<boolean>>, actionRef: React.RefObject<ActionType | undefined>): ProSchema<SysMenuDO>[] => [

    {
        title: '序号',
        dataIndex: 'index',
        valueType: 'index',
        width: 110,
    },

    {title: '菜单名', dataIndex: 'name', ellipsis: true, width: 90,},

    {title: '路径', dataIndex: 'path', ellipsis: true, width: 90,},

    {title: '路由', dataIndex: 'router', ellipsis: true, width: 90,},

    {title: '权限', dataIndex: 'auths', ellipsis: true, width: 90,},

    {
        title: '权限菜单',
        dataIndex: 'authFlag',
        valueEnum: YesNoDict,
        width: 90,
    },

    {
        title: '是否显示',
        dataIndex: 'showFlag',
        valueEnum: YesNoDict,
        width: 90,
    },

    {
        title: '是否外链',
        dataIndex: 'linkFlag',
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

        ],

    },

];

export default TableColumnList
