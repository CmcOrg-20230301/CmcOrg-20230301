/**
 * 处理后台返回的 ip所处区域
 */
export function HandlerRegion(region: string) {

    if (!region) return region

    return region
        .split('|')
        .filter((item) => item !== '0')
        .join(' ')

}

/**
 * 获取随机字符串
 */
export function RandomStr(length: number = 6) {

    const BASE_CHAR_NUMBER = 'abcdefghijklmnopqrstuvwxyz0123456789'

    let resStr = ''

    for (let index = 0; index < length; index++) {

        resStr += BASE_CHAR_NUMBER.charAt(Math.floor(Math.random() * 36))

    }

    return resStr

}
