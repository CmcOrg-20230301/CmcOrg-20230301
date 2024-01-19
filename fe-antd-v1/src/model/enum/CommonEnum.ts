import {PresetStatusColorType} from "antd/es/_util/colors";

/**
 * 枚举类的 interface
 * T 一般为：number类型
 */
export interface IEnum<T = number | string> {

    code?: T

    name?: string

    status?: PresetStatusColorType

    [x: string]: any

}
