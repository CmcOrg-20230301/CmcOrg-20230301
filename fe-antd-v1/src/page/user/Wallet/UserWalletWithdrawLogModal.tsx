import {ActionType, ColumnsState, ProColumns, ProSchemaValueEnumType, ProTable} from "@ant-design/pro-components";
import React, {useRef, useState} from "react";
import {UseEffectFullScreenChange} from "@/util/UseEffectUtil";
import {Modal, Typography} from "antd";
import {
    SysUserWalletWithdrawLogCancel,
    SysUserWalletWithdrawLogCancelTenant,
    SysUserWalletWithdrawLogCancelUserSelf,
    SysUserWalletWithdrawLogDO,
    SysUserWalletWithdrawLogPage,
    SysUserWalletWithdrawLogPageTenant,
    SysUserWalletWithdrawLogPageUserSelf,
    SysUserWalletWithdrawLogPageUserSelfDTO
} from "@/api/http/SysUserWalletWithdrawLog";
import {SysUserWalletWithdrawStatusEnum} from "@/model/enum/SysUserWalletWithdrawStatusEnum";
import {ExecConfirm, ToastSuccess} from "@/util/ToastUtil";
import {DoGetDictList} from "@/util/DictUtil";
import {SysUserBankCardDictListOpenBankName} from "@/api/http/SysUserBankCard";
import {UserWalletWithdrawLogModalTitle} from "@/page/user/Wallet/UserWallet";

interface IUserWalletWithdrawLogModal {

    updateSysUserWalletDO: () => void

    withdrawStatusDict?: Map<number, ProSchemaValueEnumType>

    tenantId?: string // 租户 id，备注：如果传递了，则表示是管理租户的钱包，备注：租户 id和用户 id只会传递一个

    userId?: string // 用户 id，备注：如果传递了，则表示是管理用户的钱包，备注：租户 id和用户 id只会传递一个

}

// 提现记录
export default function (props: IUserWalletWithdrawLogModal) {

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

                footer={null}

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
                            width: 120,

                            render: (dom, entity: SysUserWalletWithdrawLogDO) => entity.withdrawStatus as any === SysUserWalletWithdrawStatusEnum.COMMIT.code ? [

                                <a key="1" className={"red3"} onClick={() => {

                                    ExecConfirm(async () => {

                                        if (props.tenantId) {

                                            await SysUserWalletWithdrawLogCancelTenant({id: entity.id!}).then(res => {

                                                ToastSuccess(res.msg)
                                                actionRef.current?.reload()

                                                props.updateSysUserWalletDO() // 更新钱包的钱

                                            })

                                        } else if (props.userId) {

                                            await SysUserWalletWithdrawLogCancel({id: entity.id!}).then(res => {

                                                ToastSuccess(res.msg)
                                                actionRef.current?.reload()

                                                props.updateSysUserWalletDO() // 更新钱包的钱

                                            })

                                        } else {

                                            await SysUserWalletWithdrawLogCancelUserSelf({id: entity.id!}).then(res => {

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

                            return SysUserWalletWithdrawLogPageTenant({
                                ...params,
                                sort,
                                tenantIdSet: [props.tenantId]
                            })

                        } else if (props.userId) {

                            return SysUserWalletWithdrawLogPage({...params, sort, userId: props.userId})

                        } else {

                            return SysUserWalletWithdrawLogPageUserSelf({...params, sort})

                        }

                    }}

                />

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
            render: (dom) => {
                return <Typography.Text ellipsis={{tooltip: true}}
                                        style={{width: 90}}>{dom}</Typography.Text>
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
            title: '拒绝理由', dataIndex: 'rejectReason', ellipsis: true, width: 120, render: (dom) => {
                return <Typography.Text ellipsis={{tooltip: true}}
                                        style={{width: 120}}>{dom}</Typography.Text>
            }
        },

    ]

}
