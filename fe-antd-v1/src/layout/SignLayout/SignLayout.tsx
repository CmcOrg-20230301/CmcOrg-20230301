import {PropsWithChildren, useEffect} from "react";
import LocalStorageKey from "@/model/constant/LocalStorageKey";
import {Navigate} from "react-router-dom";
import {CopyrightOutlined} from "@ant-design/icons";
import {ConfigProvider} from "antd";
import {AliasToken} from "antd/es/theme/interface/alias";

interface ISignLayout extends PropsWithChildren {

    token?: Partial<AliasToken>

    tenantManageName?: string

}

export function GetCopyright(tenantManageName?: string) {

    return `2021-${new Date().getFullYear()} ${tenantManageName || ''}. All Rights Reserved.`

}

// 登录注册页面布局
export default function (props: ISignLayout) {

    if (localStorage.getItem(LocalStorageKey.JWT)) {
        return <Navigate to={"/"}/>
    }

    useEffect(() => {

        if (props.tenantManageName) {

            document.title = props.tenantManageName;

        }

    }, [props.tenantManageName])

    return (

        <ConfigProvider

            theme={{
                token: props.token,
            }}

        >

            <div className={"p-t-50 p-b-10 flex-c vwh100"}>

                <>

                    {props.children}

                    <div className={"w100 flex-c ai-c m-t-50"}>

                        <div className={"m-r-10 f-14 black1"}>

                            <CopyrightOutlined/> {GetCopyright(props.tenantManageName)}

                        </div>

                    </div>

                </>

            </div>

        </ConfigProvider>

    )

}
