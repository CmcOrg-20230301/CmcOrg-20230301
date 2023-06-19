import {useEffect, useRef, useState} from "react";
import {ActionType, BetaSchemaForm, ColumnsState, FormInstance, ProTable} from "@ant-design/pro-components";
import {Button, Space} from "antd";
import {PlusOutlined} from "@ant-design/icons/lib";
import {
    SysUserDeleteByIdSet,
    SysUserInfoById,
    SysUserInsertOrUpdate,
    SysUserInsertOrUpdateDTO,
    SysUserPage,
    SysUserPageDTO,
    SysUserPageVO,
    SysUserRefreshJwtSecretSuf,
    SysUserResetAvatar
} from "@/api/SysUser";
import TableColumnList, {SysUserUpdatePasswordModalForm} from "./TableColumnList";
import {ExecConfirm, ToastSuccess} from "@/util/ToastUtil";
import SchemaFormColumnList, {InitForm} from "./SchemaFormColumnList";
import CommonConstant from "@/model/constant/CommonConstant";
import {SysFileGetPublicUrl} from "@/api/SysFile";

// 用户-管理
export default function () {

    const [columnsStateMap, setColumnsStateMap] = useState<Record<string, ColumnsState>>();

    const [expandedRowKeys, setExpandedRowKeys] = useState<string[]>([]);

    const actionRef = useRef<ActionType>()

    const formRef = useRef<FormInstance<SysUserInsertOrUpdateDTO>>();

    const [formOpen, setFormOpen] = useState<boolean>(false);

    const currentForm = useRef<SysUserInsertOrUpdateDTO>({} as SysUserInsertOrUpdateDTO)

    const [fullScreenFlag, setFullScreenFlag] = useState<boolean>(false)

    const [userAvatarUrlObj, setUserAvatarUrlObj] = useState<Record<string, string>>({})

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

            <ProTable<SysUserPageVO, SysUserPageDTO>

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

                columns={TableColumnList(currentForm, setFormOpen, actionRef, userAvatarUrlObj)}

                options={{
                    fullScreen: true,
                }}

                request={(params, sort, filter) => {

                    return SysUserPage({...params, sort})

                }}

                postData={(data: SysUserPageVO[]) => {

                    let avatarFileIdList = data.map(it => it.avatarFileId!); // 用户头像文件 id集合

                    SysFileGetPublicUrl({idSet: avatarFileIdList}).then(res => {

                        setUserAvatarUrlObj(res.data.map as any)

                    })

                    return data

                }}

                toolbar={{

                    actions: [

                        <Button key={"1"} icon={<PlusOutlined/>} type="primary" onClick={() => {

                            currentForm.current = {} as SysUserInsertOrUpdateDTO
                            setFormOpen(true)

                        }}>新建</Button>

                    ],

                }}

                tableAlertOptionRender={({selectedRowKeys, selectedRows, onCleanSelected}) => (

                    <Space size={16}>

                        <a onClick={() => {

                            ExecConfirm(() => {

                                return SysUserResetAvatar({idSet: selectedRowKeys as string[]}).then(res => {

                                    ToastSuccess(res.msg)
                                    actionRef.current?.reload()

                                })

                            }, undefined, `确定重置选中的【${selectedRowKeys.length}】项的头像吗？`)

                        }}>重置头像</a>

                        <SysUserUpdatePasswordModalForm idSet={selectedRowKeys as string[]} actionRef={actionRef}/>

                        <a onClick={() => {

                            ExecConfirm(() => {

                                return SysUserRefreshJwtSecretSuf({idSet: selectedRowKeys as string[]}).then(res => {

                                    ToastSuccess(res.msg)
                                    actionRef.current?.reload()

                                })

                            }, undefined, `确定刷新选中的【${selectedRowKeys.length}】项的令牌吗？`)

                        }}>刷新令牌</a>

                        <a className={"red3"} onClick={() => {

                            ExecConfirm(() => {

                                return SysUserDeleteByIdSet({idSet: selectedRowKeys as string[]}).then(res => {

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

            <BetaSchemaForm<SysUserInsertOrUpdateDTO>

                title={currentForm.current.id ? "编辑用户" : "新建用户"}
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

                                        return SysUserDeleteByIdSet({idSet: [currentForm.current.id!]}).then(res => {

                                            setFormOpen(false)
                                            ToastSuccess(res.msg)
                                            actionRef.current?.reload()

                                        })

                                    }, undefined, `确定删除【${currentForm.current.signInName}】吗？`)

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

                        await SysUserInfoById({id: currentForm.current.id}).then(res => {

                            currentForm.current = res as SysUserInsertOrUpdateDTO

                        })

                    }

                    formRef.current?.setFieldsValue(currentForm.current) // 组件会深度克隆 currentForm.current

                    return InitForm

                }}

                open={formOpen}
                onOpenChange={setFormOpen}
                columns={SchemaFormColumnList(formRef)}

                onFinish={async (form) => {

                    await SysUserInsertOrUpdate({...currentForm.current, ...form}).then(res => {

                        ToastSuccess(res.msg)
                        actionRef.current?.reload()

                    })

                    return true

                }}

            />

        </>

    )

}
