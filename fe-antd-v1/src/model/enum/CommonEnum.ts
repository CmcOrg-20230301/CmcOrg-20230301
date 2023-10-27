import {PresetStatusColorType} from "antd/es/_util/colors";

/**
 * 枚举类的 interface
 */
export interface IEnum {

    code?: number | string

    name?: string

    status?: PresetStatusColorType

    [x: string]: any

}
