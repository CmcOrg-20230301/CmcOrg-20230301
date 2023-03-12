import PathConstant from "@/model/constant/PathConstant";

interface INoLoginRouterList {

    path: string
    elementStr: string

}

const NoLoginRouterList: INoLoginRouterList[] = [

    {
        path: PathConstant.SIGN_IN_PATH,
        elementStr: 'SignIn'
    },

    {
        path: PathConstant.SIGN_UP_PATH,
        elementStr: 'SignUp'
    }

]

export default NoLoginRouterList
