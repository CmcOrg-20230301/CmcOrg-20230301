import {NavigateFunction, Route, Routes, useNavigate} from "react-router-dom";
import React, {useEffect, useState} from "react";
import NoLoginRouterList from "@/router/NoLoginRouterList";
import RouterMap, {ManualRouterName, RouterMapKeySet} from "@/router/RouterMap";
import {AppDispatch, useAppDispatch, useAppSelector} from "@/store";
import {App} from "antd";
import {useAppProps} from "antd/es/app/context";
import {UserSelfInfoVO} from "@/api/http/UserSelf";
import {UseEffectConsoleOpenKeydownListener} from "@/util/UseEffectUtil";
import {SysMenuDO} from "@/api/http/SysMenu.ts";
import type {To} from "@remix-run/router";
import type {NavigateOptions} from "react-router/dist/lib/context";

let appNav: NavigateFunction

export function GetAppNav() {

    return (to: To, options?: NavigateOptions) => {

        console.log({to, options})

        return appNav(to)

    }

}

let appDispatch: AppDispatch

export function GetAppDispatch() {

    return appDispatch

}

let myApp: useAppProps

export function GetApp() {

    return myApp

}

let userSelfInfo: UserSelfInfoVO

// 获取：用户信息
export function GetUserSelfInfo() {

    return userSelfInfo

}

let userSelfMenuList: SysMenuDO[]

// 获取：用户菜单集合
export function GetUserSelfMenuList() {

    return userSelfMenuList

}

// MyApp
export default function () {

    appNav = useNavigate()

    appDispatch = useAppDispatch();

    myApp = App.useApp();

    userSelfInfo = useAppSelector((state) => state.user.userSelfInfo)

    userSelfMenuList = useAppSelector((state) => state.user.userSelfMenuList)

    // 是否打开控制台的按键监听
    UseEffectConsoleOpenKeydownListener()

    const [element, setElement] = useState<JSX.Element | null>(null);

    useEffect(() => {

        // 更新：路由
        UpdateLayoutChildrenRouter(setElement, userSelfMenuList);

    }, [userSelfMenuList])

    return element;

}

// 更新：路由
function UpdateLayoutChildrenRouter(setElement: React.Dispatch<React.SetStateAction<JSX.Element | null>>, userSelfMenuList: SysMenuDO[]) {

    const layoutChildrenRouter: Record<string, JSX.Element[]> = {}

    userSelfMenuList.forEach(item => {

        if (!item.router || !item.path) {
            return
        }

        const prePath = "/" + item.path.split("/")[1];

        let elementArr = layoutChildrenRouter[prePath]

        if (!elementArr) {

            elementArr = []

            layoutChildrenRouter[prePath] = elementArr

        }

        elementArr.push(
            <Route

                key={item.id}
                path={item.path}
                element={<LoadElement elementStr={item.router}/>}

            />
        )

    })

    const routerArr: JSX.Element[] = []

    let index = 1

    NoLoginRouterList.forEach(item => {

        routerArr.push(
            <Route

                key={index}
                path={item.path}
                element={<LoadElement elementStr={item.elementStr}/>}

            />
        )

        index = index + 1

    })

    const layoutRouterOriginArr = [ManualRouterName.AdminLayout, ManualRouterName.BlankLayout]

    // 页面布局集合
    layoutRouterOriginArr.forEach(item => {

        routerArr.push(
            <Route

                key={index}
                path={item.path}
                element={<LoadElement elementStr={item.name}/>}

            >

                {layoutChildrenRouter[item.path!]}

            </Route>
        )

        index = index + 1

    })

    routerArr.push(
        <Route

            key={index}
            path="*"
            element={<LoadElement elementStr={ManualRouterName.NotFound.name}/>}

        />
    )

    // 更新页面
    setElement(
        <Routes>

            {routerArr}

        </Routes>
    )

}

interface ILoadElement {

    elementStr?: string

}

// 加载 element
function LoadElement(props: ILoadElement) {

    const [element, setElement] = useState<JSX.Element | null>(null);

    useEffect(() => {

        console.log('elementStr', props.elementStr)

        if (props.elementStr && RouterMapKeySet.has(props.elementStr)) {

            setElement(React.createElement(RouterMap[props.elementStr].element))

        }

    }, [props.elementStr])

    return element

}

