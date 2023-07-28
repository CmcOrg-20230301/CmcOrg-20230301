import {useRef, useState} from "react";
import {ActionType, ColumnsState, ProTable} from "@ant-design/pro-components";
import {Space} from "antd";
import {SysSocketRefUserDO, SysSocketRefUserPage, SysSocketRefUserPageDTO} from "@/api/http/SysSocketRefUser";
import TableColumnList from "./TableColumnList";
import CommonConstant from "@/model/constant/CommonConstant";
import {UseEffectFullScreenChange} from "@/page/sys/Menu/Menu";

// socket-用户管理
export default function () {

    const [columnsStateMap, setColumnsStateMap] = useState<Record<string, ColumnsState>>();

    const [expandedRowKeys, setExpandedRowKeys] = useState<string[]>([]);

    const actionRef = useRef<ActionType>()

    const [fullScreenFlag, setFullScreenFlag] = useState<boolean>(false)

    UseEffectFullScreenChange(setFullScreenFlag) // 监听是否：全屏

    return (

        <>

            <ProTable<SysSocketRefUserDO, SysSocketRefUserPageDTO>

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

                    return SysSocketRefUserPage({...params, sort})

                }}

                tableAlertOptionRender={({selectedRowKeys, selectedRows, onCleanSelected}) => (

                    <Space size={16}>

                        <a className={"red3"} onClick={() => {

                        }}>批量下线</a>

                        <a onClick={onCleanSelected}>取消选择</a>

                    </Space>

                )}

            >

            </ProTable>

        </>

    )

}
