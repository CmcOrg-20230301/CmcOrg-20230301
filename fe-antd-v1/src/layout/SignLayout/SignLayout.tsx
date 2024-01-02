import {PropsWithChildren, useEffect} from "react";
import LocalStorageKey from "@/model/constant/LocalStorageKey";
import {Navigate} from "react-router-dom";
import {CopyrightOutlined} from "@ant-design/icons";
import {ConfigProvider} from "antd";
import {AliasToken} from "antd/es/theme/interface/alias";
import {RemoveTenantNameSuf} from "@/page/sign/SignUp/SignUpUtil";
import CommonConstant from "@/model/constant/CommonConstant";

interface ISignLayout extends PropsWithChildren {

    token?: Partial<AliasToken>

    tenantName?: string

}

export function GetCopyright(tenantName ?: string) {

    return `2021-${new Date().getFullYear()} ${RemoveTenantNameSuf(tenantName) || 'Cmc Org'}. All Rights Reserved.`

}

// 登录注册页面布局
export default function (props: ISignLayout) {

    if (localStorage.getItem(LocalStorageKey.JWT)) {
        return <Navigate to={"/"}/>
    }

    useEffect(() => {

        document.title = CommonConstant.SYS_NAME;

    }, [])

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

                            <CopyrightOutlined/> {GetCopyright(props.tenantName)}

                        </div>

                    </div>

                </>

            </div>

        </ConfigProvider>

    )

}
