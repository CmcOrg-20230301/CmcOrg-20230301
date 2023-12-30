import {
    DefaultFooter,
    MenuDataItem,
    PageContainer,
    ProLayout,
    RouteContext,
    RouteContextType
} from "@ant-design/pro-components";
import CommonConstant from "@/model/constant/CommonConstant";
import {Outlet} from "react-router-dom";
import {GetAppDispatch, GetAppNav} from "@/MyApp";
import React, {useEffect, useState} from "react";
import {SysMenuDO} from "@/api/http/SysMenu";
import PathConstant from "@/model/constant/PathConstant";
import {setUserSelfAvatarUrl, setUserSelfInfo} from "@/store/userSlice";
import {LogoutOutlined, UserOutlined, WalletOutlined} from "@ant-design/icons/lib";
import {GetCopyright} from "@/layout/SignLayout/SignLayout";
import {Avatar, Button, Dropdown, Space, Typography} from "antd";
import SessionStorageKey from "@/model/constant/SessionStorageKey";
import {ExecConfirm, ToastSuccess} from "@/util/ToastUtil";
import {SignOut} from "@/util/UserUtil";
import {useAppSelector} from "@/store";
import {UserSelfInfo} from "@/api/http/UserSelf";
import MyIcon from "@/component/MyIcon/MyIcon";
import {ListToTree} from "@/util/TreeUtil";
import {InDev} from "@/util/CommonUtil";
import {SignOutSelf} from "@/api/http/SignOut";
import {RouterMapKeyList} from "@/router/RouterMap";
import {SysFileGetPublicUrl} from "@/api/http/SysFile";
import {SysTenantGetNameById} from "@/api/http/SysTenant";
import {TENANT_NAME_SUF} from "@/page/sign/SignUp/SignUpUtil";
import {UseEffectLoadSysMenuUserSelfMenuList} from "@/util/UseEffectUtil";

// 前往：第一个页面
function goFirstPage(menuList: SysMenuDO[]) {

    if (window.location.pathname !== PathConstant.ADMIN_PATH) {
        return
    }

    const adminRedirectPath = sessionStorage.getItem(SessionStorageKey.ADMIN_REDIRECT_PATH);

    if (adminRedirectPath) {

        sessionStorage.removeItem(SessionStorageKey.ADMIN_REDIRECT_PATH)

        if (menuList.some(item => item.path === adminRedirectPath)) {

            return GetAppNav()(adminRedirectPath)

        }

    }

    menuList.some((item) => {

        if (item.firstFlag && item.path) {

            GetAppNav()(item.path)
            return true

        }

        return false

    })

}

// Admin 页面布局
export default function () {

    const [element, setElement] = useState<React.ReactNode>(null);

    // 设置 element
    function doSetElement(userSelfMenuList: SysMenuDO[]) {

        if (element == null) {
            setElement(<AdminLayoutElement userSelfMenuList={userSelfMenuList}/>)
        }

    }

    // 加载菜单
    UseEffectLoadSysMenuUserSelfMenuList(data => {

        doSetElement(data)
        goFirstPage(data)

    });

    return element

}

interface IAdminLayoutElement {

    userSelfMenuList: SysMenuDO[]

}

let setPathnameTemp: (value: (((prevState: string) => string) | string)) => void

/**
 * 前往页面，目的：可以设置 ProLayout组件的 pathname参数
 *
 * @param path 需要跳转的路径
 * @param data 例如：{state: {tenantId: tenantId}}
 */
export function GoPage(path: string, data?: any) {

    setPathnameTemp(path)
    GetAppNav()(path, data)

}

