import dayjs from "dayjs";

/**
 * 格式化时间：
 */
export function FormatDateTime(date: Date = new Date()) {

    return dayjs(date).format('YYYY-MM-DD HH:mm:ss')

}
