import React, {useEffect, useRef, useState} from "react";
import {
    ActionType,
    BetaSchemaForm,
    ColumnsState,
    FormInstance,
    ModalForm,
    PageContainer,
    ProFormDigit,
    ProTable,
    RouteContext,
    RouteContextType
} from "@ant-design/pro-components";
import {Button, Space} from "antd";
import {ColumnHeightOutlined, PlusOutlined, RollbackOutlined, VerticalAlignMiddleOutlined} from "@ant-design/icons";
import TableColumnList from "./TableColumnList";
import {ExecConfirm, ToastSuccess} from "@/util/ToastUtil";
import SchemaFormColumnList, {InitForm} from "./SchemaFormColumnList";
import {CalcOrderNo, GetIdListForHasChildrenNode} from "@/util/TreeUtil";
import CommonConstant from "@/model/constant/CommonConstant";
import {IMyTree} from "@/util/DictUtil";
import {UseEffectFullScreenChange} from "@/util/UseEffectUtil";
import {
    SysOtherAppGetNameById,
    SysOtherAppOfficialAccountMenuAddOrderNo,
    SysOtherAppOfficialAccountMenuDeleteByIdSet,
    SysOtherAppOfficialAccountMenuDO,
    SysOtherAppOfficialAccountMenuInfoById,
    SysOtherAppOfficialAccountMenuInsertOrUpdate,
    SysOtherAppOfficialAccountMenuInsertOrUpdateDTO,
    SysOtherAppOfficialAccountMenuPageDTO,
    SysOtherAppOfficialAccountMenuTree
} from "@/api/http/SysOtherApp";
import {SysWxOfficialAccountUpdateMenu} from "@/api/http/SysWx";
import {Navigate, useLocation} from "react-router-dom";
import LocalStorageKey from "@/model/constant/LocalStorageKey";
import PathConstant from "@/model/constant/PathConstant";
import {GoPage} from "@/layout/AdminLayout/AdminLayout";
import {MyLocalStorage} from "@/util/StorageUtil";

