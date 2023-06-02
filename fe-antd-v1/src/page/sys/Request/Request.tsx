import {useRef, useState} from "react";
import {ActionType, ColumnsState, ProTable} from "@ant-design/pro-components";
import {
    SysRequestAllAvgPro,
    SysRequestAllAvgVO,
    SysRequestDO,
    SysRequestPage,
    SysRequestPageDTO
} from "@/api/SysRequest";
import TableColumnList from "./TableColumnList";
import CommonConstant from "@/model/constant/CommonConstant";
import {Badge, Button, Space, Tooltip, Typography} from "antd";
import {LoadingOutlined, ReloadOutlined} from "@ant-design/icons";
import {FormatDateTime} from "@/util/DateUtil";

export function GetAvgType(avg: number) {
    return avg < 800 ? 'success' : (avg > 1600 ? 'danger' : 'warning')
}

// 请求-管理
export default function () {

    const [columnsStateMap, setColumnsStateMap] = useState<Record<string, ColumnsState>>();

    const [expandedRowKeys, setExpandedRowKeys] = useState<string[]>([]);

    const actionRef = useRef<ActionType>()

    const [lastUpdateTime, setLastUpdateTime] = useState<Date>(new Date());

    const [polling, setPolling] = useState<number | undefined>(CommonConstant.POLLING_TIME)

    const [sysRequestAllAvgVO, setSysRequestAllAvgVO] = useState<SysRequestAllAvgVO>({avgMs: 0, count: '0'})

    return (

        <>

            <ProTable<SysRequestDO, SysRequestPageDTO>

                scroll={{x: 'max-content'}}
                sticky={{offsetHeader: CommonConstant.NAV_TOP_HEIGHT}}
                actionRef={actionRef}
                rowKey={"id"}

                pagination={{
                    showQuickJumper: true,
                    showSizeChanger: true,
                }}

                headerTitle={

                    <Space size={16}>

                        <span>上次更新时间：{FormatDateTime(lastUpdateTime)}</span>

                        <Tooltip title={`筛选条件，接口平均响应耗时，共请求 ${sysRequestAllAvgVO.count}次`}>

                            <span className={"hand"}>

                                <Badge status="processing"
                                       text={

                                           <Typography.Text

                                               strong

                                               type={GetAvgType(sysRequestAllAvgVO.avgMs!)}>

                                               {sysRequestAllAvgVO.avgMs}ms

                                           </Typography.Text>

                                       }
                                />

                            </span>

                        </Tooltip>

                    </Space>

                }

                polling={polling}

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

                columns={TableColumnList()}

                options={{
                    fullScreen: true,
                }}

                request={(params, sort, filter) => {

                    setLastUpdateTime(new Date())

                    SysRequestAllAvgPro({...params}).then(res => {
                        setSysRequestAllAvgVO(res.data)
                    })

                    return SysRequestPage({...params, sort})

                }}

                toolbar={{

                    actions: [

                        <Button

                            key="1"
                            type="primary"

                            onClick={() => {

                                if (polling) {

                                    setPolling(undefined);

                                    return;

                                }

                                setPolling(CommonConstant.POLLING_TIME);

                            }}

                        >

                            {polling ? <LoadingOutlined/> : <ReloadOutlined/>}
                            {polling ? '停止轮询' : '开始轮询'}

                        </Button>,

                    ],

                }}

            >

            </ProTable>

        </>

    )

}
