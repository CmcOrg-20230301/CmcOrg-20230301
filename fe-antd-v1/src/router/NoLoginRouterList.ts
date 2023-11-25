import PathConstant from "@/model/constant/PathConstant";

interface INoLoginRouterList {

    path: string
    elementStr: string

}

const NoLoginRouterList: INoLoginRouterList[] = [

    {
        path: PathConstant.SIGN_IN_PATH,
        elementStr: 'signSignInSignIn'
    },

    {
        path: PathConstant.SIGN_UP_PATH,
        elementStr: 'signSignUpSignUp'
    },

    {
        path: PathConstant.BLANK_PATH,
        elementStr: 'Blank'
    },

    {
        path: PathConstant.INIT_BLANK_PATH,
        elementStr: 'InitBlank'
    },

    {
        path: PathConstant.FILE_DOWNLOAD_PATH,
        elementStr: 'FileDownload'
    },

    {
        path: PathConstant.OAUTH2_WX_PATH,
        elementStr: 'oauth2Oauth2WxOauth2Wx'
    },

    {
        path: PathConstant.OAUTH2_CHECK_JWT_PATH,
        elementStr: 'oauth2CheckJwtCheckJwt'
    },

]

export default NoLoginRouterList
