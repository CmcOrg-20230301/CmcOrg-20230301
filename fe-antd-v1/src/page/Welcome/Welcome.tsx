import {PageHeader} from "@ant-design/pro-components";
import React from "react";
import {useAppSelector} from "@/store";

// 欢迎页
export default function () {

    const tenantManageName = useAppSelector((state) => state.common.tenantManageName)

    return (

        <PageHeader
            title={tenantManageName}
        />

    )

}
