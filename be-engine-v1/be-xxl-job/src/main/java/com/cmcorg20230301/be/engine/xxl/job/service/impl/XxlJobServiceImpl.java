package com.cmcorg20230301.be.engine.xxl.job.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.cmcorg20230301.be.engine.cache.util.CacheRedisKafkaLocalUtil;
import com.cmcorg20230301.be.engine.cache.util.MyCacheUtil;
import com.cmcorg20230301.be.engine.model.model.constant.BaseConstant;
import com.cmcorg20230301.be.engine.model.model.dto.NotNullId;
import com.cmcorg20230301.be.engine.redisson.model.enums.RedisKeyEnum;
import com.cmcorg20230301.be.engine.security.model.vo.ApiResultVO;
import com.cmcorg20230301.be.engine.util.util.MyDateUtil;
import com.cmcorg20230301.be.engine.xxl.job.model.dto.XxlJobInsertDTO;
import com.cmcorg20230301.be.engine.xxl.job.service.XxlJobService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.HttpCookie;
import java.util.Date;
import java.util.List;

@Service
public class XxlJobServiceImpl implements XxlJobService {

    @Value("${xxl.job.admin.addresses}")
    private String adminAddresses;

    @Value("${xxl.job.admin.userName}")
    private String userName;

    @Value("${xxl.job.admin.password}")
    private String password;

    /**
     * 新增 xxl-job 定时任务
     */
    @Override
    public Long insert(XxlJobInsertDTO dto) {

        if (!JSONUtil.isTypeJSON(dto.getExecutorParam())) {
            ApiResultVO.errorMsg("操作失败：executorParam 必须是 json格式字符串");
        }

        if (dto.getProSendTime() != null) {
            // 增加 10秒，目的：防止时间过短，然后错过了
            Date date = new Date(dto.getProSendTime().getTime() + BaseConstant.SECOND_10_EXPIRE_TIME);
            dto.setScheduleConf(MyDateUtil.getCron(date)); // 根据时间生成 Cron 表达式
        }

        if (StrUtil.isBlank(dto.getScheduleConf())) {
            ApiResultVO.errorMsg("操作失败：scheduleConf 不能为空");
        }

        HttpCookie cookie = getCookie();
        if (StrUtil.isBlank(dto.getJobGroup())) {
            setJobGroup(dto, cookie);
        }

        if (StrUtil.isBlank(dto.getJobGroup())) {
            ApiResultVO.errorMsg("操作失败：jobGroup 不能为空");
        }

        return doAddJob(JSONUtil.parseObj(dto), cookie); // 执行：新增 xxl-job 任务

    }

    /**
     * 执行：新增 xxl-job 任务
     */
    public Long doAddJob(JSONObject formJson, HttpCookie cookie) {

        if (cookie == null) {
            cookie = getCookie();
        }

        try {

            // 新增任务
            HttpResponse response = HttpRequest.post(adminAddresses + "/jobinfo/add").form(formJson).cookie(cookie)
                .timeout(BaseConstant.MINUTE_1_EXPIRE_TIME).execute();

            JSONObject bodyJson = JSONUtil.parseObj(response.body());

            if (!response.isOk()) {
                ApiResultVO.errorMsg("操作失败：新增任务失败：" + bodyJson);
            }

            Long id = bodyJson.getLong("content"); // 会返回本次 新增任务的 id
            if (id == null) {
                ApiResultVO.errorMsg("操作失败：新增任务失败：content 为空");
            }

            // 启动任务，备注：因为新增的时候，不支持启动，所以只有再调用启动接口
            formJson = JSONUtil.createObj().set("id", id);
            response = HttpRequest.post(adminAddresses + "/jobinfo/start").form(formJson).cookie(cookie)
                .timeout(BaseConstant.MINUTE_1_EXPIRE_TIME).execute();

            bodyJson = JSONUtil.parseObj(response.body());
            if (!response.isOk()) {
                ApiResultVO.errorMsg("操作失败：启动任务失败：" + bodyJson);
            }

            return id;

        } catch (Exception e) {

            // 移除 redis中的 cookie，然后再执行一次本方法
            CacheRedisKafkaLocalUtil.remove(RedisKeyEnum.XXL_JOB_COOKIE_CACHE, null);
            return doAddJob(formJson, null);

        }

    }

