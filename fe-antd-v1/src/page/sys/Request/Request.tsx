import {useRef, useState} from "react";
import {ActionType, ColumnsState, ProTable} from "@ant-design/pro-components";
import {Button} from "antd";
import {PlusOutlined} from "@ant-design/icons/lib";
import {SysRequestDO, SysRequestPage, SysRequestPageDTO} from "@/api/SysRequest";
import TableColumnList from "./TableColumnList";
import CommonConstant from "@/model/constant/CommonConstant";

// 请求-管理
export default function () {

    const [columnsStateMap, setColumnsStateMap] = useState<Record<string, ColumnsState>>();

    const [expandedRowKeys, setExpandedRowKeys] = useState<string[]>([]);

    const actionRef = useRef<ActionType>()

    const [formVisible, setFormVisible] = useState<boolean>(false);

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

                columns={TableColumnList(currentForm, setFormVisible, actionRef)}

                options={{
                    fullScreen: true,
                }}

                request={(params, sort, filter) => {

                    return SysRequestPage({...params, sort})

                }}

                toolbar={{

                    actions: [

                        <Button key={"1"} icon={<PlusOutlined/>} type="primary" onClick={() => {

                            currentForm.current = {} as AdminInsertOrUpdateDTO
                            setFormVisible(true)

                        }}>新建</Button>

                    ],

                }}

            >

            </ProTable>

        </>

    )

}
