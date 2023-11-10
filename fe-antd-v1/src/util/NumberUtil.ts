/**
 * 获取：展示的金额
 */
export function GetShowMoneyStr(value: number | string | undefined, suf: string = "元") {

    return Number(value).toFixed(2) + suf

}
