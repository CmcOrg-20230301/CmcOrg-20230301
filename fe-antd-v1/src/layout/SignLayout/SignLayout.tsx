import {PropsWithChildren} from "react";
import LocalStorageKey from "@/model/constant/LocalStorageKey";
import {Navigate} from "react-router-dom";
import {CopyrightOutlined} from "@ant-design/icons/lib";
import {ConfigProvider} from "antd";
import {AliasToken} from "antd/es/theme/interface/alias";

interface ISignLayout extends PropsWithChildren {

    token?: Partial<AliasToken>

}

export function GetCopyright() {

    return `2021-${new Date().getFullYear()} Cmc Org. All Rights Reserved.`

}

// 登录注册页面布局
export default function (props: ISignLayout) {

    if (localStorage.getItem(LocalStorageKey.JWT)) {
        return <Navigate to={"/"}/>
    }

    return (

        <ConfigProvider
            theme={{
                token: props.token,
            }}
        >

            <div className={"p-t-130 p-b-10 flex-c vwh100"}>

                <>

                    {props.children}

                    <div className={"w100 flex-c ai-c m-t-50"}>

                        <div className={"m-r-10 f-14 black1"}>

                            <CopyrightOutlined/> {GetCopyright()}

                        </div>

                    </div>

                </>

            </div>

        </ConfigProvider>

    )

}
