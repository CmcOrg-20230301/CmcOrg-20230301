import {
    ActionType,
    BetaSchemaForm,
    ColumnsState,
    FormInstance,
    ProCard,
    ProColumns,
    ProFormColumnsType,
    ProSchemaValueEnumType,
    ProTable,
    RouteContext,
    RouteContextType
} from '@ant-design/pro-components';
import {Button, Modal, Statistic, Typography} from 'antd';
import {CheckCircleOutlined, MoneyCollectOutlined, RollbackOutlined} from "@ant-design/icons";
import React, {useEffect, useRef, useState} from "react";
import {
    SysUserWalletLogDO,
    SysUserWalletLogPage,
    SysUserWalletLogPageTenant,
    SysUserWalletLogPageUserSelf,
    SysUserWalletLogUserSelfPageDTO
} from "@/api/http/SysUserWalletLog";
import {UseEffectFullScreenChange} from "@/util/DocumentUtil";
import {
    SysUserWalletWithdrawLogCancel,
    SysUserWalletWithdrawLogCancelTenant,
    SysUserWalletWithdrawLogCancelUserSelf,
    SysUserWalletWithdrawLogDictListWithdrawStatus,
    SysUserWalletWithdrawLogDO,
    SysUserWalletWithdrawLogInsertOrUpdate,
    SysUserWalletWithdrawLogInsertOrUpdateTenant,
    SysUserWalletWithdrawLogInsertOrUpdateUserSelf,
    SysUserWalletWithdrawLogInsertOrUpdateUserSelfDTO,
    SysUserWalletWithdrawLogPage,
    SysUserWalletWithdrawLogPageTenant,
    SysUserWalletWithdrawLogPageUserSelf,
    SysUserWalletWithdrawLogPageUserSelfDTO
} from "@/api/http/SysUserWalletWithdrawLog";
import {DoGetDictList, IEnum} from "@/util/DictUtil";
import {
    SysUserBankCardDictListOpenBankName,
    SysUserBankCardDO,
    SysUserBankCardInfoById,
    SysUserBankCardInfoByIdUserSelf,
    SysUserBankCardInsertOrUpdate,
    SysUserBankCardInsertOrUpdateUserSelf,
    SysUserBankCardInsertOrUpdateUserSelfDTO
} from "@/api/http/SysUserBankCard";
import {ExecConfirm, ToastError, ToastSuccess} from "@/util/ToastUtil";
import {SysUserWalletDO, SysUserWalletInfoById, SysUserWalletInfoByIdUserSelf} from "@/api/http/SysUserWallet";
import CommonConstant from "@/model/constant/CommonConstant";
import {GetTextType} from "@/util/StrUtil";
import {PresetStatusColorType} from "antd/es/_util/colors";
import {Validate} from "@/util/ValidatorUtil";
import {SysUserDictList} from "@/api/http/SysUser";
import {InDev} from "@/util/CommonUtil";
import {SysTenantBankCardInfoById, SysTenantBankCardInsertOrUpdateTenant} from "@/api/http/SysTenantBankCard";
import {SysTenantWalletInfoById} from "@/api/http/SysTenantWallet";
import PathConstant from "@/model/constant/PathConstant";
import {GoPage} from "@/layout/AdminLayout/AdminLayout";

const UserWalletLogModalTitle = "钱包日志"
const BindUserBankCardModalTitle = "绑定银行卡"
const UpdateUserBankCardModalTitle = "修改银行卡"
const UserWalletWithdrawLogModalTitle = "提现记录"
const UserWalletWithdrawModalTitle = "提现"
const UserWalletRechargeLogModalTitle = "充值记录"
const UserWalletRechargeModalTitle = "充值"

export interface ISysUserWalletWithdrawStatusEnum {

    COMMIT: IEnum,
    ACCEPT: IEnum,
    SUCCESS: IEnum,
    REJECT: IEnum,
    CANCEL: IEnum,

}

