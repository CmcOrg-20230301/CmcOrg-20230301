/**
 * 处理后台返回的 ip所处区域
 */
export function HandlerRegion(region: string | undefined) {

    if (!region) return region

    return region
        .split('|')
        .filter((item) => item !== '0')
        .join(' ')

}

export const BASE_CHAR_NUMBER = 'abcdefghijklmnopqrstuvwxyz0123456789'

/**
 * 获取随机字符串
 */
export function RandomStr(length: number = 6) {

    let resStr = ''

    for (let index = 0; index < length; index++) {

        resStr += BASE_CHAR_NUMBER.charAt(Math.floor(Math.random() * 36))

    }

    return resStr

}

/**
 * 通过：text，获取 Typography.Text.type
 */
export function GetTextType(text: any) {

    let type

    const textNumber = Number(text);

    if (textNumber === 0) {

        type = 'secondary'

    } else if (textNumber > 0) {

        type = 'success'

    } else {

        type = 'danger'

    }

    return type;

}
