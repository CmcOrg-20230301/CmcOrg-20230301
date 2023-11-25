import {useEffect, useMemo} from "react";
import {OpenFileDownloadPageFlag, SysFilePrivateDownload} from "@/util/FileUtil";
import {useLocation} from "react-router-dom";
import {GetURLSearchParams} from "@/util/CommonUtil";
import {UseEffectInit} from "@/util/UseEffectUtil";

// 文件下载页面
export default function () {

    UseEffectInit()

    let location = useLocation();

    const id = useMemo(() => {

        return location.state?.id || GetURLSearchParams().get("id") || null

    }, []);

    const openFileDownloadPageFlag = useMemo(() => {

        return OpenFileDownloadPageFlag()

    }, []);

    useEffect(() => {

        if (!openFileDownloadPageFlag) {

            SysFilePrivateDownload({id: id})

        }

    })

    return <>

        {id ? (openFileDownloadPageFlag ? '点击右上角选择在浏览器中打开' : '文件下载中，请稍后...') : '文件下载失败，请重新打开'}

    </>

}