// 用户提现状态枚举类
export const SYS_USER_WALLET_WITHDRAW_STATUS_ENUM: ISysUserWalletWithdrawStatusEnum = {

    COMMIT: {
        code: 101,
        name: '待受理', // 待受理（可取消）
        status: 'warning',
    },

    ACCEPT: {
        code: 201,
        name: '受理中', // 受理中（不可取消）
        status: 'processing',
    },

    SUCCESS: {
        code: 301,
        name: '已成功', // 已成功
        status: 'success',
    },

    REJECT: {
        code: 401,
        name: '已拒绝', // 已拒绝（需要填写拒绝理由）
        status: 'error',
    },

    CANCEL: {
        code: 501,
        name: '已取消', // 已取消（用户在待受理的时候，可以取消）
        status: 'default',
    },

}

export const SYS_USER_WALLET_WITHDRAW_STATUS_MAP = new Map<number, PresetStatusColorType>();

Object.keys(SYS_USER_WALLET_WITHDRAW_STATUS_ENUM).forEach(key => {

    const item = SYS_USER_WALLET_WITHDRAW_STATUS_ENUM[key];

    SYS_USER_WALLET_WITHDRAW_STATUS_MAP.set(item.code as number, item.status!)

})

// 设置：用户提现状态的字典
export function UpdateWithdrawStatusDict(setWithdrawStatusDict: (value: (((prevState: (Map<number, ProSchemaValueEnumType> | undefined)) => (Map<number, ProSchemaValueEnumType> | undefined)) | Map<number, ProSchemaValueEnumType> | undefined)) => void) {

    SysUserWalletWithdrawLogDictListWithdrawStatus().then(res => {

        const dictMap = new Map<number, ProSchemaValueEnumType>();

        res.data?.map((it) => {

            dictMap.set(it.id!, {text: it.name, status: SYS_USER_WALLET_WITHDRAW_STATUS_MAP.get(it.id!)})

        })

        setWithdrawStatusDict(dictMap)

    })

}

interface IUserWallet {

    tenantId?: string // 租户 id，备注：如果传递了，则表示是管理租户的钱包，备注：租户 id和用户 id只会传递一个

    userId?: string // 用户 id，备注：如果传递了，则表示是管理用户的钱包，备注：租户 id和用户 id只会传递一个

}

