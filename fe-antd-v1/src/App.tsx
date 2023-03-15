import {BrowserRouter, NavigateFunction, Route, Routes, useNavigate} from "react-router-dom";
import React from "react";
import NoLoginRouterList from "@/router/NoLoginRouterList";
import RouterMap, {RouterMapKeyList} from "@/router/RouterMap";
import PathConstant from "@/model/constant/PathConstant";
import {AppDispatch, useAppDispatch, useAppSelector} from "@/store";

// App
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

                    {userSelfMenuList.map((item, index) => (

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

let AppNav: NavigateFunction

export function getAppNav() {

    return AppNav

}

let appDispatch: AppDispatch

export function getAppDispatch() {

    return appDispatch

}

// 加载 element
function LoadElement(props: ILoadElement) {

    AppNav = useNavigate()

    appDispatch = useAppDispatch();

    if (props.elementStr && RouterMapKeyList.includes(props.elementStr)) {

        return React.createElement(RouterMap[props.elementStr].element)

    }

    return null

}
