import {PageContainer, RouteContext, RouteContextType} from "@ant-design/pro-components"
import {Card, Tabs} from "antd";
import React, {useEffect, useState} from "react";
import {WalletOutlined} from "@ant-design/icons";
import UserWallet from "@/page/user/Wallet/UserWallet";
import {Navigate, useLocation} from "react-router-dom";
import PathConstant from "@/model/constant/PathConstant";
import LocalStorageKey from "@/model/constant/LocalStorageKey";
import {SysTenantGetNameById} from "@/api/http/SysTenant";
import CommonConstant from "@/model/constant/CommonConstant";
import {DoGetDictList, GetByValueFromDictList} from "@/util/DictUtil";
import {SysUserDictList} from "@/api/http/SysUser";
import {SysUserTenantEnum} from "@/model/enum/SysUserTenantEnum";
import {MyLocalStorage} from "@/util/StorageUtil.ts";

export const USER_WALLET_KEY_ONE = "钱包"

export interface IUserWalletOwnerInfo {

    id?: string

    type?: 1 | 2 // 1 用户 2 租户，请使用：SysUserWalletWithdrawLogTypeEnum 里面的 code属性

    goBackUri?: string // 返回列表时，需要返回的 uri

}

// 用户钱包
export default function () {

    let location = useLocation();

    const [ownerName, setOwnerName] = useState<string>(""); // 租户名 或者 用户名

    if (location.pathname === PathConstant.SYS_WALLET_MANAGE_PATH) {

        if (!location.state?.id) {

            // 获取：历史值
            const ownerInfo: IUserWalletOwnerInfo = JSON.parse(MyLocalStorage.getItem(LocalStorageKey.USER_WALLET_OWNER_INFO) || '{}');

            if (ownerInfo.id) {

                location.state = ownerInfo

            } else {

                return <Navigate to={PathConstant.SYS_USER_WALLET_PATH}/>

            }

        }

    }

    useEffect(() => {

        if (location.state?.id) {

            MyLocalStorage.setItem(LocalStorageKey.USER_WALLET_OWNER_INFO, JSON.stringify(location.state)) // 存储起来下次使用

            if (location.state.type === SysUserTenantEnum.TENANT.code) {

                SysTenantGetNameById({value: location.state.id}).then(res => {

                    if (res.data != null) {

                        setOwnerName(res.data === "" ? CommonConstant.DEFAULT_TENANT_NAME : res.data)

                    } else {

                        setOwnerName("")

                    }

                })

            } else {

                DoGetDictList(SysUserDictList({addAdminFlag: true})).then(res => {

                    setOwnerName(GetByValueFromDictList(res, location.state.id))

                })

            }

        }

    }, [])

    return (

        <RouteContext.Consumer>

            {(routeContextType: RouteContextType) => {

                function getElement() {

                    let tenantId
                    let userId

                    if (location.state?.id) {

                        if (location.state?.type === SysUserTenantEnum.TENANT.code) {

                            tenantId = location.state?.id

                        } else {

                            userId = location.state?.id

                        }

                    }

                    return <div className={"flex-center p-b-50"}>

                        <Card style={{width: routeContextType.isMobile ? '100%' : '45%'}}>

                            <Tabs items={[

                                {
                                    key: '1',
                                    icon: <WalletOutlined/>,
                                    label: USER_WALLET_KEY_ONE,
                                    children:

                                        <UserWallet

                                            tenantId={tenantId}
                                            userId={userId}
                                            goBackUri={location.state?.goBackUri}

                                        />

                                },

                            ]} tabPosition={routeContextType.isMobile ? 'top' : 'left'}/>

                        </Card>

                    </div>;

                }

                let preName;

                if (location.state?.id) {

                    preName = location.state?.type === SysUserTenantEnum.TENANT.code ? SysUserTenantEnum.TENANT.name : SysUserTenantEnum.USER.name;

                }

                return (

                    <>

                        {

                            location.state?.id ?

                                <PageContainer title={preName + routeContextType.currentMenu!.name + "：" + ownerName}>

                                    {getElement()}

                                </PageContainer>

                                :

                                getElement()

                        }

                    </>

                )

            }}

        </RouteContext.Consumer>

    )

}
