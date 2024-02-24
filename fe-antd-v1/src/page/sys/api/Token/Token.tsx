import {useRef, useState} from "react";
import {ActionType, BetaSchemaForm, ColumnsState, FormInstance, ProTable} from "@ant-design/pro-components";
import {Button, Space, Typography} from "antd";
import {PlusOutlined} from "@ant-design/icons";

import TableColumnList from "./TableColumnList";
import {ExecConfirm, ToastInfo, ToastSuccess} from "@/util/ToastUtil";
import SchemaFormColumnList, {InitForm} from "./SchemaFormColumnList";
import CommonConstant from "@/model/constant/CommonConstant";
import {UseEffectFullScreenChange} from "@/util/UseEffectUtil";
import {
    SysApiTokenDeleteByIdSet,
    SysApiTokenDO,
    SysApiTokenInfoById,
    SysApiTokenInsertOrUpdate,
    SysApiTokenInsertOrUpdateDTO,
    SysApiTokenPage,
    SysApiTokenPageDTO
} from "@/api/http/SysApi.ts";
import {GetApp} from "@/MyApp.tsx";

// api-token-管理
export default function () {

    const [columnsStateMap, setColumnsStateMap] = useState<Record<string, ColumnsState>>();

    const actionRef = useRef<ActionType>()

    const formRef = useRef<FormInstance<SysApiTokenInsertOrUpdateDTO>>();

    const [formOpen, setFormOpen] = useState<boolean>(false);

    const currentForm = useRef<SysApiTokenInsertOrUpdateDTO>({} as SysApiTokenInsertOrUpdateDTO)

    const [fullScreenFlag, setFullScreenFlag] = useState<boolean>(false)

    UseEffectFullScreenChange(setFullScreenFlag) // 监听是否：全屏

    return (

        <>

            <ProTable<SysApiTokenDO, SysApiTokenPageDTO>

                scroll={{x: 'max-content'}}
                sticky={{offsetHeader: fullScreenFlag ? 0 : CommonConstant.NAV_TOP_HEIGHT}}

                actionRef={actionRef}
                rowKey={"id"}
                pagination={false}
                columnEmptyText={false}

                columnsState={{
                    value: columnsStateMap,
                    onChange: setColumnsStateMap,
                }}

                rowSelection={{}}

                revalidateOnFocus={false}

                columns={TableColumnList(currentForm, setFormOpen, actionRef)}

                options={{
                    fullScreen: true,
                }}

                request={(params, sort, filter) => {

                    return SysApiTokenPage({...params, sort})

                }}

                toolbar={{

                    actions: [

                        <Button key={"1"} icon={<PlusOutlined/>} type="primary" onClick={() => {

                            currentForm.current = {} as SysApiTokenInsertOrUpdateDTO

                            setFormOpen(true)

                        }}>新建</Button>

                    ],

                }}

                tableAlertOptionRender={({selectedRowKeys, selectedRows, onCleanSelected}) => (

                    <Space size={16}>

                        <a className={"red3"} onClick={() => {

                            ExecConfirm(async () => {

                                await SysApiTokenDeleteByIdSet({idSet: selectedRowKeys as string[]}).then(res => {

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

            <BetaSchemaForm<SysApiTokenInsertOrUpdateDTO>

                title={currentForm.current.id ? "编辑访问令牌" : "新建访问令牌"}
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

                                        await SysApiTokenDeleteByIdSet({idSet: [currentForm.current.id!]}).then(res => {

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

                        SysApiTokenInfoById({id: currentForm.current.id}).then(res => {

                            currentForm.current = res as SysApiTokenInsertOrUpdateDTO

                            formRef.current?.setFieldsValue(currentForm.current)

                        })

                    } else {

                        setTimeout(() => {

                            formRef.current?.setFieldsValue(currentForm.current)

                        }, CommonConstant.SHORT_DELAY)

                    }

                    return InitForm

                }}

                open={formOpen}
                onOpenChange={setFormOpen}
                columns={SchemaFormColumnList()}

                onFinish={async (form) => {

                    await SysApiTokenInsertOrUpdate({...currentForm.current, ...form}).then(res => {

                        ToastSuccess(res.msg)
                        actionRef.current?.reload()

                        if (res.data) {

                            ToastInfo(
                                <div className={"flex-c"}>

                                    <Typography.Text className={"m-t-b-10"}
                                                     type="secondary">请确保立即复制访问令牌，因为您将无法再次看到它。</Typography.Text>

                                    <Typography.Text copyable={{

                                        text: res.data,

                                        onCopy: () => {

                                            GetApp().message.destroy() // 关闭弹窗

                                        }

                                    }}>{res.data}</Typography.Text>

                                </div>
                                , 60 * 60 * 24)

                        }

                    })

                    return true

                }}

            />

        </>

    )

}
