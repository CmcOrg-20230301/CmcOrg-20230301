import {useRef, useState} from "react";
import {ActionType, BetaSchemaForm, ColumnsState, ModalForm, ProFormDigit, ProTable} from "@ant-design/pro-components";
import {Button, Dropdown, Form, Space} from "antd";
import {ColumnHeightOutlined, EllipsisOutlined, PlusOutlined, VerticalAlignMiddleOutlined} from "@ant-design/icons/lib";
import {
    AdminAddOrderNo,
    AdminDeleteByIdSet,
    AdminDO,
    AdminInfoById,
    AdminInsertOrUpdate,
    AdminInsertOrUpdateDTO,
    AdminPageDTO,
    AdminTree
} from "@/api/admin/AdminController";
import TableColumnList from "./TableColumnList";
import {ExecConfirm, ToastSuccess} from "@/util/ToastUtil";
import SchemaFormColumnList, {InitForm} from "./SchemaFormColumnList";
import {GetIdListForHasChildrenNode} from "@/util/TreeUtil";
import CommonConstant from "@/model/constant/CommonConstant";

// AdminTsxTitle
export default function () {

    const [columnsStateMap, setColumnsStateMap] = useState<Record<string, ColumnsState>>();

    const [expandedRowKeys, setExpandedRowKeys] = useState<number[]>([]);

    const hasChildrenIdList = useRef<number[]>([]); // 有子节点的 idList

    const actionRef = useRef<ActionType>(null)

    const [useForm] = Form.useForm<AdminInsertOrUpdateDTO>();

    const [formVisible, setFormVisible] = useState<boolean>(false);

    const currentForm = useRef<AdminInsertOrUpdateDTO>({} as AdminInsertOrUpdateDTO)

    return (
        <>
            <ProTable<AdminDO, AdminPageDTO>
                scroll={{x: 'max-content'}}
                sticky={{offsetHeader: CommonConstant.NAV_TOP_HEIGHT}}
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
                        setExpandedRowKeys(expandedRows as number[])
                    },
                }}
                revalidateOnFocus={false}
                columns={TableColumnList(currentForm, setFormVisible, actionRef)}
                options={{
                    fullScreen: true,
                }}
                request={(params, sort, filter) => {
                    return AdminTree({...params, sort})
                }}
                postData={(data) => {
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
                            currentForm.current = {} as AdminInsertOrUpdateDTO
                            setFormVisible(true)
                        }}>新建</Button>
                    ],
                }}
                tableAlertOptionRender={({selectedRowKeys, selectedRows, onCleanSelected}) => (
                    <Space size={16}>
                        <ModalForm<AdminInsertOrUpdateDTO>
                            modalProps={{
                                maskClosable: false
                            }}
                            isKeyPressSubmit
                            width={CommonConstant.MODAL_FORM_WIDTH}
                            title={CommonConstant.ADD_ORDER_NO}
                            trigger={<a>{CommonConstant.ADD_ORDER_NO}</a>}
                            onFinish={async (form) => {
                                await AdminAddOrderNo({
                                    idSet: selectedRowKeys as number[],
                                    number: form.orderNo!
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
                                return AdminDeleteByIdSet({idSet: selectedRowKeys as number[]}).then(res => {
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

            <BetaSchemaForm<AdminInsertOrUpdateDTO>
                title={currentForm.current.id ? "编辑AdminModalFormTitle" : "新建AdminModalFormTitle"}
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
                                        return AdminDeleteByIdSet({idSet: [currentForm.current.id!]}).then(res => {
                                            setFormVisible(false)
                                            ToastSuccess(res.msg)
                                            actionRef.current?.reload()
                                        })
                                    }, undefined, `确定删除【${currentForm.current.AdminDeleteName}】吗？`)
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
                        await AdminInfoById({id: currentForm.current.id}).then(res => {
                            currentForm.current = res as AdminInsertOrUpdateDTO
                        })
                    }
                    useForm.setFieldsValue(currentForm.current) // 组件会深度克隆 currentForm.current

                    return InitForm
                }}
                visible={formVisible}
                onVisibleChange={setFormVisible}
                columns={SchemaFormColumnList()}
                onFinish={async (form) => {
                    await AdminInsertOrUpdate({...currentForm.current, ...form}).then(res => {
                        ToastSuccess(res.msg)
                        actionRef.current?.reload()
                    })
                    return true
                }}
            />
        </>
    )
}