// 用户钱包
export default function (props: IUserWallet) {

    const [sysUserWalletDO, setSysUserWalletDO] = useState<SysUserWalletDO>({} as SysUserWalletDO); // 用户钱包信息

    const [sysUserBankCardDO, setSysUserBankCardDO] = useState<SysUserBankCardDO>({} as SysUserBankCardDO); // 用户银行卡信息

    const [withdrawStatusDict, setWithdrawStatusDict] = useState<Map<number, ProSchemaValueEnumType>>() // 提现状态

    function UpdateSysUserBankCardDO() {

        if (props.tenantId) {

            SysTenantBankCardInfoById({value: props.tenantId}).then(res => {

                setSysUserBankCardDO(res || {})

            })

        } else if (props.userId) {

            SysUserBankCardInfoById({value: props.userId}).then(res => {

                setSysUserBankCardDO(res || {})

            })

        } else {

            SysUserBankCardInfoByIdUserSelf().then(res => {

                setSysUserBankCardDO(res || {})

            })

        }

    }

    function UpdateSysUserWalletDO(showMessage?: string) {

        if (props.tenantId) {

            SysTenantWalletInfoById({value: props.tenantId}).then(res => {

                setSysUserWalletDO(res)

                if (showMessage) {
                    ToastSuccess(showMessage)
                }

            })

        } else if (props.userId) {

            SysUserWalletInfoById({value: props.userId}).then(res => {

                setSysUserWalletDO(res)

                if (showMessage) {
                    ToastSuccess(showMessage)
                }

            })

        } else {

            SysUserWalletInfoByIdUserSelf().then(res => {

                setSysUserWalletDO(res)

                if (showMessage) {
                    ToastSuccess(showMessage)
                }

            })

        }

    }

    function Init(showMessage?: string) {

        UpdateSysUserBankCardDO()

        UpdateSysUserWalletDO(showMessage)

    }

    useEffect(() => {

        Init()

        // 设置：用户提现状态的字典
        UpdateWithdrawStatusDict(setWithdrawStatusDict);

    }, [])

    return (

        <>

            <RouteContext.Consumer>

                {(routeContextType: RouteContextType) => {

                    return (<>

                            <ProCard.Group

                                title={

                                    <div className={"flex ai-c"}>

                                        <div
                                            className={"f-20 fw-600"}>钱包
                                        </div>

                                        <UserWalletLogModal tenantId={props.tenantId} userId={props.userId}/>

                                        <a className={"m-l-20 f-14"} onClick={() => {

                                            Init('刷新成功')

                                        }}>刷新</a>

                                    </div>

                                }

                                direction={routeContextType.isMobile ? 'column' : 'row'}

                                extra={<UserBankCardModal

                                    sysUserBankCardDO={sysUserBankCardDO}
                                    UpdateSysUserBankCardDO={UpdateSysUserBankCardDO}
                                    tenantId={props.tenantId}
                                    userId={props.userId}

                                />}

                            >

                                <ProCard>

                                    <Statistic

                                        title={

                                            <div className={"flex"}>

                                                <div>钱包余额（元）</div>

                                                <a onClick={() => {
                                                    InDev()
                                                }}>{UserWalletRechargeLogModalTitle}</a>

                                            </div>

                                        }

                                        value={sysUserWalletDO.totalMoney}

                                        precision={2}

                                    />

                                </ProCard>

                                <ProCard.Divider type={routeContextType.isMobile ? 'horizontal' : 'vertical'}/>

                                <ProCard>

                                    <Statistic

                                        title={

                                            <div className={"flex"}>

                                                <div>可提现（元）</div>

                                                <UserWalletWithdrawLogModal

                                                    updateSysUserWalletDO={UpdateSysUserWalletDO}
                                                    withdrawStatusDict={withdrawStatusDict}
                                                    tenantId={props.tenantId}
                                                    userId={props.userId}

                                                />

                                            </div>

                                        }

                                        value={sysUserWalletDO.withdrawableMoney}

                                        precision={2}

                                    />

                                </ProCard>

                            </ProCard.Group>

                            <ProCard.Divider type={"horizontal"}/>

                            <div className={"flex-center m-t-20"}>

                                {

                                    (props.tenantId || props.userId) && <Button

                                        icon={<RollbackOutlined/>}
                                        onClick={() => {
                                            GoPage(props.tenantId ? PathConstant.SYS_TENANT_WALLET_PATH : PathConstant.SYS_USER_WALLET_PATH)
                                        }}

                                    >返回列表</Button>

                                }

                                <Button

                                    className={"m-l-20"}
                                    icon={<MoneyCollectOutlined/>}
                                    onClick={() => {
                                        InDev()
                                    }}

                                >{UserWalletRechargeModalTitle}</Button>

                                <UserWalletWithdrawModal

                                    sysUserBankCardDO={sysUserBankCardDO}
                                    sysUserWalletDO={sysUserWalletDO}
                                    UpdateSysUserWalletDO={UpdateSysUserWalletDO}
                                    tenantId={props.tenantId}
                                    userId={props.userId}

                                />

                            </div>

                        </>

                    )

                }}

            </RouteContext.Consumer>

        </>

    )

}

/**
 * 用户银行卡基础 form字段集合
 */
export const UserBankCardFormBaseColumnArr: ProFormColumnsType<SysUserBankCardInsertOrUpdateUserSelfDTO>[] = [

    {
        title: '银行卡号',
        dataIndex: 'bankCardNo',
        formItemProps: {
            rules: [
                {
                    required: true,
                    whitespace: true,
                    validator: Validate.bankDebitCard.validator
                },
            ],
        },
    },

    {
        title: '开户行',
        dataIndex: 'openBankName',
        fieldProps: {
            allowClear: true,
            showSearch: true,
        },
        formItemProps: {
            rules: [
                {
                    required: true,
                },
            ],
        },
        valueType: 'select',
        request: () => {
            return DoGetDictList(SysUserBankCardDictListOpenBankName())
        },
    },

    {
        title: '支行',
        dataIndex: 'branchBankName',
        formItemProps: {
            rules: [
                {
                    required: true,
                    whitespace: true,
                },
            ],
        },
        tooltip: '请正确填写，填写错误将导致打款失败'
    },

    {
        title: '收款人姓名',
        dataIndex: 'payeeName',
        formItemProps: {
            rules: [
                {
                    required: true,
                    whitespace: true,
                },
            ],
        },
    },

]

