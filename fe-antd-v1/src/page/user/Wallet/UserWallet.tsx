import {ProCard, ProSchemaValueEnumType, RouteContext, RouteContextType} from '@ant-design/pro-components';
import {Button, Statistic} from 'antd';
import {MoneyCollectOutlined, RollbackOutlined} from "@ant-design/icons";
import React, {useEffect, useState} from "react";
import {SysUserBankCardDO, SysUserBankCardInfoById, SysUserBankCardInfoByIdUserSelf} from "@/api/http/SysUserBankCard";
import {ToastSuccess} from "@/util/ToastUtil";
import {SysUserWalletDO, SysUserWalletInfoById, SysUserWalletInfoByIdUserSelf} from "@/api/http/SysUserWallet";
import {InDev} from "@/util/CommonUtil";
import {SysTenantBankCardInfoById} from "@/api/http/SysTenantBankCard";
import {SysTenantWalletInfoById} from "@/api/http/SysTenantWallet";
import PathConstant from "@/model/constant/PathConstant";
import {GoPage} from "@/layout/AdminLayout/AdminLayout";
import {UpdateWithdrawStatusDict} from "@/model/enum/SysUserWalletWithdrawStatusEnum";
import UserBankCardModal from "@/page/user/Wallet/UserBankCardModal";
import UserWalletLogModal from "@/page/user/Wallet/UserWalletLogModal";
import UserWalletWithdrawLogModal from "@/page/user/Wallet/UserWalletWithdrawLogModal";
import UserWalletWithdrawModal from "@/page/user/Wallet/UserWalletWithdrawModal";

export const UserWalletLogModalTitle = "钱包日志"
export const BindUserBankCardModalTitle = "绑定银行卡"
export const UpdateUserBankCardModalTitle = "修改银行卡"
export const UserWalletWithdrawLogModalTitle = "提现记录"
export const UserWalletWithdrawModalTitle = "提现"
export const UserWalletRechargeLogModalTitle = "充值记录"
export const UserWalletRechargeModalTitle = "充值"

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
