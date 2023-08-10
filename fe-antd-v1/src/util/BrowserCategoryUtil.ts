/**
 * 获取：浏览器的类别
 *
 * 101 windows-浏览器
 * 102 mac-浏览器
 * 103 linux-浏览器
 *
 * 302 安卓-浏览器
 * 303 安卓-浏览器-微信
 *
 * 402 苹果-浏览器
 * 403 苹果-浏览器-微信
 */
export function GetBrowserCategory(): number {

    // 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36 Edg/115.0.1901.200'

    // 'Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Mobile Safari/537.36 Edg/115.0.1901.200'

    const userAgent = navigator.userAgent;

    if (/(MicroMessenger)/i.test(userAgent)) {

        return 303 // 安卓-浏览器-微信

    } else if (/(Android)/i.test(userAgent)) {

        return 302 // 安卓-浏览器

    } else if (/(iPhone|iPad|iPod|iOS)/i.test(userAgent)) {

        return 402 // 苹果-浏览器

    } else if (/(Mac)/i.test(userAgent)) {

        return 102 // mac-浏览器

    } else if (/(Linux)/i.test(userAgent)) {

        return 103 // linux-浏览器

    }

    return 101

}
