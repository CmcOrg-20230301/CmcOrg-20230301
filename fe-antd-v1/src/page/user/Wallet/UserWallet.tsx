import {ProCard, ProTable, RouteContext, RouteContextType} from '@ant-design/pro-components';
import {Button, Modal, Space, Statistic} from 'antd';
import {CheckCircleOutlined} from "@ant-design/icons";
import React, {useState} from "react";
import {
    SysUserWalletLogDO,
    SysUserWalletLogPageUserSelf,
    SysUserWalletLogUserSelfPageDTO
} from "@/api/http/SysUserWalletLog";
import {UseEffectFullScreenChange} from "@/util/DocumentUtil";
import {
    SysUserWalletWithdrawLogDictListWithdrawStatus,
    SysUserWalletWithdrawLogDO,
    SysUserWalletWithdrawLogPageUserSelf,
    SysUserWalletWithdrawLogPageUserSelfDTO
} from "@/api/http/SysUserWalletWithdrawLog";
import {DoGetDictList} from "@/util/DictUtil";

const UserWalletLogModalTitle = "钱包日志"
const UserWalletWithdrawLogModalTitle = "提现记录"

// 用户钱包
export default function () {

    return (

        <>

            <RouteContext.Consumer>

                {(routeContextType: RouteContextType) => {

                    return (<>

                            <ProCard.Group

                                title={<div className={"f-20 fw-600"}>钱包</div>}
                                direction={routeContextType.isMobile ? 'column' : 'row'}
                                extra={<a>绑定银行卡</a>}

                                actions={

                                    <Space className={"m-t-20"} align={'center'} size={'large'}>

                                        <UserWalletLogModal/>

                                        <UserWalletWithdrawLogModal/>

                                        <Button type="primary" icon={<CheckCircleOutlined/>}>提现</Button>

                                    </Space>

                                }

                            >

                                <ProCard>
                                    <Statistic title="钱包余额（元）" value={79.0} precision={2}/>
                                </ProCard>

                                <ProCard.Divider type={routeContextType.isMobile ? 'horizontal' : 'vertical'}/>

                                <ProCard>
                                    <Statistic title="可提现（元）" value={112893.0} precision={2}/>
                                </ProCard>

                                <ProCard.Divider type={routeContextType.isMobile ? 'horizontal' : 'vertical'}/>

                                <ProCard>
                                    <Statistic title="不可提现（元）" value={112893.0} precision={2}/>
                                </ProCard>

                            </ProCard.Group>

                        </>

                    )

                }}

            </RouteContext.Consumer>

        </>

    )

}

// 钱包日志
function UserWalletLogModal() {

    const [open, setOpen] = useState(false);

    const [fullScreenFlag, setFullScreenFlag] = useState<boolean>(false)

    UseEffectFullScreenChange(setFullScreenFlag) // 监听是否：全屏

    return (

        <>

            <Button onClick={() => {
                setOpen(true)
            }}>{UserWalletLogModalTitle}</Button>

            <Modal

                width={1300}

                title={UserWalletLogModalTitle}

                onCancel={() => setOpen(false)}

                open={open}

                maskClosable={false}

                footer={false}

                className={"noFooterModal"}

            >

                <ProTable<SysUserWalletLogDO, SysUserWalletLogUserSelfPageDTO>

                    rowKey={"id"}

                    pagination={{
                        showQuickJumper: true,
                        showSizeChanger: true,
                    }}

                    columnEmptyText={false}

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
                            hideInSearch: true
                        },

                        {
                            title: '钱包余额（变）',
                            dataIndex: 'totalMoneyChange',
                            ellipsis: true,
                            width: 120,
                            hideInSearch: true
                        },

                        {
                            title: '钱包余额（后）',
                            dataIndex: 'totalMoneySuf',
                            ellipsis: true,
                            width: 120,
                            hideInSearch: true
                        },

                        {
                            title: '可提现（前）',
                            dataIndex: 'withdrawableMoneyPre',
                            ellipsis: true,
                            width: 120,
                            hideInSearch: true
                        },

                        {
                            title: '可提现（变）',
                            dataIndex: 'withdrawableMoneyChange',
                            ellipsis: true,
                            width: 120,
                            hideInSearch: true
                        },

                        {
                            title: '可提现（后）',
                            dataIndex: 'withdrawableMoneySuf',
                            ellipsis: true,
                            width: 120,
                            hideInSearch: true
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

                        return SysUserWalletLogPageUserSelf({...params, sort})

                    }}

                >

                </ProTable>

            </Modal>

        </>

    )

}

// 提现记录
function UserWalletWithdrawLogModal() {

    const [open, setOpen] = useState(false);

    const [fullScreenFlag, setFullScreenFlag] = useState<boolean>(false)

    UseEffectFullScreenChange(setFullScreenFlag) // 监听是否：全屏

    return (

        <>

            <Button onClick={() => {
                setOpen(true)
            }}>{UserWalletWithdrawLogModalTitle}</Button>

            <Modal

                width={1300}

                title={UserWalletWithdrawLogModalTitle}

                onCancel={() => setOpen(false)}

                open={open}

                maskClosable={false}

                footer={false}

                className={"noFooterModal"}

            >

                <ProTable<SysUserWalletWithdrawLogDO, SysUserWalletWithdrawLogPageUserSelfDTO>

                    rowKey={"id"}

                    pagination={{
                        showQuickJumper: true,
                        showSizeChanger: true,
                    }}

                    columnEmptyText={false}

                    revalidateOnFocus={false}

                    scroll={fullScreenFlag ? undefined : {y: 440}}

                    columns={[

                        {
                            title: '序号',
                            dataIndex: 'index',
                            valueType: 'index',
                            width: 90,
                        },

                        {title: '提现金额', dataIndex: 'withdrawMoney', valueType: 'money', ellipsis: true, width: 90,},

                        {title: '卡号', dataIndex: 'bankCardNo', ellipsis: true, width: 90,},

                        {title: '开户行', dataIndex: 'openBankName', ellipsis: true, width: 90,},

                        {title: '支行', dataIndex: 'branchBankName', ellipsis: true, width: 90,},

                        {title: '收款人姓名', dataIndex: 'payeeName', ellipsis: true, width: 90,},

                        {
                            title: '提现状态',
                            dataIndex: 'withdrawStatus',
                            ellipsis: true,
                            width: 90,
                            valueType: 'select',
                            request: () => {
                                return DoGetDictList(SysUserWalletWithdrawLogDictListWithdrawStatus())
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
                        },

                        {title: '拒绝理由', dataIndex: 'rejectReason', ellipsis: true, width: 90},

                    ]}

                    options={{
                        fullScreen: true,
                    }}

                    request={(params, sort, filter) => {

                        return SysUserWalletWithdrawLogPageUserSelf({...params, sort})

                    }}

                >

                </ProTable>

            </Modal>

        </>

    )

}
