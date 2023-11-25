import {useLocation} from "react-router-dom";
import {GetURLSearchParams} from "@/util/CommonUtil";
import {UseEffectInit} from "@/util/UseEffectUtil";

// 初始化数据的空白页
export default function () {

    let location = useLocation();

    UseEffectInit();

    return <>

        {

            location.state?.showText || GetURLSearchParams().get("showText") || '打开失败，请重新打开页面'

        }

    </>

}