// 第三方应用-公众号-菜单-管理
export default function () {

    let location = useLocation();

    if (!location.state?.otherAppId) {

        // 获取：历史值：otherAppId
        const otherAppId = MyLocalStorage.getItem(LocalStorageKey.SYS_OTHER_APP_OFFICIAL_ACCOUNT_MENU_OTHER_APP_ID);

        if (otherAppId) {

            location.state = {otherAppId: otherAppId}

        } else {

            return <Navigate to={PathConstant.SYS_OTHER_APP_PATH}/>

        }

    }

    const [columnsStateMap, setColumnsStateMap] = useState<Record<string, ColumnsState>>();

    const [expandedRowKeys, setExpandedRowKeys] = useState<string[]>([]);

    const hasChildrenIdList = useRef<string[]>([]); // 有子节点的 idList

    const actionRef = useRef<ActionType>()

    const formRef = useRef<FormInstance<SysOtherAppOfficialAccountMenuInsertOrUpdateDTO>>();

    const [formOpen, setFormOpen] = useState<boolean>(false);

    const currentForm = useRef<SysOtherAppOfficialAccountMenuInsertOrUpdateDTO>({} as SysOtherAppOfficialAccountMenuInsertOrUpdateDTO)

    const [fullScreenFlag, setFullScreenFlag] = useState<boolean>(false)

    const treeListRef = useRef<IMyTree[]>([]) // table的数据

    UseEffectFullScreenChange(setFullScreenFlag) // 监听是否：全屏

    const [otherAppName, setOtherAppName] = useState<string>(""); // 第三方应用名

    useEffect(() => {

        if (location.state?.otherAppId) {

            MyLocalStorage.setItem(LocalStorageKey.SYS_OTHER_APP_OFFICIAL_ACCOUNT_MENU_OTHER_APP_ID, location.state?.otherAppId) // 存储起来下次使用

            SysOtherAppGetNameById({id: location.state.otherAppId}).then(res => {

                setOtherAppName(res.data!)

            })

        }

    }, [])

    return (

        <>

            <RouteContext.Consumer>

                {(routeContextType: RouteContextType) => {

                    return (

                        <PageContainer title={routeContextType.currentMenu!.name + "：" + otherAppName}>

                            <ProTable<SysOtherAppOfficialAccountMenuDO, SysOtherAppOfficialAccountMenuPageDTO>

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

                                    return SysOtherAppOfficialAccountMenuTree({
                                        ...params,
                                        sort,
                                        otherAppId: location.state?.otherAppId
                                    })

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

                                            <Button

                                                icon={<RollbackOutlined/>}
                                                onClick={() => {
                                                    GoPage(PathConstant.SYS_OTHER_APP_PATH)
                                                }}

                                            >返回列表</Button>

                                            <Button type="primary" onClick={() => {

                                                ExecConfirm(async () => {

                                                    await SysWxOfficialAccountUpdateMenu({id: location.state?.otherAppId}).then(res => {

                                                        ToastSuccess(res.msg)

                                                    })

                                                }, undefined, `确定同步到微信吗？`)

                                            }}>同步到微信</Button>

                                        </Space>,

                                    actions: [

                                        <Button key={"1"} icon={<PlusOutlined/>} type="primary" onClick={() => {

                                            currentForm.current = {} as SysOtherAppOfficialAccountMenuInsertOrUpdateDTO

                                            CalcOrderNo(currentForm.current, {children: treeListRef.current});

                                            setFormOpen(true)

                                        }}>新建</Button>

                                    ],

                                }}

                                tableAlertOptionRender={({selectedRowKeys, selectedRows, onCleanSelected}) => (

                                    <Space size={16}>

                                        <ModalForm<SysOtherAppOfficialAccountMenuInsertOrUpdateDTO>

                                            modalProps={{
                                                maskClosable: false
                                            }}

                                            isKeyPressSubmit

                                            width={CommonConstant.MODAL_FORM_WIDTH}
                                            title={CommonConstant.ADD_ORDER_NO}
                                            trigger={<a>{CommonConstant.ADD_ORDER_NO}</a>}

                                            onFinish={async (form) => {

                                                await SysOtherAppOfficialAccountMenuAddOrderNo({

                                                    idSet: selectedRowKeys as string[],
                                                    number: String(form.orderNo)

                                                }).then(res => {

                                                    ToastSuccess(res.msg)
                                                    actionRef.current?.reload()

                                                })

                                                return true

                                            }}

                                        >

                                            <ProFormDigit label={CommonConstant.ADD_VALUE} name="orderNo"
                                                          tooltip={"可以为负数"}
                                                          min={Number.MIN_SAFE_INTEGER} className={"w100"}
                                                          rules={[{required: true}]}/>

                                        </ModalForm>

                                        <a className={"red3"} onClick={() => {

                                            ExecConfirm(async () => {

                                                await SysOtherAppOfficialAccountMenuDeleteByIdSet({idSet: selectedRowKeys as string[]}).then(res => {

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

                            <BetaSchemaForm<SysOtherAppOfficialAccountMenuInsertOrUpdateDTO>

                                title={currentForm.current.id ? "编辑公众号菜单" : "新建公众号菜单"}
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

                                                        await SysOtherAppOfficialAccountMenuDeleteByIdSet({idSet: [currentForm.current.id!]}).then(res => {

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

                                        SysOtherAppOfficialAccountMenuInfoById({id: currentForm.current.id}).then(res => {

                                            currentForm.current = res as SysOtherAppOfficialAccountMenuInsertOrUpdateDTO

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
                                columns={SchemaFormColumnList(location.state?.otherAppId)}

                                onFinish={async (form) => {

                                    await SysOtherAppOfficialAccountMenuInsertOrUpdate({
                                        ...currentForm.current, ...form,
                                        otherAppId: location.state?.otherAppId
                                    }).then(res => {

                                        ToastSuccess(res.msg)
                                        actionRef.current?.reload()

                                    })

                                    return true

                                }}

                            />

                        </PageContainer>

                    )

                }}

            </RouteContext.Consumer>

        </>

    )

}
