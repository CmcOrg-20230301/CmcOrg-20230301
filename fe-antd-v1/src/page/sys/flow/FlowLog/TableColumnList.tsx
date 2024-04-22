import {ActionType, ProColumns} from "@ant-design/pro-components";
import React from "react";
import {
    SysActivitiHistoryProcessInstanceDeleteByIdSet,
    SysActivitiHistoryProcessInstanceVO,
    SysActivitiProcessInstanceActiveByIdSet,
    SysActivitiProcessInstanceDeleteByIdSet,
    SysActivitiProcessInstanceSuspendByIdSet
} from "@/api/http/SysActiviti.ts";
import {YesNoDict} from "@/util/DictUtil.ts";
import {ExecConfirm, ToastSuccess} from "@/util/ToastUtil.ts";
import {OpenFlowDesign} from "@/page/sys/flow/FlowDesign/FlowDesignUtil.tsx";

const TableColumnList = (actionRef: React.RefObject<ActionType | undefined>): ProColumns<SysActivitiHistoryProcessInstanceVO>[] => [

    {
        title: '序号',
        dataIndex: 'index',
        valueType: 'index',
        width: 90,
    },

    {title: '自动化id', dataIndex: 'processDefinitionId', ellipsis: true, width: 200,},

    {title: '自动化名称', dataIndex: 'processDefinitionName', ellipsis: true, width: 200,},

    {title: '记录id', dataIndex: 'id', ellipsis: true, width: 200,},

    {
        title: '创建时间',
        dataIndex: 'startTime',
        hideInSearch: true,
        valueType: 'fromNow',
        width: 120,
    },

    {
        title: '结束时间',
        dataIndex: 'endTime',
        hideInSearch: true,
        valueType: 'fromNow',
        width: 120,
        render: (dom, entity) => {
            return entity.endTime ? dom : ''
        },
    },

    {
        title: '运行状态',
        dataIndex: 'state',
        hideInSearch: true,
        width: 90,
        render: (dom, entity) => {

            if (entity.endTime) {

                return "已结束"

            }

            if (entity.suspended) {

                return "暂停中"

            } else {

                return "运行中"

            }

        },
    },

    {
        title: '是否结束',
        dataIndex: 'ended',
        valueEnum: YesNoDict,
        hideInTable: true,
        width: 90,
        renderText: (text, record) => {
            return !!record.endTime
        },
    },

    {

        title: '操作',
        dataIndex: 'option',
        valueType: 'option',
        width: 150,

        render: (dom, entity) => {

            const res: React.ReactNode[] = []

            res.push(<a key="1" onClick={() => {

                OpenFlowDesign(entity.deploymentId, entity.id, entity.processDefinitionId)

            }}>查看</a>)

            if (entity.endTime) {

                res.push(<a key="5" className={"red3"} onClick={() => {

                    ExecConfirm(async () => {

                        await SysActivitiHistoryProcessInstanceDeleteByIdSet({idSet: [entity.id!]}).then(res => {

                            ToastSuccess(res.msg)
                            actionRef.current?.reload()

                        })

                    }, undefined, `确定删除【${entity.id}】吗？`)

                }}>删除</a>)

            } else {

                if (entity.suspended) {

                    res.push(<a key="3" className={"green2"} onClick={() => {

                        SysActivitiProcessInstanceActiveByIdSet({idSet: [entity.id!]}).then(res => {

                            ToastSuccess(res.msg)
                            actionRef.current?.reload()

                        })

                    }}>继续</a>)

                } else {

                    res.push(<a key="2" onClick={() => {

                        SysActivitiProcessInstanceSuspendByIdSet({idSet: [entity.id!]}).then(res => {

                            ToastSuccess(res.msg)
                            actionRef.current?.reload()

                        })

                    }}>暂停</a>)

                }

                res.push(<a key="4" className={"red3"} onClick={() => {

                    ExecConfirm(async () => {

                        await SysActivitiProcessInstanceDeleteByIdSet({idSet: [entity.id!]}).then(res => {

                            ToastSuccess(res.msg)
                            actionRef.current?.reload()

                        })

                    }, undefined, `确定结束【${entity.id}】吗？`)

                }}>结束</a>)

            }

            return res

        }

    },

];

export default TableColumnList