// Admin 页面布局元素
function AdminLayoutElement(props: IAdminLayoutElement) {

    const [pathname, setPathname] = useState<string>('')
    const appDispatch = GetAppDispatch();

    setPathnameTemp = setPathname

    const userSelfInfo = useAppSelector((state) => state.user.userSelfInfo)

    const userSelfAvatarUrl = useAppSelector((state) => state.user.userSelfAvatarUrl)

    const [tenantName, setTenantName] = useState<string>(""); // 租户名

    useEffect(() => {

        setPathname(window.location.pathname)

        UserSelfInfo().then(res => {

            appDispatch(setUserSelfInfo(res.data))

            const avatarFileId = res.data.avatarFileId!;

            if (avatarFileId as any !== -1) {

                SysFileGetPublicUrl({idSet: [avatarFileId!]}).then(res => {

                    appDispatch(setUserSelfAvatarUrl(res.data.map![avatarFileId] || ''))

                })

            }

            SysTenantGetNameById({value: res.data.tenantId}).then(res => {

                if (res.data) {

                    setTenantName(res.data + TENANT_NAME_SUF)

                } else {

                    setTenantName("")

                }

            })

        })

    }, [])

    return (

        <ProLayout

            title={tenantName + CommonConstant.SYS_NAME}

            location={{
                pathname
            }}

            menu={{

                request: async () => {

                    const userSelfMenuListTemp: MenuDataItem[] = JSON.parse(JSON.stringify(props.userSelfMenuList));

                    userSelfMenuListTemp.forEach(item => {

                        item.icon = <MyIcon icon={item.icon as string}/>
                        item.hideInMenu = !item.showFlag

                    })

                    return ListToTree(userSelfMenuListTemp);

                },

            }}

            layout={"mix"}
            splitMenus={true}
            fixSiderbar={true}
            fixedHeader={true}

            menuItemRender={(item: MenuDataItem, defaultDom: React.ReactNode) => (

                <a
                    onClick={() => {

                        let path = item.path // 路径
                        let linkFlag = item.linkFlag // 是否另外一个页面打开

                        if (item.redirect) {
                            path = item.redirect
                        }

                        if (path && linkFlag) {

                            window.open(path, '_blank')
                            return

                        }

                        if (path && path === item.redirect) {

                            GoPage(path)
                            return;

                        }

                        if (path && item.router) {

                            if (RouterMapKeyList.includes(item.router)) {

                                GoPage(path)
                                return;

                            }

                        }

                        InDev() // 否则：提示开发中

                    }}

                >

                    <>
                        {defaultDom}
                    </>

                </a>

            )}

            rightContentRender={() => (

                <RouteContext.Consumer>

                    {(routeContextType: RouteContextType) => {

                        return <Space size={"large"}>

                            <Dropdown

                                menu={{

                                    items: [

                                        {
                                            key: '1',
                                            label: <a onClick={() => {

                                                GoPage(PathConstant.USER_SELF_PATH)

                                            }
                                            }>
                                                个人中心
                                            </a>,

                                            icon: <UserOutlined/>
                                        },

                                        {
                                            key: '2',
                                            label: <a onClick={() => {

                                                GoPage(PathConstant.SYS_USER_SELF_WALLET_PATH)

                                            }
                                            }>
                                                我的钱包
                                            </a>,

                                            icon: <WalletOutlined/>
                                        },

                                        {
                                            key: '3',
                                            danger: true,
                                            label: <a
                                                onClick={() => {

                                                    ExecConfirm(async () => {

                                                        await SignOutSelf().then((res) => {

                                                            ToastSuccess(res.msg)
                                                            SignOut()

                                                        })

                                                    }, undefined, "确定退出登录吗？")

                                                }}
                                            >
                                                退出登录
                                            </a>,

                                            icon: <LogoutOutlined/>
                                        },

                                    ]

                                }}>

                                <Button className={"m-r-10"} type="text" onClick={() => {

                                    if (!routeContextType.isMobile) {

                                        GoPage(PathConstant.USER_SELF_PATH)

                                    }

                                }}>

                                    <Space>

                                        <Avatar

                                            size="small"

                                            src={
                                                userSelfAvatarUrl ? userSelfAvatarUrl : CommonConstant.FIXED_AVATAR_URL
                                            }

                                        />

                                        <Typography.Text ellipsis style={{width: 95}}
                                                         type="secondary">{userSelfInfo.nickname}</Typography.Text>

                                    </Space>

                                </Button>

                            </Dropdown>

                        </Space>

                    }}

                </RouteContext.Consumer>

            )}

            footerRender={() => (

                <DefaultFooter
                    copyright={GetCopyright(tenantName)}
                />

            )}

        >

            <RouteContext.Consumer>

                {(routeContextType: RouteContextType) => {

                    return (

                        routeContextType.currentMenu?.hiddenPageContainerFlag ?

                            <Outlet/>

                            :

                            <PageContainer>

                                <Outlet/>

                            </PageContainer>

                    )

                }}

            </RouteContext.Consumer>

        </ProLayout>

    )

}
