import {useLocation} from "react-router-dom";
import {GetURLSearchParams} from "@/util/CommonUtil";

// 空白页
export default function () {

    let location = useLocation();

    return <>

        {

            location.state?.showText || GetURLSearchParams().get("showText") || null

        }

    </>

}
