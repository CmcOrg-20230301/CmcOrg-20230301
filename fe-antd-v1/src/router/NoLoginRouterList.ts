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
        path: PathConstant.OAUTH2_WX_PATH,
        elementStr: 'oauth2Oauth2WxOauth2Wx'
    },

]

export default NoLoginRouterList