interface IUserBankCardModal {

    sysUserBankCardDO: SysUserBankCardDO

    UpdateSysUserBankCardDO: () => void

    tenantId?: string // 租户 id，备注：如果传递了，则表示是管理租户的钱包，备注：租户 id和用户 id只会传递一个

    userId?: string // 用户 id，备注：如果传递了，则表示是管理用户的钱包，备注：租户 id和用户 id只会传递一个

}

// 用户银行卡
function UserBankCardModal(props: IUserBankCardModal) {

    const formRef = useRef<FormInstance<SysUserBankCardInsertOrUpdateUserSelfDTO>>();

    const currentForm = useRef<SysUserBankCardInsertOrUpdateUserSelfDTO>({} as SysUserBankCardInsertOrUpdateUserSelfDTO)

    return (

        <BetaSchemaForm<SysUserBankCardInsertOrUpdateUserSelfDTO>

            trigger={
                <a>{props.sysUserBankCardDO.bankCardNo ? UpdateUserBankCardModalTitle : BindUserBankCardModalTitle}</a>}

            title={props.sysUserBankCardDO.bankCardNo ? UpdateUserBankCardModalTitle : BindUserBankCardModalTitle}
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
                destroyOnClose: true,
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

                    ]

                },

            }}

            params={new Date()} // 目的：为了打开页面时，执行 request方法

            request={async () => {

                formRef.current?.resetFields()

                if (props.tenantId) {

                    SysTenantBankCardInfoById({value: props.tenantId}).then(res => {

                        currentForm.current = res as SysUserBankCardInsertOrUpdateUserSelfDTO

                        formRef.current?.setFieldsValue(currentForm.current)

                    })

                } else if (props.userId) {

                    SysUserBankCardInfoById({value: props.userId}).then(res => {

                        currentForm.current = res as SysUserBankCardInsertOrUpdateUserSelfDTO

                        formRef.current?.setFieldsValue(currentForm.current)

                    })

                } else {

                    SysUserBankCardInfoByIdUserSelf().then(res => {

                        currentForm.current = res as SysUserBankCardInsertOrUpdateUserSelfDTO

                        formRef.current?.setFieldsValue(currentForm.current)

                    })

                }

                return {}

            }}

            columns={

                UserBankCardFormBaseColumnArr

            }

            onFinish={async (form) => {

                if (props.tenantId) {

                    await SysTenantBankCardInsertOrUpdateTenant({
                        ...currentForm.current, ...form,
                        tenantId: props.tenantId
                    }).then(res => {

                        ToastSuccess(res.msg)

                        props.UpdateSysUserBankCardDO() // 更新：银行卡信息

                    })

                } else if (props.userId) {

                    await SysUserBankCardInsertOrUpdate({
                        ...currentForm.current, ...form,
                        id: props.userId
                    }).then(res => {

                        ToastSuccess(res.msg)

                        props.UpdateSysUserBankCardDO() // 更新：银行卡信息

                    })

                } else {

                    await SysUserBankCardInsertOrUpdateUserSelf({...currentForm.current, ...form}).then(res => {

                        ToastSuccess(res.msg)

                        props.UpdateSysUserBankCardDO() // 更新：银行卡信息

                    })

                }

                return true

            }}

        />

    )

}

interface IUserWalletLogModal {

    tenantId?: string // 租户 id，备注：如果传递了，则表示是管理租户的钱包，备注：租户 id和用户 id只会传递一个

    userId?: string // 用户 id，备注：如果传递了，则表示是管理用户的钱包，备注：租户 id和用户 id只会传递一个

}

