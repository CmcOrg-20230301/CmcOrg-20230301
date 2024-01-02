import {RouteContext, RouteContextType} from "@ant-design/pro-components"
import {Card, Tabs} from "antd";
import React from "react";
import {SettingOutlined, UserOutlined} from "@ant-design/icons";
import UserSelfInfo from "@/page/user/Self/UserSelfInfo";
import UserSelfSetting from "@/page/user/Self/UserSelfSetting";
import {Tab} from "rc-tabs/lib/interface";

export const USER_CENTER_KEY_ONE = "个人资料"
export const USER_CENTER_KEY_TWO = "账号设置"

// 个人中心
export default function () {

    const itemArr: Tab[] = [

        {key: '2', icon: <SettingOutlined/>, label: USER_CENTER_KEY_TWO, children: <UserSelfSetting/>},
        {key: '1', icon: <UserOutlined/>, label: USER_CENTER_KEY_ONE, children: <UserSelfInfo/>},

    ];

    return (

        <RouteContext.Consumer>

            {(routeContextType: RouteContextType) => {

                return (

                    <div className={"flex-center p-b-50"}>

                        <Card style={{width: routeContextType.isMobile ? '100%' : '45%'}}>

                            <Tabs items={itemArr} tabPosition={routeContextType.isMobile ? 'top' : 'left'}/>

                        </Card>

                    </div>

                )

            }}

        </RouteContext.Consumer>

    )

}
