import {BrowserRouter, NavigateFunction, Route, Routes, useNavigate} from "react-router-dom";
import React from "react";
import NoLoginRouterList from "@/router/NoLoginRouterList";
import RouterMap, {RouterMapKeyList} from "@/router/RouterMap";
import {AppDispatch, useAppDispatch, useAppSelector} from "@/store";
import PathConstant from "@/model/constant/PathConstant";
import {App} from "antd";
import {useAppProps} from "antd/es/app/context";

// MyApp
export default function () {

    const userSelfMenuList = useAppSelector(
        (state) => state.user.userSelfMenuList
    ).filter((item) => item.router)

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

                <Route
                    path={PathConstant.ADMIN_PATH}
                    element={<LoadElement elementStr="AdminLayout"/>}
                >

                    {userSelfMenuList.filter(it => it.path?.startsWith(PathConstant.ADMIN_PATH)).map((item, index) => (

                        <Route

                            key={index}
                            path={item.path}

                            element={

                                <LoadElement elementStr={item.router}/>

                            }

                        />

                    ))}

                </Route>

                <Route

                    path="*"

                    element={<LoadElement elementStr="NotFound"/>}

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

// 加载 element
function LoadElement(props: ILoadElement) {

    appNav = useNavigate()

    appDispatch = useAppDispatch();

    myApp = App.useApp();

    if (props.elementStr && RouterMapKeyList.includes(props.elementStr)) {

        return React.createElement(RouterMap[props.elementStr].element)

    }

    return null

}
