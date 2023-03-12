import {RouteContext, RouteContextType} from "@ant-design/pro-components"
import {Card, Tabs} from "antd";
import React from "react";
import {SettingOutlined, UserOutlined} from "@ant-design/icons/lib";
import UserSelfInfo from "@/page/user/Self/UserSelfInfo";
import UserSelfSetting from "@/page/user/Self/UserSelfSetting";

export const USER_CENTER_KEY_ONE = "个人资料"
export const USER_CENTER_KEY_TWO = "账号设置"

// 个人中心
export default function () {

    const itemArr = [

        {key: '2', label: <span><SettingOutlined/>{USER_CENTER_KEY_TWO}</span>, children: <UserSelfSetting/>},
        {key: '1', label: <span><UserOutlined/>{USER_CENTER_KEY_ONE}</span>, children: <UserSelfInfo/>},

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
