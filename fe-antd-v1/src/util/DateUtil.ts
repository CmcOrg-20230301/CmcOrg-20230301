import dayjs from "dayjs";

/**
 * 格式化时间
 */
export function FormatDateTime(date: Date = new Date()) {

    return dayjs(date).format('YYYY-MM-DD HH:mm:ss')

}

/**
 * 格式化时间，如果是今天，则不显示年月日
 */
export function FormatDateTimeForCurrentDay(date: Date = new Date()) {

    const currentDay = Math.trunc(GetServerTimestamp() / 86400000);

    const checkDay = Math.trunc(date.getTime() / 86400000);

    if (currentDay === checkDay) {

        return dayjs(date).format('HH:mm:ss')

    }

    return dayjs(date).format('YYYY-MM-DD HH:mm:ss')

}

/**
 * 获取：服务器的时间戳，目的：防止不同地区的时间差，保证和服务器的时间一致
 */
export function GetServerTimestamp(date: Date = new Date(), timezone: number = 8) {

    const offsetGmt = date.getTimezoneOffset(); // 本地时间和格林威治的时间差，单位为分钟

    const nowDate = date.getTime(); // 本地时间距 1970 年 1 月 1 日午夜（GMT 时间）之间的毫秒数

    const targetDate = new Date(nowDate + offsetGmt * 60 * 1000 + timezone * 60 * 60 * 1000);

    return targetDate.getTime();

}