// 钱包日志
function UserWalletLogModal(props: IUserWalletLogModal) {

    const [columnsStateMap, setColumnsStateMap] = useState<Record<string, ColumnsState>>();

    const [open, setOpen] = useState(false);

    const [fullScreenFlag, setFullScreenFlag] = useState<boolean>(false)

    UseEffectFullScreenChange(setFullScreenFlag) // 监听是否：全屏

    return (

        <>

            <a className={"m-l-20 f-14"} onClick={() => {
                setOpen(true)
            }}>{UserWalletLogModalTitle}</a>

            <Modal

                width={1300}

                title={UserWalletLogModalTitle}

                onCancel={() => setOpen(false)}

                open={open}

                maskClosable={false}

                footer={false}

                className={"noFooterModal"}

                destroyOnClose={true}

            >

                <ProTable<SysUserWalletLogDO, SysUserWalletLogUserSelfPageDTO>

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

                    revalidateOnFocus={false}

                    scroll={fullScreenFlag ? undefined : {y: 440}}

                    columns={[

                        {
                            title: '序号',
                            dataIndex: 'index',
                            valueType: 'index',
                            width: 90,
                        },

                        {title: '日志名称', dataIndex: 'name', ellipsis: true, width: 90,},

                        {
                            title: '钱包余额（前）',
                            dataIndex: 'totalMoneyPre',
                            ellipsis: true,
                            width: 120,
                            hideInSearch: true,
                            valueType: 'money',
                            fieldProps: {
                                precision: 2, // 小数点精度
                            },
                        },

                        {
                            title: '钱包余额（变）',
                            dataIndex: 'totalMoneyChange',
                            width: 120,
                            hideInSearch: true,
                            valueType: 'money',
                            fieldProps: {
                                precision: 2, // 小数点精度
                            },
                            render: (text, entity: SysUserWalletLogDO) => {

                                const type = GetTextType(entity.totalMoneyChange)

                                return <Typography.Text

                                    ellipsis={{tooltip: true}}
                                    type={type}
                                    style={{width: 120}}

                                >
                                    {text}
                                </Typography.Text>

                            }
                        },

                        {
                            title: '钱包余额（后）',
                            dataIndex: 'totalMoneySuf',
                            ellipsis: true,
                            width: 120,
                            hideInSearch: true,
                            valueType: 'money',
                            fieldProps: {
                                precision: 2, // 小数点精度
                            },
                        },

                        {
                            title: '可提现（前）',
                            dataIndex: 'withdrawableMoneyPre',
                            ellipsis: true,
                            width: 120,
                            hideInSearch: true,
                            valueType: 'money',
                            fieldProps: {
                                precision: 2, // 小数点精度
                            },
                        },

                        {
                            title: '可提现（变）',
                            dataIndex: 'withdrawableMoneyChange',
                            width: 120,
                            hideInSearch: true,
                            valueType: 'money',
                            fieldProps: {
                                precision: 2, // 小数点精度
                            },
                            render: (text, entity: SysUserWalletLogDO) => {

                                const type = GetTextType(entity.withdrawableMoneyChange)

                                return <Typography.Text

                                    ellipsis={{tooltip: true}}
                                    type={type}
                                    style={{width: 120}}

                                >
                                    {text}
                                </Typography.Text>

                            }
                        },

                        {
                            title: '可提现（后）',
                            dataIndex: 'withdrawableMoneySuf',
                            ellipsis: true,
                            width: 120,
                            hideInSearch: true,
                            valueType: 'money',
                            fieldProps: {
                                precision: 2, // 小数点精度
                            },
                        },

                        {
                            title: '创建人', dataIndex: 'createId', ellipsis: true, width: 90, valueType: 'select',
                            hideInSearch: true,
                            request: () => {
                                return DoGetDictList(SysUserDictList({addAdminFlag: true}))
                            },
                            fieldProps: {
                                allowClear: true,
                                showSearch: true,
                            },
                        },

                        {
                            title: '创建时间',
                            dataIndex: 'createTime',
                            hideInSearch: true,
                            valueType: 'fromNow',
                            width: 90,
                            sorter: true,
                            defaultSortOrder: 'descend',
                        },

                        {
                            title: '创建时间',
                            dataIndex: 'createTimeRange',
                            hideInTable: true,
                            valueType: 'dateTimeRange',
                            search: {

                                transform: (value) => {

                                    return {

                                        ctBeginTime: value[0],
                                        ctEndTime: value[1],

                                    } as SysUserWalletLogUserSelfPageDTO

                                }

                            }
                        },

                        {title: '备注', dataIndex: 'remark', ellipsis: true, width: 90},

                    ]}

                    options={{
                        fullScreen: true,
                    }}

                    request={(params, sort, filter) => {

                        if (props.tenantId) {

                            return SysUserWalletLogPageTenant({...params, sort, tenantIdSet: [props.tenantId]})

                        } else if (props.userId) {

                            return SysUserWalletLogPage({...params, sort, userId: props.userId})

                        } else {

                            return SysUserWalletLogPageUserSelf({...params, sort})

                        }

                    }}

                >

                </ProTable>

            </Modal>

        </>

    )

}

