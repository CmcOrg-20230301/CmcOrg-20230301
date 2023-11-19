import React, {MutableRefObject, useEffect, useRef, useState} from "react";
import {
    ActionType,
    BetaSchemaForm,
    ColumnsState,
    FormInstance,
    ProSchemaValueEnumType,
    ProTable
} from "@ant-design/pro-components";
import {
    NotNullIdAndStringValue,
    SysUserWalletWithdrawLogAccept,
    SysUserWalletWithdrawLogDO,
    SysUserWalletWithdrawLogInfoById,
    SysUserWalletWithdrawLogPage,
    SysUserWalletWithdrawLogPageDTO,
    SysUserWalletWithdrawLogReject,
    SysUserWalletWithdrawLogSuccess
} from "@/api/http/SysUserWalletWithdrawLog";
import {UseEffectFullScreenChange} from "@/util/UseEffectUtil";
import CommonConstant from "@/model/constant/CommonConstant";
import {ExecConfirm, ToastSuccess} from "@/util/ToastUtil";
import {Button, Space, TreeSelect, Typography} from "antd";
import {FormatDateTime} from "@/util/DateUtil";
import {LoadingOutlined, ReloadOutlined} from "@ant-design/icons";
import {DoGetDictList, GetDictList, NoFormGetDictTreeList} from "@/util/DictUtil";
import {SysTenantDictList} from "@/api/http/SysTenant";
import {SearchTransform} from "@/util/CommonUtil";
import {SysUserDictList} from "@/api/http/SysUser";
import {SysUserTenantEnum, SysUserTenantEnumDict} from "@/model/enum/SysUserTenantEnum";
import {SysUserWalletWithdrawStatusEnum, UpdateWithdrawStatusDict} from "@/model/enum/SysUserWalletWithdrawStatusEnum";
import {UserWalletWithdrawLogTableBaseColumnArr} from "@/page/user/Wallet/UserWalletWithdrawLogModal";
import {GetUserWalletWithdrawFormColumnArr} from "@/page/user/Wallet/UserWalletWithdrawModal";

