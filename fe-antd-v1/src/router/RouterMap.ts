import AdminLayout from "@/layout/AdminLayout/AdminLayout";
import NotFound from "@/componse/NotFound/NotFound";
import SignIn from "@/page/sign/SignIn/SignIn";
import SignUp from "@/page/sign/SignUp/SignUp";

// 正则表达式转驼峰，默认：下划线转驼峰
export function toHump(name: string, searchValue: string | RegExp = /_(\w)/g) {

    return name.replace(searchValue, (all, letter) => {

        return letter.toUpperCase()

    })

}

interface IRouterMapItem {

    element: any

}

const RouterMap: Record<string, IRouterMapItem> = {} // 路由 map

// 手动添加路由
RouterMap['NotFound'] = {
    element: NotFound
}

RouterMap['SignIn'] = {
    element: SignIn
}

RouterMap['SignUp'] = {
    element: SignUp
}

RouterMap['AdminLayout'] = {
    element: AdminLayout
}

// 自动获取路由
const fileObj: Record<string, { [key: string]: any }> = import.meta.glob(
    '/src/page/**/*.tsx', {eager: true}
)

Object.keys(fileObj).forEach((item: string) => {

    const split = item.split('/');

    if ((split[split.length - 2] + '.tsx') !== split[split.length - 1]) {
        return // 只要：/src/page/home/home.tsx
    }

    // 例如：/src/page/home/home -> homeHome
    const fileName = toHump(item.split('/src/page/')[1].split('.tsx')[0], /\/(\w)/g)

    RouterMap[fileName] = {
        element: fileObj[item].default,
    }

})

export const RouterMapKeyList = Object.keys(RouterMap)

export default RouterMap
