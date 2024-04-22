import {ActionType, ProColumns} from "@ant-design/pro-components";
import {ExecConfirm, ToastSuccess} from "@/util/ToastUtil";
import React from "react";
import {
    SysActivitiDeployDeleteByProcessDefinitionIdSet,
    SysActivitiProcessDefinitionVO
} from "@/api/http/SysActiviti.ts";
import {OpenFlowDesign} from "@/page/sys/flow/FlowDesign/FlowDesignUtil.tsx";

const TableColumnList = (actionRef: React.RefObject<ActionType | undefined>, setModalVisit: (value: (((prevState: boolean) => boolean) | boolean)) => void, setModalVisitId: (value: (((prevState: string) => string) | string)) => void): ProColumns<SysActivitiProcessDefinitionVO>[] => [

    {
        title: '序号',
        dataIndex: 'index',
        valueType: 'index',
        width: 90,
    },

    {title: 'id', dataIndex: 'id', ellipsis: true, width: 200,},

    {title: '名称', dataIndex: 'name', ellipsis: true, width: 200,},

    {title: '文件名', dataIndex: 'resourceName', ellipsis: true, width: 200,},

    {

        title: '操作',
        dataIndex: 'option',
        valueType: 'option',
        width: 120,

        render: (dom, entity) => [

            <a key="1" className={"green2"} onClick={() => {

                setModalVisitId(entity.id!)

                setModalVisit(true)

            }}>执行</a>,

            <a key="2" onClick={() => {

                OpenFlowDesign(entity.deploymentId, undefined, entity.id)

            }}>设计</a>,

            <a key="3" onClick={() => {


            }}>记录</a>,

            <a key="4" className={"red3"} onClick={() => {

                ExecConfirm(async () => {

                    await SysActivitiDeployDeleteByProcessDefinitionIdSet({idSet: [entity.id!]}).then(res => {

                        ToastSuccess(res.msg)
                        actionRef.current?.reload()

                    })

                }, undefined, `确定删除【${entity.name || entity.resourceName}】吗？`)

            }}>删除</a>

        ],

    },

];

export default TableColumnList