// 提现管理
export default function () {

    const [columnsStateMap, setColumnsStateMap] = useState<Record<string, ColumnsState>>();

    const actionRef = useRef<ActionType>()

    const [lastUpdateTime, setLastUpdateTime] = useState<Date>(new Date());

    const [polling, setPolling] = useState<number | undefined>()

    const formRef = useRef<FormInstance<SysUserWalletWithdrawLogDO>>();

    const [formOpen, setFormOpen] = useState<boolean>(false);

    const currentForm = useRef<SysUserWalletWithdrawLogDO>({} as SysUserWalletWithdrawLogDO)

    const [fullScreenFlag, setFullScreenFlag] = useState<boolean>(false)

    UseEffectFullScreenChange(setFullScreenFlag) // 监听是否：全屏

    const [withdrawStatusDict, setWithdrawStatusDict] = useState<Map<number, ProSchemaValueEnumType>>() // 提现状态

    useEffect(() => {

        // 设置：用户提现状态的字典
        UpdateWithdrawStatusDict(setWithdrawStatusDict);

    }, [])

    // 改变：轮询状态
    function ChangePolling(changeFlag: boolean = true, stopFlag: boolean = true) {

        if (changeFlag) {

            if (polling) {
                setPolling(undefined);
                return;
            }

            setPolling(CommonConstant.POLLING_TIME);

        } else {

            if (stopFlag) {

                setPolling(undefined);

            } else {

                setPolling(CommonConstant.POLLING_TIME);

            }

        }

    }

    return (

        <>

            <ProTable<SysUserWalletWithdrawLogDO, SysUserWalletWithdrawLogPageDTO>

                scroll={{x: 'max-content'}}
                sticky={{offsetHeader: fullScreenFlag ? 0 : CommonConstant.NAV_TOP_HEIGHT}}
                actionRef={actionRef}
                rowKey={"id"}

                pagination={{
                    showQuickJumper: true,
                    showSizeChanger: true,
                }}

                headerTitle={
                    <span>上次更新时间：{FormatDateTime(lastUpdateTime)}</span>
                }

                polling={polling}

                columnEmptyText={false}

                columnsState={{
                    value: columnsStateMap,
                    onChange: setColumnsStateMap,
                }}

                rowSelection={{}}

                revalidateOnFocus={false}

                columns={[

                    ...UserWalletWithdrawLogTableBaseColumnArr(withdrawStatusDict, [

                            {
                                title: '租户',
                                dataIndex: 'tenantId',
                                ellipsis: true,
                                width: 90,
                                hideInSearch: true,
                                valueType: 'select',
                                request: () => {
                                    return GetDictList(SysTenantDictList)
                                }
                            },

                            {
                                title: '租户',
                                dataIndex: 'tenantIdSet',
                                ellipsis: true,
                                width: 90,
                                hideInTable: true,
                                order: 2000,
                                valueType: 'treeSelect',
                                fieldProps: {
                                    placeholder: '请选择',
                                    allowClear: true,
                                    treeNodeFilterProp: 'title',
                                    maxTagCount: 'responsive',
                                    treeCheckable: true,
                                    showCheckedStrategy: TreeSelect.SHOW_ALL,
                                    treeCheckStrictly: true,
                                },
                                request: () => {
                                    return NoFormGetDictTreeList(SysTenantDictList, true, '-1')
                                },
                                search: {
                                    transform: (valueArr: { label: string, value: string }[]) =>
                                        SearchTransform(valueArr, 'tenantIdSet')
                                }
                            },

                            {
                                title: '提现用户', dataIndex: 'userId', ellipsis: true, width: 90, valueType: 'select',
                                request: () => {
                                    return DoGetDictList(SysUserDictList({addAdminFlag: true}))
                                },
                                renderText: (text, record) => {
                                    return record.userId === CommonConstant.TENANT_USER_ID_STR ? SysUserTenantEnum.TENANT.name : text
                                },
                                fieldProps: {
                                    allowClear: true,
                                    showSearch: true,
                                },
                            },

                        ]
                    ),

                    {
                        title: '用户/租户',
                        dataIndex: 'sysUserTenantEnum',
                        valueEnum: SysUserTenantEnumDict,
                        width: 90,
                        hideInTable: true,
                    },

                    {

                        title: '操作',
                        dataIndex: 'option',
                        valueType: 'option',
                        width: 120,

                        render: (dom, entity: SysUserWalletWithdrawLogDO) => {

                            const optionArr: React.ReactNode[] = []

                            if (entity.withdrawStatus as any === SysUserWalletWithdrawStatusEnum.COMMIT.code) {

                                optionArr.push(<a key="1" className={"green2"} onClick={() => {

                                    ExecConfirm(() => {

                                        return SysUserWalletWithdrawLogAccept({idSet: [entity.id!]}).then(res => {

                                            ToastSuccess(res.msg)
                                            actionRef.current?.reload()

                                        })

                                    }, undefined, `确定受理【${entity.id}】吗？`)

                                }}>受理</a>)

                            }

                            optionArr.push(<a key="9" onClick={() => {

                                currentForm.current = {id: entity.id} as SysUserWalletWithdrawLogDO
                                ChangePolling(false, true) // 停止：轮询
                                setFormOpen(true)

                            }}>查看</a>,)

                            return optionArr

                        }

                    },

                ]}

                options={{
                    fullScreen: true,
                }}

                request={(params, sort, filter) => {

                    setLastUpdateTime(new Date())

                    return SysUserWalletWithdrawLogPage({...params, sort})

                }}

                toolbar={{

                    actions: [

                        <Button

                            key="1"
                            type="primary"

                            onClick={() => {
                                ChangePolling();
                            }}

                        >

                            {polling ? <LoadingOutlined/> : <ReloadOutlined/>}
                            {polling ? '停止轮询' : '开始轮询'}

                        </Button>,

                    ],

                }}

                tableAlertOptionRender={({selectedRowKeys, selectedRows, onCleanSelected}) => {

                    const dataArr = selectedRows as SysUserWalletWithdrawLogDO[];

                    const someFlag = dataArr.some(item => {

                        return item.withdrawStatus as any === SysUserWalletWithdrawStatusEnum.COMMIT.code

                    })

                    return (

                        <Space size={16}>

                            {

                                someFlag && <a className={"green2"} onClick={() => {

                                    ExecConfirm(() => {

                                        return SysUserWalletWithdrawLogAccept({idSet: selectedRowKeys as string[]}).then(res => {

                                            ToastSuccess(res.msg)
                                            actionRef.current?.reload()
                                            onCleanSelected()

                                        })

                                    }, undefined, <div className={"flex-c"}>

                                        <div>确定受理选中的【{selectedRowKeys.length}】项吗？</div>

                                        <Typography.Text className={"m-t-10"}
                                                         type="secondary">备注：已经受理或者无法受理的记录，不会进行处理</Typography.Text>

                                    </div>)

                                }}>批量受理</a>

                            }

                            <a onClick={onCleanSelected}>取消选择</a>

                        </Space>

                    )

                }}

            />

            <BetaSchemaForm<SysUserWalletWithdrawLogDO>

                title={currentForm.current.id ? "查看提现" : "新建提现"}
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

                params={new Date()} // 目的：为了打开页面时，执行 request方法

                request={async () => {

                    formRef.current?.resetFields()

                    if (currentForm.current.id) {

                        await SysUserWalletWithdrawLogInfoById({id: currentForm.current.id}).then(res => {

                            currentForm.current = res as SysUserWalletWithdrawLogDO

                            setTimeout(() => {

                                formRef.current?.setFieldsValue(currentForm.current)

                            }, CommonConstant.SHORT_DELAY)

                        })

                    }

                    return {}

                }}

                open={formOpen}
                onOpenChange={setFormOpen}
                columns={[

                    {
                        title: '提现编号',
                        dataIndex: 'id',
                        readonly: true,
                    },

                    ...GetUserWalletWithdrawFormColumnArr(),

                    {
                        title: '提现金额',
                        dataIndex: 'withdrawMoney',
                        valueType: 'money',
                        readonly: true,
                        fieldProps: {
                            precision: 2, // 小数点精度
                            className: "w100",
                        },

                    },

                ]}

                submitter={{

                    submitButtonProps: {style: {display: 'none',}},

                    render: (props, dom) => {

                        const resArr = [...dom]

                        if (currentForm.current.withdrawStatus as any === SysUserWalletWithdrawStatusEnum.ACCEPT.code) {

                            resArr.push(<Button key={"1"} type={"primary"} onClick={() => {

                                ExecConfirm(() => {

                                    return SysUserWalletWithdrawLogSuccess({id: currentForm.current.id!}).then(res => {

                                        ToastSuccess(res.msg)
                                        actionRef.current?.reload()
                                        setFormOpen(false)

                                    })

                                }, undefined, `确定成功【${currentForm.current.id}】吗？`)

                            }}>成功</Button>)

                            resArr.push(
                                <UserWalletWithdrawLogRejectModal

                                    key={"2"}
                                    setFormOpen={setFormOpen}
                                    currentForm={currentForm}
                                    actionRef={actionRef}

                                />
                            )

                        }

                        return resArr;

                    },

                }}

            />

        </>

    )

}

