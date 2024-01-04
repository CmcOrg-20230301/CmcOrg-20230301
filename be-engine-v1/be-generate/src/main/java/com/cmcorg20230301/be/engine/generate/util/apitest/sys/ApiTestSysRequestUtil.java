package com.cmcorg20230301.be.engine.generate.util.apitest.sys;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import com.cmcorg20230301.be.engine.generate.util.apitest.ApiTestHelper;
import com.cmcorg20230301.be.engine.generate.util.apitest.sign.ApiTestSignSignInNameUtil;
import com.cmcorg20230301.be.engine.model.model.vo.SignInVO;
import com.cmcorg20230301.be.engine.request.model.dto.SysRequestPageDTO;
import com.cmcorg20230301.be.engine.request.model.dto.SysRequestSelfLoginRecordPageDTO;
import lombok.extern.slf4j.Slf4j;

/**
 * 请求相关接口测试工具
 */
@Slf4j
public class ApiTestSysRequestUtil {

    // 执行，接口的地址，备注：最后面不要加斜杠 /
    //    private static final String API_ENDPOINT = "http://43.154.37.130:10001";
    private static final String API_ENDPOINT = "http://127.0.0.1:10001";

    public static void main(String[] args) {

        // 执行
        exec(API_ENDPOINT, ApiTestHelper.ADMIN_SIGN_IN_NAME, ApiTestHelper.ADMIN_PASSWORD,
                ApiTestHelper.RSA_PUBLIC_KEY);

    }

    /**
     * 执行
     */
    private static void exec(String apiEndpoint, String adminSignInName, String adminPassword, String rsaPublicKey) {

        // 登录名-用户名账号密码登录
        SignInVO signInVO =
                ApiTestSignSignInNameUtil.signInNameSignIn(apiEndpoint, adminSignInName, adminPassword, rsaPublicKey);

        String jwt = signInVO.getJwt();

        // 请求-分页排序查询
        sysRequestPage(apiEndpoint, jwt);

        // 请求-所有请求的平均耗时-增强：增加筛选项
        sysRequestAllAvgPro(apiEndpoint, jwt);

        // 请求-所有请求的平均耗时
        sysRequestAllAvg(apiEndpoint, jwt);

        // 请求-当前用户：登录记录
        sysSelfLoginRecord(apiEndpoint, jwt);

    }

    /**
     * 请求-当前用户：登录记录
     */
    private static void sysSelfLoginRecord(String apiEndpoint, String jwt) {

        long currentTs = System.currentTimeMillis();

        SysRequestSelfLoginRecordPageDTO dto = new SysRequestSelfLoginRecordPageDTO();

        String bodyStr = HttpRequest.post(apiEndpoint + "/sys/request/self/loginRecord").body(JSONUtil.toJsonStr(dto))
                .header("Authorization", jwt).execute().body();

        log.info("请求-当前用户：登录记录：耗时：{}，bodyStr：{}", ApiTestHelper.calcCostMs(currentTs), bodyStr);

    }

    /**
     * 请求-所有请求的平均耗时
     */
    private static void sysRequestAllAvg(String apiEndpoint, String jwt) {

        long currentTs = System.currentTimeMillis();

        String bodyStr =
                HttpRequest.post(apiEndpoint + "/sys/request/allAvg").header("Authorization", jwt).execute().body();

        log.info("请求-所有请求的平均耗时：耗时：{}，bodyStr：{}", ApiTestHelper.calcCostMs(currentTs), bodyStr);

    }

    /**
     * 请求-所有请求的平均耗时-增强：增加筛选项
     */
    private static void sysRequestAllAvgPro(String apiEndpoint, String jwt) {

        long currentTs = System.currentTimeMillis();

        SysRequestPageDTO dto = new SysRequestPageDTO();

        String bodyStr = HttpRequest.post(apiEndpoint + "/sys/request/allAvgPro").body(JSONUtil.toJsonStr(dto))
                .header("Authorization", jwt).execute().body();

        log.info("请求-所有请求的平均耗时-增强：增加筛选项：耗时：{}，bodyStr：{}", ApiTestHelper.calcCostMs(currentTs), bodyStr);

    }

    /**
     * 请求-分页排序查询
     */
    private static void sysRequestPage(String apiEndpoint, String jwt) {

        long currentTs = System.currentTimeMillis();

        SysRequestPageDTO dto = new SysRequestPageDTO();

        String bodyStr = HttpRequest.post(apiEndpoint + "/sys/request/page").body(JSONUtil.toJsonStr(dto))
                .header("Authorization", jwt).execute().body();

        log.info("请求-分页排序查询：耗时：{}，bodyStr：{}", ApiTestHelper.calcCostMs(currentTs), bodyStr);

    }

}
