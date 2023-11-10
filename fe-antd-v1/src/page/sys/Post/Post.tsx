import {useRef, useState} from "react";
import {
    ActionType,
    BetaSchemaForm,
    ColumnsState,
    FormInstance,
    ModalForm,
    ProFormDigit,
    ProTable
} from "@ant-design/pro-components";
import {Button, Space} from "antd";
import {ColumnHeightOutlined, PlusOutlined, VerticalAlignMiddleOutlined} from "@ant-design/icons/lib";
import {
    SysPostAddOrderNo,
    SysPostDeleteByIdSet,
    SysPostDO,
    SysPostInfoById,
    SysPostInsertOrUpdate,
    SysPostInsertOrUpdateDTO,
    SysPostPageDTO,
    SysPostTree
} from "@/api/http/SysPost";
import TableColumnList from "./TableColumnList";
import {ExecConfirm, ToastSuccess} from "@/util/ToastUtil";
import SchemaFormColumnList, {InitForm} from "./SchemaFormColumnList";
import {CalcOrderNo, GetIdListForHasChildrenNode} from "@/util/TreeUtil";
import CommonConstant from "@/model/constant/CommonConstant";
import {IMyTree} from "@/util/DictUtil";
import {UseEffectFullScreenChange} from "@/util/UseEffectUtil";

// 岗位-管理
export default function () {

    const [columnsStateMap, setColumnsStateMap] = useState<Record<string, ColumnsState>>();

    const [expandedRowKeys, setExpandedRowKeys] = useState<string[]>([]);

    const hasChildrenIdList = useRef<string[]>([]); // 有子节点的 idList

    const actionRef = useRef<ActionType>()

    const formRef = useRef<FormInstance<SysPostInsertOrUpdateDTO>>();

    const [formOpen, setFormOpen] = useState<boolean>(false);

    const currentForm = useRef<SysPostInsertOrUpdateDTO>({} as SysPostInsertOrUpdateDTO)

    const [fullScreenFlag, setFullScreenFlag] = useState<boolean>(false)

    const treeListRef = useRef<IMyTree[]>([]) // table的数据

    UseEffectFullScreenChange(setFullScreenFlag) // 监听是否：全屏

    return (

        <>

            <ProTable<SysPostDO, SysPostPageDTO>

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

                    return SysPostTree({...params, sort})

                }}

                postData={(data: any) => {

                    treeListRef.current = data

                    hasChildrenIdList.current = GetIdListForHasChildrenNode(data)

                    return data

                }}

                toolbar={{

                    title:

                        <Space size={16}>

                            <Button

                                onClick={() => {

                                    setExpandedRowKeys(hasChildrenIdList.current)

                                }}

                                icon={<ColumnHeightOutlined/>}

                            >

                                展开

                            </Button>

                            <Button

                                onClick={() => {

                                    setExpandedRowKeys([])

                                }}

                                icon={<VerticalAlignMiddleOutlined/>}

                            >

                                收起

                            </Button>

                        </Space>,

                    actions: [

                        <Button key={"1"} icon={<PlusOutlined/>} type="primary" onClick={() => {

                            currentForm.current = {} as SysPostInsertOrUpdateDTO

                            CalcOrderNo(currentForm.current, {children: treeListRef.current});

                            setFormOpen(true)

                        }}>新建</Button>

                    ],

                }}

                tableAlertOptionRender={({selectedRowKeys, selectedRows, onCleanSelected}) => (

                    <Space size={16}>

                        <ModalForm<SysPostInsertOrUpdateDTO>

                            modalProps={{
                                maskClosable: false
                            }}

                            isKeyPressSubmit

                            width={CommonConstant.MODAL_FORM_WIDTH}
                            title={CommonConstant.ADD_ORDER_NO}
                            trigger={<a>{CommonConstant.ADD_ORDER_NO}</a>}

                            onFinish={async (form) => {

                                await SysPostAddOrderNo({

                                    idSet: selectedRowKeys as string[],
                                    number: String(form.orderNo)

                                }).then(res => {

                                    ToastSuccess(res.msg)
                                    actionRef.current?.reload()

                                })

                                return true

                            }}

                        >

                            <ProFormDigit label={CommonConstant.ADD_VALUE} name="orderNo" tooltip={"可以为负数"}
                                          min={Number.MIN_SAFE_INTEGER} className={"w100"}
                                          rules={[{required: true}]}/>

                        </ModalForm>

                        <a className={"red3"} onClick={() => {

                            ExecConfirm(() => {

                                return SysPostDeleteByIdSet({idSet: selectedRowKeys as string[]}).then(res => {

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

            <BetaSchemaForm<SysPostInsertOrUpdateDTO>

                title={currentForm.current.id ? "编辑岗位" : "新建岗位"}
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

                                        return SysPostDeleteByIdSet({idSet: [currentForm.current.id!]}).then(res => {

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

                        SysPostInfoById({id: currentForm.current.id}).then(res => {

                            currentForm.current = res as SysPostInsertOrUpdateDTO

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

                    await SysPostInsertOrUpdate({...currentForm.current, ...form}).then(res => {

                        ToastSuccess(res.msg)
                        actionRef.current?.reload()

                    })

                    return true

                }}

            />

        </>

    )

}
