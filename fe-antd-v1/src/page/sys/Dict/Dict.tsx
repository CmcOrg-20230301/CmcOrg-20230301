import {useEffect, useRef, useState} from "react";
import {ActionType, BetaSchemaForm, ColumnsState, ModalForm, ProFormDigit, ProTable} from "@ant-design/pro-components";
import {Button, Dropdown, Form, Space} from "antd";
import {ColumnHeightOutlined, EllipsisOutlined, PlusOutlined, VerticalAlignMiddleOutlined} from "@ant-design/icons/lib";
import {
    SysDictAddOrderNo,
    SysDictDeleteByIdSet,
    SysDictDO,
    SysDictInfoById,
    SysDictInsertOrUpdate,
    SysDictInsertOrUpdateDTO,
    SysDictPageDTO,
    SysDictTree
} from "@/api/SysDict";
import TableColumnList from "./TableColumnList";
import {ExecConfirm, ToastSuccess} from "@/util/ToastUtil";
import SchemaFormColumnList, {InitForm} from "./SchemaFormColumnList";
import {GetIdListForHasChildrenNode} from "@/util/TreeUtil";
import CommonConstant from "@/model/constant/CommonConstant";

// 字典-管理
export default function () {

    const [columnsStateMap, setColumnsStateMap] = useState<Record<string, ColumnsState>>();

    const [expandedRowKeys, setExpandedRowKeys] = useState<string[]>([]);

    const hasChildrenIdList = useRef<string[]>([]); // 有子节点的 idList

    const actionRef = useRef<ActionType>()

    const [useForm] = Form.useForm<SysDictInsertOrUpdateDTO>();

    const [formOpen, setFormOpen] = useState<boolean>(false);

    const currentForm = useRef<SysDictInsertOrUpdateDTO>({} as SysDictInsertOrUpdateDTO)

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

            <ProTable<SysDictDO, SysDictPageDTO>

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

                    return SysDictTree({...params, sort})

                }}

                postData={(data: any) => {

                    hasChildrenIdList.current = GetIdListForHasChildrenNode(data)
                    return data

                }}

                toolbar={{

                    title:

                        <Dropdown menu={{

                            items: [
                                {

                                    key: '1',

                                    label: <a onClick={() => {

                                        setExpandedRowKeys(hasChildrenIdList.current)

                                    }}>
                                        展开全部
                                    </a>,

                                    icon: <ColumnHeightOutlined/>

                                },

                                {

                                    key: '2',

                                    label: <a onClick={() => {

                                        setExpandedRowKeys([])

                                    }}>
                                        收起全部
                                    </a>,

                                    icon: <VerticalAlignMiddleOutlined/>

                                },

                            ]

                        }}>

                            <Button size={"small"} icon={<EllipsisOutlined/>}/>

                        </Dropdown>,

                    actions: [

                        <Button key={"1"} icon={<PlusOutlined/>} type="primary" onClick={() => {

                            currentForm.current = {} as SysDictInsertOrUpdateDTO
                            setFormOpen(true)

                        }}>新建</Button>

                    ],

                }}

                tableAlertOptionRender={({selectedRowKeys, selectedRows, onCleanSelected}) => (

                    <Space size={16}>

                        <ModalForm<SysDictInsertOrUpdateDTO>

                            modalProps={{
                                maskClosable: false
                            }}

                            isKeyPressSubmit

                            width={CommonConstant.MODAL_FORM_WIDTH}
                            title={CommonConstant.ADD_ORDER_NO}
                            trigger={<a>{CommonConstant.ADD_ORDER_NO}</a>}

                            onFinish={async (form) => {

                                await SysDictAddOrderNo({

                                    idSet: selectedRowKeys as string[],
                                    number: String(form.orderNo)

                                }).then(res => {

                                    ToastSuccess(res.msg)
                                    actionRef.current?.reload()

                                })

                                return true

                            }}

                        >

                            <ProFormDigit label="排序号" name="orderNo" min={Number.MIN_SAFE_INTEGER} className={"w100"}
                                          rules={[{required: true}]}/>

                        </ModalForm>

                        <a className={"red3"} onClick={() => {

                            ExecConfirm(() => {

                                return SysDictDeleteByIdSet({idSet: selectedRowKeys as string[]}).then(res => {

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

            <BetaSchemaForm<SysDictInsertOrUpdateDTO>

                title={currentForm.current.id ? "编辑字典" : "新建字典"}
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

                form={useForm}

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

                                        return SysDictDeleteByIdSet({idSet: [currentForm.current.id!]}).then(res => {

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

                    useForm.resetFields()

                    if (currentForm.current.id) {

                        await SysDictInfoById({id: currentForm.current.id}).then(res => {

                            currentForm.current = res as SysDictInsertOrUpdateDTO

                        })

                    }

                    useForm.setFieldsValue(currentForm.current) // 组件会深度克隆 currentForm.current

                    return InitForm

                }}

                open={formOpen}
                onOpenChange={setFormOpen}
                columns={SchemaFormColumnList()}

                onFinish={async (form) => {

                    await SysDictInsertOrUpdate({...currentForm.current, ...form}).then(res => {

                        ToastSuccess(res.msg)
                        actionRef.current?.reload()

                    })

                    return true

                }}

            />

        </>

    )

}
