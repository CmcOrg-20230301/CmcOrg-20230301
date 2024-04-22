import {useRef, useState} from "react";
import {ActionType, ColumnsState, ProTable} from "@ant-design/pro-components";
import {UseEffectFullScreenChange} from "@/util/UseEffectUtil.ts";
import {
    SysActivitiHistoryProcessInstanceDeleteByIdSet,
    SysActivitiHistoryProcessInstancePage,
    SysActivitiHistoryProcessInstancePageDTO,
    SysActivitiHistoryProcessInstanceVO,
    SysActivitiProcessInstanceDeleteByIdSet
} from "@/api/http/SysActiviti.ts";
import CommonConstant from "@/model/constant/CommonConstant.ts";
import TableColumnList from "./TableColumnList";
import {ExecConfirm, ToastSuccess} from "@/util/ToastUtil.ts";
import {Space} from "antd";

// 自动化记录
export default function () {

    const [columnsStateMap, setColumnsStateMap] = useState<Record<string, ColumnsState>>();

    const actionRef = useRef<ActionType>()

    const [fullScreenFlag, setFullScreenFlag] = useState<boolean>(false)

    UseEffectFullScreenChange(setFullScreenFlag) // 监听是否：全屏

    return (

        <>

            <ProTable<SysActivitiHistoryProcessInstanceVO, SysActivitiHistoryProcessInstancePageDTO>

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

                columns={TableColumnList(actionRef)}

                options={{
                    fullScreen: true,
                }}

                request={(params, sort, filter) => {

                    return SysActivitiHistoryProcessInstancePage({...params, sort})

                }}

                tableAlertOptionRender={({selectedRowKeys, selectedRows, onCleanSelected}) => (

                    <Space size={16}>

                        <a className={"red3"} onClick={() => {

                            ExecConfirm(async () => {

                                await SysActivitiHistoryProcessInstanceDeleteByIdSet({idSet: selectedRowKeys as string[]}).then(res => {

                                    ToastSuccess(res.msg)
                                    actionRef.current?.reload()
                                    onCleanSelected()

                                })

                            }, undefined, `确定删除选中的【${selectedRowKeys.length}】项吗？`)

                        }}>批量删除</a>

                        <a className={"red3"} onClick={() => {

                            ExecConfirm(async () => {

                                await SysActivitiProcessInstanceDeleteByIdSet({idSet: selectedRowKeys as string[]}).then(res => {

                                    ToastSuccess(res.msg)
                                    actionRef.current?.reload()
                                    onCleanSelected()

                                })

                            }, undefined, `确定结束选中的【${selectedRowKeys.length}】项吗？`)

                        }}>批量结束</a>

                        <a onClick={onCleanSelected}>取消选择</a>

                    </Space>

                )}

            />

        </>

    )

}