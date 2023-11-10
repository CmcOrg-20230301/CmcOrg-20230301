import {useLocation} from "react-router-dom";

// 空白页
export default function () {

    let location = useLocation();

    return <>

        {

            location.state?.showText || null

        }

    </>

}