interface IUserWalletWithdrawLogRejectModal {

    setFormOpen: (value: (((prevState: boolean) => boolean) | boolean)) => void

    currentForm: MutableRefObject<SysUserWalletWithdrawLogDO>

    actionRef: MutableRefObject<ActionType | undefined>

}

// 拒绝用户提现
function UserWalletWithdrawLogRejectModal(props: IUserWalletWithdrawLogRejectModal) {

    const formRef = useRef<FormInstance<NotNullIdAndStringValue>>();

    return (

        <BetaSchemaForm<NotNullIdAndStringValue>

            trigger={<Button type={"primary"} danger={true}>拒绝</Button>}

            title={'拒绝原因'}
            layoutType={"ModalForm"}

            modalProps={{
                maskClosable: false,
                destroyOnClose: true,
            }}

            formRef={formRef}

            isKeyPressSubmit

            width={CommonConstant.MODAL_FORM_WIDTH}

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

                    ]

                },

            }}

            params={new Date()} // 目的：为了打开页面时，执行 request方法

            request={async () => {

                return {}

            }}

            columns={[

                {
                    title: '拒绝原因',
                    dataIndex: 'value',
                    valueType: 'textarea',
                    formItemProps: {
                        rules: [
                            {
                                whitespace: true,
                                max: 300,
                                required: true,
                            },
                        ],
                    },
                    fieldProps: {
                        showCount: true,
                        maxLength: 300,
                        allowClear: true,
                    }
                },

            ]}

            onFinish={async (form) => {

                await SysUserWalletWithdrawLogReject({
                    id: props.currentForm.current.id,
                    value: form.value
                }).then(res => {

                    ToastSuccess(res.msg)
                    props.actionRef.current?.reload()
                    props.setFormOpen(false)

                })

                return true

            }}

        />

    )

}
