import SetWxModalForm from "@/page/user/Self/userSelfSetting/wx/SetWxModalForm.tsx";
import {UserSelfDeleteModalTargetName, UserSelfDeleteModalTitle} from "@/page/user/Self/UserSelfSetting.tsx";
import React from "react";
import {SignWxSignDelete, SignWxSignDeleteGetQrCodeSceneFlag, SignWxSignDeleteGetQrCodeUrl} from "@/api/http/SignWx.ts";

export default function () {

    return <>

        <SetWxModalForm setWxGetQrCodeUrl={SignWxSignDeleteGetQrCodeUrl}
                        setWxGetQrCodeSceneFlag={SignWxSignDeleteGetQrCodeSceneFlag}
                        setWx={SignWxSignDelete}
                        title={UserSelfDeleteModalTitle}
                        trigger={<a className={"red3"}>{UserSelfDeleteModalTargetName}</a>}
        />

    </>

}