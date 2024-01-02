import * as Icon from "@ant-design/icons";
import {IconBaseProps} from "@ant-design/icons/lib/components/Icon";

export const IconList = Object.keys(Icon).filter(item => item !== 'default' && item !== 'IconProvider' && item !== 'getTwoToneColor' && item !== 'setTwoToneColor' && item !== 'createFromIconfontCN')

const IconTemp = Icon as Record<string, any>

interface IMyIcon extends IconBaseProps {
    icon?: string
}

// 自定义封装：@ant-design/icons
export default function (props: IMyIcon) {

    if (!props.icon) {
        return null
    }

    const Element = IconTemp[props.icon]

    if (Element) {

        return <Element {...props} />

    } else {

        return null

    }

}
