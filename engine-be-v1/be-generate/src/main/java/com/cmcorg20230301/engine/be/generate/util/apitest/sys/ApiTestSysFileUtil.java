package com.cmcorg20230301.engine.be.generate.util.apitest.sys;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpRequest;
import com.cmcorg20230301.engine.be.generate.util.apitest.ApiTestHelper;
import com.cmcorg20230301.engine.be.generate.util.apitest.sign.ApiTestSignSignInNameUtil;
import com.cmcorg20230301.engine.be.security.model.dto.SysFileUploadDTO;
import com.cmcorg20230301.engine.be.security.model.enums.SysFileUploadTypeEnum;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.Map;

/**
 * 文件系统相关接口测试工具
 */
@Slf4j
public class ApiTestSysFileUtil {

    // 执行，接口的地址，备注：最后面不要加斜杠 /
    private static final String API_ENDPOINT = "http://43.154.37.130:10001";
    //    private static final String API_ENDPOINT = "http://127.0.0.1:10001";

    // 测试文件的地址，需要包含文件类型后缀
    private static final String testFilePath = "/test/img/testImg.jpg";

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
        String jwt =
            ApiTestSignSignInNameUtil.signInNameSignIn(apiEndpoint, adminSignInName, adminPassword, rsaPublicKey);

        // 请求-上传文件：共有和私有
        sysFileUpload(apiEndpoint, jwt);

    }

    /**
     * 请求-上传文件：共有和私有
     */
    @SneakyThrows
    private static void sysFileUpload(String apiEndpoint, String jwt) {

        long currentTs = System.currentTimeMillis();

        File file = FileUtil.file(testFilePath);

        SysFileUploadDTO dto = new SysFileUploadDTO();
        dto.setUploadType(SysFileUploadTypeEnum.AVATAR);

        Map<String, Object> map = BeanUtil.beanToMap(dto);

        String bodyStr =
            HttpRequest.post(apiEndpoint + "/sys/file/upload").form(map).form("file", file).header("Authorization", jwt)
                .execute().body();

        log.info("请求-上传文件：共有和私有：耗时：{}，bodyStr：{}", ApiTestHelper.calcCostMs(currentTs), bodyStr);

    }

}
