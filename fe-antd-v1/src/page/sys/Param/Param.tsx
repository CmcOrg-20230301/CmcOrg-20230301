import {useEffect, useRef, useState} from "react";
import {ActionType, BetaSchemaForm, ColumnsState, FormInstance, ProTable} from "@ant-design/pro-components";
import {Button, Space} from "antd";
import {PlusOutlined} from "@ant-design/icons/lib";
import {
    SysParamDeleteByIdSet,
    SysParamDO,
    SysParamInfoById,
    SysParamInsertOrUpdate,
    SysParamInsertOrUpdateDTO,
    SysParamPage,
    SysParamPageDTO
} from "@/api/http/SysParam";
import TableColumnList from "./TableColumnList";
import {ExecConfirm, ToastSuccess} from "@/util/ToastUtil";
import SchemaFormColumnList, {InitForm} from "./SchemaFormColumnList";
import CommonConstant from "@/model/constant/CommonConstant";

// 系统参数-管理
export default function () {

    const [columnsStateMap, setColumnsStateMap] = useState<Record<string, ColumnsState>>();

    const [expandedRowKeys, setExpandedRowKeys] = useState<string[]>([]);

    const actionRef = useRef<ActionType>()

    const formRef = useRef<FormInstance<SysParamInsertOrUpdateDTO>>();

    const [formOpen, setFormOpen] = useState<boolean>(false);

    const currentForm = useRef<SysParamInsertOrUpdateDTO>({} as SysParamInsertOrUpdateDTO)

    const [fullScreenFlag, setFullScreenFlag] = useState<boolean>(false)

    useEffect(() => {

        const handleFullScreenChange = () => {
            setFullScreenFlag(document.fullscreenElement !== null)
        }

        document.addEventListener('fullscreenchange', handleFullScreenChange);

        return () => {

            document.removeEventListener('fullscreenchange', handleFullScreenChange);

        }

    }, [])

    return (

        <>

            <ProTable<SysParamDO, SysParamPageDTO>

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

                columns={TableColumnList(currentForm, setFormOpen, actionRef)}

                options={{
                    fullScreen: true,
                }}

                request={(params, sort, filter) => {

                    return SysParamPage({...params, sort})

                }}

                toolbar={{

                    actions: [

                        <Button key={"1"} icon={<PlusOutlined/>} type="primary" onClick={() => {

                            currentForm.current = {} as SysParamInsertOrUpdateDTO
                            setFormOpen(true)

                        }}>新建</Button>

                    ],

                }}

                tableAlertOptionRender={({selectedRowKeys, selectedRows, onCleanSelected}) => (

                    <Space size={16}>

                        <a className={"red3"} onClick={() => {

                            ExecConfirm(() => {

                                return SysParamDeleteByIdSet({idSet: selectedRowKeys as string[]}).then(res => {

                                    ToastSuccess(res.msg)
                                    actionRef.current?.reload()
                                    onCleanSelected()

                                })

                            }, undefined, `确定删除选中的【${selectedRowKeys.length}】项吗？`)

                        }}>批量删除</a>

                        <a onClick={onCleanSelected}>取消选择</a>

                    </Space>

                )}

            >

            </ProTable>

            <BetaSchemaForm<SysParamInsertOrUpdateDTO>

                title={currentForm.current.id ? "编辑系统参数" : "新建系统参数"}
                layoutType={"ModalForm"}
                grid

                rowProps={{
                    gutter: 16
                }}

                colProps={{
                    span: 8
                }}

                modalProps={{
                    maskClosable: false,
                }}

                formRef={formRef}

                isKeyPressSubmit

                submitter={{

                    render: (props, dom) => {

                        return [

                            ...dom,

                            <Button

                                key="1"

                                onClick={() => {

                                    ExecConfirm(async () => {

                                        props.reset();

                                    }, undefined, "确定重置表单吗？")

                                }}
                            >
                                重置

                            </Button>,

                            currentForm.current.id ? <Button

                                key="2"
                                type="primary"
                                danger

                                onClick={() => {

                                    ExecConfirm(async () => {

                                        return SysParamDeleteByIdSet({idSet: [currentForm.current.id!]}).then(res => {

                                            setFormOpen(false)
                                            ToastSuccess(res.msg)
                                            actionRef.current?.reload()

                                        })

                                    }, undefined, `确定删除【${currentForm.current.name}】吗？`)

                                }}>

                                删除

                            </Button> : null

                        ]

                    },

                }}

                params={new Date()} // 目的：为了打开页面时，执行 request方法

                request={async () => {

                    formRef.current?.resetFields()

                    if (currentForm.current.id) {

                        SysParamInfoById({id: currentForm.current.id}).then(res => {

                            currentForm.current = res as SysParamInsertOrUpdateDTO

                            formRef.current?.setFieldsValue(currentForm.current) // 组件会深度克隆 currentForm.current

                        })

                    }

                    formRef.current?.setFieldsValue(currentForm.current) // 组件会深度克隆 currentForm.current

                    return InitForm

                }}

                open={formOpen}
                onOpenChange={setFormOpen}
                columns={SchemaFormColumnList()}

                onFinish={async (form) => {

                    await SysParamInsertOrUpdate({...currentForm.current, ...form}).then(res => {

                        ToastSuccess(res.msg)
                        actionRef.current?.reload()

                    })

                    return true

                }}

            />

        </>

    )

}
