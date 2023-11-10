import {BrowserRouter, NavigateFunction, Route, Routes, useNavigate} from "react-router-dom";
import React, {useMemo} from "react";
import NoLoginRouterList from "@/router/NoLoginRouterList";
import RouterMap, {IManualRouterItem, ManualRouterName, RouterMapKeyList} from "@/router/RouterMap";
import {AppDispatch, useAppDispatch, useAppSelector} from "@/store";
import {App} from "antd";
import {useAppProps} from "antd/es/app/context";
import {UserSelfInfoVO} from "@/api/http/UserSelf";

// MyApp
export default function () {

    const userSelfMenuList = useAppSelector(
        (state) => state.user.userSelfMenuList
    ).filter((item) => item.router)

    const layoutRouterArr = useMemo<IManualRouterItem[]>(() => {

        return [ManualRouterName.AdminLayout, ManualRouterName.BlankLayout]

    }, []);

    return (

        <BrowserRouter>

            <Routes>

                {NoLoginRouterList.map((item, index) => (

                    <Route
                        key={index}
                        path={item.path}
                        element={
                            <LoadElement elementStr={item.elementStr}/>
                        }
                    />

                ))}

                {

                    layoutRouterArr.map((item, index) => {

                        return item.path &&

                            <Route

                                key={index}
                                path={item.path}

                                element={<LoadElement elementStr={item.name}/>}

                            >

                                {userSelfMenuList.map((subItem, subIndex) => (

                                    subItem.path?.startsWith(item.path! + "/") &&

                                    <Route

                                        key={subIndex}
                                        path={subItem.path}

                                        element={

                                            <LoadElement elementStr={subItem.router}/>

                                        }

                                    />

                                ))}

                            </Route>

                    })

                }

                <Route

                    path="*"

                    element={<LoadElement elementStr={ManualRouterName.NotFound.name}/>}

                />

            </Routes>

        </BrowserRouter>

    );

}

interface ILoadElement {

    elementStr?: string

}

let appNav: NavigateFunction

export function getAppNav(): Function {

    return appNav as Function

}

let appDispatch: AppDispatch

export function getAppDispatch() {

    return appDispatch

}

let myApp: useAppProps

export function getApp() {

    return myApp

}

let userSelfInfo: UserSelfInfoVO

// 获取：用户信息
export function getUserSelfInfo() {

    return userSelfInfo

}

// 加载 element
function LoadElement(props: ILoadElement) {

    appNav = useNavigate()

    appDispatch = useAppDispatch();

    myApp = App.useApp();

    userSelfInfo = useAppSelector((state) => state.user.userSelfInfo)

    if (props.elementStr && RouterMapKeyList.includes(props.elementStr)) {

        return React.createElement(RouterMap[props.elementStr].element)

    }

    return null

}
