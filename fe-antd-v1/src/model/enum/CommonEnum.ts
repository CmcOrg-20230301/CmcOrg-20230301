import {PresetStatusColorType} from "antd/es/_util/colors";

/**
 * 枚举类的 interface
 */
export interface IEnum<T = number | string> {

    code?: T

    name?: string

    status?: PresetStatusColorType

    [x: string]: any

}
