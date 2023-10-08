import {RouteContext, RouteContextType} from "@ant-design/pro-components"
import {Card, Tabs} from "antd";
import React from "react";
import {WalletOutlined} from "@ant-design/icons";
import UserWallet from "@/page/user/Wallet/UserWallet";

export const USER_WALLET_KEY_ONE = "钱包"

// 用户钱包
export default function () {

    const itemArr = [

        {key: '1', label: <span><WalletOutlined/>{USER_WALLET_KEY_ONE}</span>, children: <UserWallet/>},

    ];

    return (

        <RouteContext.Consumer>

            {(routeContextType: RouteContextType) => {

                return (

                    <div className={"flex-center p-b-50"}>

                        <Card>

                            <Tabs items={itemArr} tabPosition={routeContextType.isMobile ? 'top' : 'left'}/>

                        </Card>

                    </div>

                )

            }}

        </RouteContext.Consumer>

    )

}
