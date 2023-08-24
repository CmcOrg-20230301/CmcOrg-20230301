package com.cmcorg20230301.engine.be.ip2region.util;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import org.lionsoul.ip2region.xdb.Searcher;

public class Ip2RegionUtil {

    private static Searcher searcher;

    static {

        try {
            searcher = Searcher.newWithBuffer(IoUtil.readBytes(new ClassPathResource("ip2region.xdb").getStream()));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 通过 ip获取 ip的区域
     * 返回格式：0|0|0|内网IP|内网IP
     * 中国|0|重庆|重庆市|电信
     * 中国|0|上海|上海市|电信
     * 中国|0|香港|0|腾讯
     * 中国|0|香港|0|0
     * 日本|0|东京都|东京|亚马逊
     * 美国|0|0|0|科进
     * 美国|0|0|0|亚马逊
     * 备注：默认的 region 信息都固定了格式：国家|区域|省份|城市|ISP，缺省的地域信息默认是 0
     */
    public static String getRegion(String ip) {

        if (StrUtil.isBlank(ip)) {
            return "";
        }

        if (BooleanUtil.isFalse(Validator.isIpv4(ip))) {
            return "isNotIpv4#" + ip;
        }

        try {

            return searcher.search(ip);

        } catch (Exception e) {

            e.printStackTrace();
            return "errorIp#" + ip;

        }

    }

}
