import React, {useState} from "react";
import {ColumnsState, ProTable} from "@ant-design/pro-components";
import {UseEffectFullScreenChange} from "@/util/UseEffectUtil";
import {Modal, Typography} from "antd";
import {
    SysUserWalletLogDO,
    SysUserWalletLogPage,
    SysUserWalletLogPageTenant,
    SysUserWalletLogPageUserSelf,
    SysUserWalletLogUserSelfPageDTO
} from "@/api/http/SysUserWalletLog";
import {GetTextType} from "@/util/StrUtil";
import {DoGetDictList} from "@/util/DictUtil";
import {SysUserDictList} from "@/api/http/SysUser";
import {UserWalletLogModalTitle} from "@/page/user/Wallet/UserWallet";

interface IUserWalletLogModal {

    tenantId?: string // 租户 id，备注：如果传递了，则表示是管理租户的钱包，备注：租户 id和用户 id只会传递一个

    userId?: string // 用户 id，备注：如果传递了，则表示是管理用户的钱包，备注：租户 id和用户 id只会传递一个

}

// 钱包日志
export default function (props: IUserWalletLogModal) {

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

                footer={null}

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
                            render: (dom, entity: SysUserWalletLogDO) => {

                                const type = GetTextType(entity.totalMoneyChange)

                                return <Typography.Text

                                    ellipsis={{tooltip: true}}
                                    type={type}
                                    style={{width: 120}}

                                >
                                    {dom}
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
                            render: (dom, entity: SysUserWalletLogDO) => {

                                const type = GetTextType(entity.withdrawableMoneyChange)

                                return <Typography.Text

                                    ellipsis={{tooltip: true}}
                                    type={type}
                                    style={{width: 120}}

                                >
                                    {dom}
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

                />

            </Modal>

        </>

    )

}