interface IUserWalletWithdrawLogModal {

    updateSysUserWalletDO: () => void

    withdrawStatusDict?: Map<number, ProSchemaValueEnumType>

    tenantId?: string // 租户 id，备注：如果传递了，则表示是管理租户的钱包，备注：租户 id和用户 id只会传递一个

    userId?: string // 用户 id，备注：如果传递了，则表示是管理用户的钱包，备注：租户 id和用户 id只会传递一个

}

// 提现记录
function UserWalletWithdrawLogModal(props: IUserWalletWithdrawLogModal) {

    const [columnsStateMap, setColumnsStateMap] = useState<Record<string, ColumnsState>>();

    const actionRef = useRef<ActionType>()

    const [open, setOpen] = useState(false);

    const [fullScreenFlag, setFullScreenFlag] = useState<boolean>(false)

    UseEffectFullScreenChange(setFullScreenFlag) // 监听是否：全屏

    return (

        <>

            <a onClick={() => {
                setOpen(true)
            }}>{UserWalletWithdrawLogModalTitle}</a>

            <Modal

                width={1300}

                title={UserWalletWithdrawLogModalTitle}

                onCancel={() => setOpen(false)}

                open={open}

                maskClosable={false}

                footer={false}

                className={"noFooterModal"}

                destroyOnClose={true}

            >

                <ProTable<SysUserWalletWithdrawLogDO, SysUserWalletWithdrawLogPageUserSelfDTO>

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

                    revalidateOnFocus={false}

                    scroll={fullScreenFlag ? undefined : {y: 440}}

                    columns={[

                        ...UserWalletWithdrawLogTableBaseColumnArr(props.withdrawStatusDict),

                        {

                            title: '操作',
                            dataIndex: 'option',
                            valueType: 'option',
                            width: 90,

                            render: (dom, entity: SysUserWalletWithdrawLogDO) => entity.withdrawStatus as any === SYS_USER_WALLET_WITHDRAW_STATUS_ENUM.COMMIT.code ? [

                                <a key="1" className={"red3"} onClick={() => {

                                    ExecConfirm(() => {

                                        if (props.tenantId) {

                                            return SysUserWalletWithdrawLogCancelTenant({id: entity.id!}).then(res => {

                                                ToastSuccess(res.msg)
                                                actionRef.current?.reload()

                                                props.updateSysUserWalletDO() // 更新钱包的钱

                                            })

                                        } else if (props.userId) {

                                            return SysUserWalletWithdrawLogCancel({id: entity.id!}).then(res => {

                                                ToastSuccess(res.msg)
                                                actionRef.current?.reload()

                                                props.updateSysUserWalletDO() // 更新钱包的钱

                                            })

                                        } else {

                                            return SysUserWalletWithdrawLogCancelUserSelf({id: entity.id!}).then(res => {

                                                ToastSuccess(res.msg)
                                                actionRef.current?.reload()

                                                props.updateSysUserWalletDO() // 更新钱包的钱

                                            })

                                        }

                                    }, undefined, `确定取消【${entity.id}】吗？`)

                                }}>取消</a>,

                            ] : [],

                        },

                    ]}

                    options={{
                        fullScreen: true,
                    }}

                    request={(params, sort, filter) => {

                        if (props.tenantId) {

                            return SysUserWalletWithdrawLogPageTenant({...params, sort, tenantIdSet: [props.tenantId]})

                        } else if (props.userId) {

                            return SysUserWalletWithdrawLogPage({...params, sort, userId: props.userId})

                        } else {

                            return SysUserWalletWithdrawLogPageUserSelf({...params, sort})

                        }

                    }}

                >

                </ProTable>

            </Modal>

        </>

    )

}

