import React from "react";
import {SignSignInNameSetWx, SignSignInNameSetWxGetQrCodeUrl} from "@/api/http/SignSignInName.ts";
import SetWxModalForm from "../SetWxModalForm";

export default function () {

    return <>

        <SetWxModalForm setWxGetQrCodeUrl={SignSignInNameSetWxGetQrCodeUrl}
                        setWxGetQrCodeSceneFlag={SignSignInNameSetWxGetQrCodeSceneFlag}
                        formItemArr={[]}
                        setWx={SignSignInNameSetWx}/>

    </>

}