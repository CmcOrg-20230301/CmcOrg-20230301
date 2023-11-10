import {useRef, useState} from "react";
import {ActionType, ColumnsState, ProTable} from "@ant-design/pro-components";
import {Space} from "antd";
import {
    SysSocketDisableByIdSet,
    SysSocketDO,
    SysSocketEnableByIdSet,
    SysSocketPage,
    SysSocketPageDTO
} from "@/api/http/SysSocket";
import TableColumnList from "./TableColumnList";
import CommonConstant from "@/model/constant/CommonConstant";
import {UseEffectFullScreenChange} from "@/util/UseEffectUtil";
import {ExecConfirm, ToastSuccess} from "@/util/ToastUtil";

// socket-管理
export default function () {

    const [columnsStateMap, setColumnsStateMap] = useState<Record<string, ColumnsState>>();

    const [expandedRowKeys, setExpandedRowKeys] = useState<string[]>([]);

    const actionRef = useRef<ActionType>()

    const [fullScreenFlag, setFullScreenFlag] = useState<boolean>(false)

    UseEffectFullScreenChange(setFullScreenFlag) // 监听是否：全屏

    return (

        <>

            <ProTable<SysSocketDO, SysSocketPageDTO>

                scroll={{x: 'max-content'}}
                sticky={{offsetHeader: fullScreenFlag ? 0 : CommonConstant.NAV_TOP_HEIGHT}}
                actionRef={actionRef}
                rowKey={"id"}

                pagination={{
                    showQuickJumper: true,
                    showSizeChanger: true,
                }}

                columnEmptyText={false}

                columnsState={{
                    value: columnsStateMap,
                    onChange: setColumnsStateMap,
                }}

                rowSelection={{}}

                expandable={{

                    expandedRowKeys,

                    onExpandedRowsChange: (expandedRows) => {

                        setExpandedRowKeys(expandedRows as string[])

                    },

                }}

                revalidateOnFocus={false}

                columns={TableColumnList(actionRef)}

                options={{
                    fullScreen: true,
                }}

                request={(params, sort, filter) => {

                    return SysSocketPage({...params, sort})

                }}

                tableAlertOptionRender={({selectedRowKeys, selectedRows, onCleanSelected}) => (

                    <Space size={16}>

                        <a className={"red3"} onClick={() => {

                            ExecConfirm(() => {

                                return SysSocketDisableByIdSet({idSet: selectedRowKeys as string[]}).then(res => {

                                    ToastSuccess(res.msg)
                                    actionRef.current?.reload()
                                    onCleanSelected()

                                })

                            }, undefined, `确定禁用选中的【${selectedRowKeys.length}】项吗？`)

                        }}>批量禁用</a>

                        <a className={"green2"} onClick={() => {

                            ExecConfirm(() => {

                                return SysSocketEnableByIdSet({idSet: selectedRowKeys as string[]}).then(res => {

                                    ToastSuccess(res.msg)
                                    actionRef.current?.reload()
                                    onCleanSelected()

                                })

                            }, undefined, `确定启用选中的【${selectedRowKeys.length}】项吗？`)

                        }}>批量启用</a>

                        <a onClick={onCleanSelected}>取消选择</a>

                    </Space>

                )}

            />

        </>

    )

}