/**
 * 获取：用户提现记录的 table基础字段集合
 */
export const UserWalletWithdrawLogTableBaseColumnArr = (withdrawStatusDict?: Map<number, ProSchemaValueEnumType>, otherProColumnArr: ProColumns<SysUserWalletWithdrawLogDO>[] = []): ProColumns<SysUserWalletWithdrawLogDO>[] => {

    return [

        {
            title: '序号',
            dataIndex: 'index',
            valueType: 'index',
            width: 90,
        },

        ...otherProColumnArr,

        {
            title: '提现编号',
            dataIndex: 'id',
            ellipsis: true,
            width: 90,
            order: 1000,
            copyable: true,
            render: (text) => {
                return <Typography.Text ellipsis={{tooltip: true}} style={{width: 90}}>{text}</Typography.Text>
            }
        },

        {
            title: '提现金额',
            dataIndex: 'withdrawMoney',
            valueType: 'money',
            ellipsis: true,
            width: 90,
            sorter: true,
            hideInSearch: true,
            fieldProps: {
                precision: 2, // 小数点精度
            },
        },

        {
            title: '提现金额',
            dataIndex: 'withdrawMoneyRange',
            hideInTable: true,
            valueType: 'digitRange',
            search: {

                transform: (value) => {

                    return {

                        beginWithdrawMoney: value[0],
                        endWithdrawMoney: value[1],

                    } as SysUserWalletWithdrawLogPageUserSelfDTO

                }

            }
        },

        {title: '卡号', dataIndex: 'bankCardNo', ellipsis: true, width: 90, order: 800,},

        {
            title: '开户行', dataIndex: 'openBankName', ellipsis: true, width: 90,
            fieldProps: {
                allowClear: true,
                showSearch: true,
            },
            valueType: 'select',
            request: () => {
                return DoGetDictList(SysUserBankCardDictListOpenBankName())
            },
        },

        {title: '支行', dataIndex: 'branchBankName', ellipsis: true, width: 90,},

        {title: '收款人姓名', dataIndex: 'payeeName', ellipsis: true, width: 90,},

        {
            title: '提现状态',
            dataIndex: 'withdrawStatus',
            ellipsis: true,
            width: 90,
            valueType: 'select',
            valueEnum: withdrawStatusDict,
            order: 900,
        },

        {
            title: '创建时间',
            dataIndex: 'createTime',
            hideInSearch: true,
            valueType: 'fromNow',
            width: 90,
            sorter: true,
        },

        {
            title: '创建时间',
            dataIndex: 'createTimeRange',
            hideInTable: true,
            valueType: 'dateTimeRange',
            search: {

                transform: (value) => {

                    return {

                        ctBeginTime: value[0],
                        ctEndTime: value[1],

                    } as SysUserWalletWithdrawLogPageUserSelfDTO

                }

            }
        },

        {
            title: '更新时间',
            dataIndex: 'updateTime',
            hideInSearch: true,
            valueType: 'fromNow',
            width: 90,
            sorter: true,
            defaultSortOrder: 'descend',
        },

        {
            title: '拒绝理由', dataIndex: 'rejectReason', ellipsis: true, width: 120, render: (text) => {
                return <Typography.Text ellipsis={{tooltip: true}} style={{width: 120}}>{text}</Typography.Text>
            }
        },

    ]

}

interface IUserWalletWithdrawModal {

    sysUserBankCardDO: SysUserBankCardDO

    sysUserWalletDO: SysUserWalletDO

    UpdateSysUserWalletDO: () => void

    tenantId?: string // 租户 id，备注：如果传递了，则表示是管理租户的钱包，备注：租户 id和用户 id只会传递一个

    userId?: string // 用户 id，备注：如果传递了，则表示是管理用户的钱包，备注：租户 id和用户 id只会传递一个

}