    /**
     * 自动获取第一个【执行器 id】，如果一个都没有，则会自动创建
     */
    public void setJobGroup(XxlJobInsertDTO dto, HttpCookie cookie) {

        if (cookie == null) {
            cookie = getCookie();
        }

        try {

            JSONObject formJson = JSONUtil.createObj().set("start", 0).set("length", 1);

            // 获取执行器
            HttpResponse response =
                HttpRequest.post(adminAddresses + "/jobgroup/pageList").form(formJson).cookie(cookie)
                    .timeout(BaseConstant.MINUTE_1_EXPIRE_TIME).execute();

            JSONObject bodyJson = JSONUtil.parseObj(response.body());

            if (!response.isOk()) {
                ApiResultVO.errorMsg("操作失败：获取执行器失败：" + bodyJson);
            }

            JSONArray data = bodyJson.getJSONArray("data");

            if (CollUtil.isEmpty(data)) { // 如果执行器列表为空，则进行创建

                formJson = JSONUtil.createObj().set("appname", "xxl-job-executor-main").set("title", "主执行器")
                    .set("addressType", 0); // addressType：0 自动注册

                response = HttpRequest.post(adminAddresses + "/jobgroup/save").form(formJson).cookie(cookie)
                    .timeout(BaseConstant.MINUTE_1_EXPIRE_TIME).execute();

                bodyJson = JSONUtil.parseObj(response.body());

                if (!response.isOk()) {
                    ApiResultVO.errorMsg("操作失败：创建执行器失败：" + bodyJson);
                }

                setJobGroup(dto, cookie); // 再次设置【执行器 id】
                return;

            }

            JSONObject jsonObject = (JSONObject)data.get(0); // 获取第一个【执行器】
            dto.setJobGroup(jsonObject.getStr("id")); // 设置 jobGroup

        } catch (Exception e) {

            // 移除 redis中的 cookie，然后再执行一次本方法
            CacheRedisKafkaLocalUtil.remove(RedisKeyEnum.XXL_JOB_COOKIE_CACHE, null);
            setJobGroup(dto, null);

        }

    }

    /**
     * 获取 cookie
     */
    private HttpCookie getCookie() {

        JSONObject jsonObject = MyCacheUtil.getMap(RedisKeyEnum.XXL_JOB_COOKIE_CACHE, null, () -> {

            JSONObject formJson =
                JSONUtil.createObj().set("userName", userName).set("password", password).set("ifRemember", "on");

            HttpResponse response =
                HttpRequest.post(adminAddresses + "/login").form(formJson).timeout(BaseConstant.MINUTE_1_EXPIRE_TIME)
                    .execute();

            if (BooleanUtil.isFalse(response.isOk())) {
                ApiResultVO.errorMsg("操作失败：登录 xxl-job失败：" + JSONUtil.parseObj(response.body()));
            }

            List<HttpCookie> cookieList = response.getCookies();

            if (CollUtil.isEmpty(cookieList)) {
                ApiResultVO.errorMsg("操作失败：登录 xxl-job失败：cookieList 为空");
            }

            HttpCookie httpCookie = cookieList.get(0);

            return JSONUtil.createObj().set("name", httpCookie.getName()).set("value", httpCookie.getValue());

        });

        return new HttpCookie(jsonObject.getStr("name"), jsonObject.getStr("value"));

    }

    /**
     * 通过 任务 id，删除 xxl-job 任务
     */
    @Override
    public void deleteById(NotNullId notNullId) {

        if (notNullId.getId() == null) {
            ApiResultVO.errorMsg("操作失败：删除任务失败：id 不能为空");
        }

        HttpCookie cookie = getCookie();

        try {

            JSONObject formJson = JSONUtil.createObj().set("id", notNullId.getId());

            HttpResponse response = HttpRequest.post(adminAddresses + "/jobinfo/remove").form(formJson).cookie(cookie)
                .timeout(BaseConstant.MINUTE_1_EXPIRE_TIME).execute();

            JSONObject bodyJson = JSONUtil.parseObj(response.body());

            if (!response.isOk()) {
                ApiResultVO.errorMsg("操作失败：删除任务失败：" + bodyJson);
            }

        } catch (Exception e) {

            // 移除 redis中的 cookie，然后再执行一次本方法
            CacheRedisKafkaLocalUtil.remove(RedisKeyEnum.XXL_JOB_COOKIE_CACHE, null);
            deleteById(notNullId);

        }

    }

}
