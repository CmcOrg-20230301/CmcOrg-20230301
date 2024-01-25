import AdminLayout from "@/layout/AdminLayout/AdminLayout";
import NotFound from "@/component/NotFound/NotFound";
import Blank from "@/component/Blank/Blank";
import BlankLayout from "@/layout/BlankLayout/BlankLayout";
import PathConstant from "@/model/constant/PathConstant";
import InitBlank from "@/component/InitBlank/InitBlank";
import FileDownload from "@/component/FileDownload/FileDownload";

export interface IManualRouterItem extends IRouterMapItem {

    name: string
    path?: string

}

export interface IManualRouter {

    NotFound: IManualRouterItem
    AdminLayout: IManualRouterItem
    BlankLayout: IManualRouterItem
    Blank: IManualRouterItem
    InitBlank: IManualRouterItem
    FileDownload: IManualRouterItem

}

// 手动添加的路由组件
export const ManualRouterName: IManualRouter = {

    NotFound: {name: "NotFound", element: NotFound},

    AdminLayout: {name: "AdminLayout", element: AdminLayout, path: PathConstant.ADMIN_PATH},

    BlankLayout: {name: "BlankLayout", element: BlankLayout, path: PathConstant.BLANK_LAYOUT_PATH},

    Blank: {name: "Blank", element: Blank},

    InitBlank: {name: "InitBlank", element: InitBlank},

    FileDownload: {name: "FileDownload", element: FileDownload},

}

// 正则表达式转驼峰，默认：下划线转驼峰
export function toHump(name: string, searchValue: string | RegExp = /_(\w)/g) {

    return name.replace(searchValue, (all, letter) => {

        return letter.toUpperCase()

    })

}

export interface IRouterMapItem {

    element: any

}

const RouterMap: Record<string, IRouterMapItem> = {} // 路由 map

// 手动添加路由组件
Object.keys(ManualRouterName).forEach(key => {

    const item = ManualRouterName[key];

    RouterMap[item.name] = {element: item.element}

})

// 自动获取路由
const fileObj: Record<string, { [key: string]: any }> = import.meta.glob(
    '/src/page/**/*.tsx',
    {eager: true}
)

Object.keys(fileObj).forEach((item: string) => {

    const split = item.split('/');

    if ((split[split.length - 2] + '.tsx') !== split[split.length - 1]) {
        return // 只要：/src/page/home/home.tsx
    }

    // 例如：/src/page/home/home -> homeHome，/src/page/sign/SignIn/SignIn -> signSignInSignIn
    const fileName = toHump(item.split('/src/page/')[1].split('.tsx')[0], /\/(\w)/g)

    RouterMap[fileName] = {
        element: fileObj[item].default,
    }

})

export const RouterMapKeySet = new Set<string>(Object.keys(RouterMap))

export default RouterMap