// 提现
function UserWalletWithdrawModal(props: IUserWalletWithdrawModal) {

    const formRef = useRef<FormInstance<SysUserWalletWithdrawLogInsertOrUpdateUserSelfDTO>>();

    const [formOpen, setFormOpen] = useState<boolean>(false);

    const currentForm = useRef<SysUserWalletWithdrawLogInsertOrUpdateUserSelfDTO>({} as SysUserWalletWithdrawLogInsertOrUpdateUserSelfDTO)

    return (

        <>

            <Button

                type="primary"
                icon={<CheckCircleOutlined/>}
                className={"m-l-20"}
                onClick={() => {

                    if (!props.sysUserWalletDO.withdrawableMoney) {

                        ToastError('操作失败：可提现余额不足')
                        return

                    }

                    if (!props.sysUserBankCardDO.bankCardNo) {

                        ToastError('操作失败：请先绑定银行卡')
                        return

                    }

                    setFormOpen(true)

                }}

            >{UserWalletWithdrawModalTitle}</Button>

            <BetaSchemaForm<SysUserWalletWithdrawLogInsertOrUpdateUserSelfDTO>

                title={UserWalletWithdrawModalTitle}
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

                        ]

                    },

                }}

                params={new Date()} // 目的：为了打开页面时，执行 request方法

                request={async () => {

                    formRef.current?.resetFields()

                    setTimeout(() => {

                        // @ts-ignore
                        currentForm.current.bankCardNo = props.sysUserBankCardDO.bankCardNo
                        // @ts-ignore
                        currentForm.current.openBankName = props.sysUserBankCardDO.openBankName
                        // @ts-ignore
                        currentForm.current.branchBankName = props.sysUserBankCardDO.branchBankName
                        // @ts-ignore
                        currentForm.current.payeeName = props.sysUserBankCardDO.payeeName

                        formRef.current?.setFieldsValue(currentForm.current)

                    }, CommonConstant.SHORT_DELAY)

                    return {}

                }}

                open={formOpen}
                onOpenChange={setFormOpen}

                columns={[

                    ...GetUserWalletWithdrawFormColumnArr(),

                    {
                        title: '提现金额',
                        dataIndex: 'withdrawMoney',
                        valueType: 'digit',

                        fieldProps: {
                            precision: 2, // 小数点精度
                            className: "w100",
                            addonAfter: <div>{props.sysUserWalletDO.withdrawableMoney}</div>,
                            autoFocus: true,
                        },

                        formItemProps: {
                            rules: [
                                {
                                    required: true,
                                    min: 1,
                                    max: props.sysUserWalletDO.withdrawableMoney,
                                    type: "number",
                                }
                            ]
                        }

                    },

                ]}

                onFinish={async (form) => {

                    if (props.tenantId) {

                        await SysUserWalletWithdrawLogInsertOrUpdateTenant({
                            ...currentForm.current, ...form,
                            tenantId: props.tenantId
                        }).then(res => {

                            ToastSuccess(res.msg)

                            props.UpdateSysUserWalletDO() // 更新钱包的钱

                        })

                    } else if (props.userId) {

                        await SysUserWalletWithdrawLogInsertOrUpdate({
                            ...currentForm.current, ...form,
                            userId: props.userId
                        }).then(res => {

                            ToastSuccess(res.msg)

                            props.UpdateSysUserWalletDO() // 更新钱包的钱

                        })

                    } else {

                        await SysUserWalletWithdrawLogInsertOrUpdateUserSelf({...currentForm.current, ...form,}).then(res => {

                            ToastSuccess(res.msg)

                            props.UpdateSysUserWalletDO() // 更新钱包的钱

                        })

                    }

                    return true

                }}

            />

        </>

    )

}

/**
 * 获取：用户提现记录的 form基础字段集合
 */
export function GetUserWalletWithdrawFormColumnArr() {

    return [

        {
            title: '银行卡号',
            dataIndex: 'bankCardNo',
            readonly: true,
        },

        {
            title: '开户行',
            dataIndex: 'openBankName',
            readonly: true,
        },

        {
            title: '支行',
            dataIndex: 'branchBankName',
            readonly: true,
        },

        {
            title: '收款人姓名',
            dataIndex: 'payeeName',
            readonly: true,
        },

    ]

}
