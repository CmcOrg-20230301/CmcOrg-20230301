import React, {useRef, useState} from "react";
import {ActionType, ColumnsState, ProTable} from "@ant-design/pro-components";
import {UseEffectFullScreenChange} from "@/util/UseEffectUtil.ts";
import CommonConstant from "@/model/constant/CommonConstant.ts";
import {Button, Space, Upload, UploadFile} from "antd";
import {ExecConfirm, ToastError, ToastSuccess} from "@/util/ToastUtil.ts";
import TableColumnList from "./TableColumnList";
import {
    SysActivitiDeployDeleteByProcessDefinitionIdSet,
    SysActivitiProcessDefinitionPage,
    SysActivitiProcessDefinitionPageDTO,
    SysActivitiProcessDefinitionVO
} from "@/api/http/SysActiviti.ts";
import {CheckBpmnFileType, CheckFileSize, FileUpload} from "@/util/FileUtil.ts";
import {PlusOutlined} from "@ant-design/icons";
import {OpenFlowDesign} from "@/page/sys/flow/FlowDesign/FlowDesignUtil.tsx";
import SchemaForm from "@/page/sys/flow/MyFlowList/SchemaForm.tsx";

export const AcceptFileTypeStr = "支持文件格式：bpmn20.xml/bpmn，单个文件最大支持 10Mb";

// 自动化记录
export default function () {

    const [columnsStateMap, setColumnsStateMap] = useState<Record<string, ColumnsState>>();

    const actionRef = useRef<ActionType>()

    const [fullScreenFlag, setFullScreenFlag] = useState<boolean>(false)

    const [fileList, setFileList] = useState<UploadFile[]>([]);

    const [fileLoading, setFileLoading] = useState<boolean>(false)

    UseEffectFullScreenChange(setFullScreenFlag) // 监听是否：全屏

    const [modalVisit, setModalVisit] = useState<boolean>(false);

    const [modalVisitId, setModalVisitId] = useState<string>('');

    return (

        <>

            <ProTable<SysActivitiProcessDefinitionVO, SysActivitiProcessDefinitionPageDTO>

                scroll={{x: 'max-content'}}
                sticky={{offsetHeader: fullScreenFlag ? 0 : CommonConstant.NAV_TOP_HEIGHT}}

                actionRef={actionRef}
                rowKey={"id"}
                columnEmptyText={false}

                columnsState={{
                    value: columnsStateMap,
                    onChange: setColumnsStateMap,
                }}

                rowSelection={{}}

                revalidateOnFocus={false}

                columns={TableColumnList(actionRef, setModalVisit, setModalVisitId)}

                options={{
                    fullScreen: true,
                }}

                request={(params, sort, filter) => {

                    return SysActivitiProcessDefinitionPage({...params, sort})

                }}

                toolbar={{

                    actions: [

                        <Upload.Dragger

                            key={"1"}

                            disabled={fileLoading}

                            accept={CommonConstant.BPMN_FILE_ACCEPT_TYPE}

                            fileList={fileList}

                            maxCount={1}

                            showUploadList={false}

                            beforeUpload={(file) => {

                                if (!CheckBpmnFileType(file.type)) {

                                    ToastError("暂不支持此文件类型：" + file.type + "，请重新选择")

                                    return false

                                }

                                if (!CheckFileSize(file.size!, 10485760)) {

                                    ToastError("文件大于 10MB，请重新选择")

                                    return false

                                }

                                return true

                            }}

                            customRequest={(options) => {

                                setFileLoading(true)

                                const formData = new FormData();

                                formData.append("file", options.file);

                                FileUpload(formData, '/sys/activiti/deploy/insertOrUpdate/byFile').then(res => {

                                    actionRef.current?.reload()
                                    ToastSuccess(res.msg)
                                    setFileLoading(false)

                                }).catch(() => {

                                    setFileLoading(false)

                                })

                            }}

                            onChange={(info) => {
                                setFileList(info.fileList)
                            }}

                        >

                            <p className="ant-upload-text">单击或拖动文件到此区域进行上传</p>

                            <p className="ant-upload-hint">
                                {AcceptFileTypeStr}
                            </p>

                        </Upload.Dragger>,

                        <Button key={"2"} icon={<PlusOutlined/>} type="primary" onClick={() => {

                            OpenFlowDesign()

                        }}>新建</Button>,

                    ],

                }}

                tableAlertOptionRender={({selectedRowKeys, selectedRows, onCleanSelected}) => (

                    <Space size={16}>

                        <a className={"red3"} onClick={() => {

                            ExecConfirm(async () => {

                                await SysActivitiDeployDeleteByProcessDefinitionIdSet({idSet: selectedRowKeys as string[]}).then(res => {

                                    ToastSuccess(res.msg)
                                    actionRef.current?.reload()
                                    onCleanSelected()

                                })

                            }, undefined, `确定删除选中的【${selectedRowKeys.length}】项吗？`)

                        }}>批量删除</a>

                        <a onClick={onCleanSelected}>取消选择</a>

                    </Space>

                )}

            />

            <SchemaForm modalVisit={modalVisit} processDefinitionId={modalVisitId}
                        setModalVisit={setModalVisit}></SchemaForm>

        </>

    )

}