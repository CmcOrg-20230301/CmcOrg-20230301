package com.cmcorg20230301.be.engine.generate.util.apitest.sys;

import java.io.File;
import java.util.Map;

import com.cmcorg20230301.be.engine.file.base.model.dto.SysFileUploadDTO;
import com.cmcorg20230301.be.engine.generate.util.apitest.ApiTestHelper;
import com.cmcorg20230301.be.engine.generate.util.apitest.sign.ApiTestSignSignInNameUtil;
import com.cmcorg20230301.be.engine.model.model.dto.NotEmptyIdSet;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.model.model.vo.SignInVO;
import com.cmcorg20230301.be.engine.security.model.enums.SysFileUploadTypeEnum;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * 文件系统相关接口测试工具
 */
@Slf4j
public class ApiTestSysFileUtil {

    // 执行，接口的地址，备注：最后面不要加斜杠 /
    // private static final String API_ENDPOINT = "http://43.154.37.130:10001";
    private static final String API_ENDPOINT = "http://127.0.0.1:10001";

    // 测试文件的地址，需要包含文件类型后缀
    private static final String TEST_FILE_PATH = "/test/img/testImg.jpg";

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

        // 请求-上传文件：公有和私有
        Long fileId = sysFileUpload(apiEndpoint, signInVO.getJwt());

        // 请求-下载文件：私有
        sysFilePrivateDownload(apiEndpoint, signInVO.getJwt(), fileId);

        // 请求-批量获取：公开文件的 url
        sysFileGetPublicUrl(apiEndpoint, signInVO.getJwt(), fileId);

        // 请求-批量删除文件：公有和私有
        sysFileRemoveByFileIdSet(apiEndpoint, signInVO.getJwt(), fileId);

    }

    /**
     * 批量删除文件：公有和私有
     */
    private static void sysFileRemoveByFileIdSet(String apiEndpoint, String jwt, Long fileId) {

        long currentTs = System.currentTimeMillis();

        NotEmptyIdSet notEmptyIdSet = new NotEmptyIdSet(CollUtil.newHashSet(fileId));

        String bodyStr = HttpRequest.post(apiEndpoint + "/sys/file/removeByFileIdSet").header("Authorization", jwt)
            .body(JSONUtil.toJsonStr(notEmptyIdSet)).execute().body();

        log.info("请求-批量删除文件：公有和私有：耗时：{}，bodyByte长度：{}", ApiTestHelper.calcCostMs(currentTs), bodyStr);

    }

    /**
     * 批量获取：公开文件的 url
     */
    @SneakyThrows
    private static void sysFileGetPublicUrl(String apiEndpoint, String jwt, Long fileId) {

        long currentTs = System.currentTimeMillis();

        NotEmptyIdSet notEmptyIdSet = new NotEmptyIdSet(CollUtil.newHashSet(fileId));

        String bodyStr = HttpRequest.post(apiEndpoint + "/sys/file/getPublicUrl").header("Authorization", jwt)
            .body(JSONUtil.toJsonStr(notEmptyIdSet)).execute().body();

        log.info("请求-批量获取：公开文件的 url：耗时：{}，bodyByte长度：{}", ApiTestHelper.calcCostMs(currentTs), bodyStr);

    }

    /**
     * 下载文件：私有
     */
    @SneakyThrows
    private static void sysFilePrivateDownload(String apiEndpoint, String jwt, Long fileId) {

        long currentTs = System.currentTimeMillis();

        NotNullId notNullId = new NotNullId(fileId);

        byte[] bodyByteArr = HttpRequest.post(apiEndpoint + "/sys/file/privateDownload").header("Authorization", jwt)
            .body(JSONUtil.toJsonStr(notNullId)).execute().bodyBytes();

        log.info("请求-下载文件：私有：耗时：{}，bodyByte长度：{}", ApiTestHelper.calcCostMs(currentTs), bodyByteArr.length);

    }

    /**
     * 请求-上传文件：公有和私有
     */
    @SneakyThrows
    private static Long sysFileUpload(String apiEndpoint, String jwt) {

        long currentTs = System.currentTimeMillis();

        File file = FileUtil.file(TEST_FILE_PATH);

        SysFileUploadDTO dto = new SysFileUploadDTO();
        dto.setUploadType(SysFileUploadTypeEnum.AVATAR);

        Map<String, Object> map = BeanUtil.beanToMap(dto);

        String bodyStr = HttpRequest.post(apiEndpoint + "/sys/file/upload").form(map).form("file", file)
            .header("Authorization", jwt).execute().body();

        log.info("请求-上传文件：公有和私有：耗时：{}，bodyStr：{}", ApiTestHelper.calcCostMs(currentTs), bodyStr);

        return JSONUtil.parseObj(bodyStr).getLong("data");

    }

}
