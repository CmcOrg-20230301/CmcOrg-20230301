package com.cmcorg20230301.engine.be.netty.websocket.service.impl;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.http.Header;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.admin.common.configuration.BaseConfiguration;
import com.admin.common.configuration.JsonRedisTemplate;
import com.admin.common.exception.BaseBizCodeEnum;
import com.admin.common.model.constant.BaseConstant;
import com.admin.common.model.dto.NotEmptyIdSet;
import com.admin.common.model.dto.NotNullByte;
import com.admin.common.model.dto.NotNullByteAndId;
import com.admin.common.model.entity.BaseEntity;
import com.admin.common.model.entity.BaseEntityThree;
import com.admin.common.model.entity.BaseEntityTwo;
import com.admin.common.model.vo.ApiResultVO;
import com.admin.common.util.*;
import com.admin.websocket.configuration.NettyServer;
import com.admin.websocket.mapper.SysWebSocketMapper;
import com.admin.websocket.model.dto.SysWebSocketPageDTO;
import com.admin.websocket.model.entity.SysWebSocketDO;
import com.admin.websocket.model.enums.SysWebSocketTypeEnum;
import com.admin.websocket.model.vo.SysWebSocketRegisterVO;
import com.admin.websocket.service.SysWebSocketService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SysWebSocketServiceImpl extends ServiceImpl<SysWebSocketMapper, SysWebSocketDO>
    implements SysWebSocketService {

    @Resource
    HttpServletRequest httpServletRequest;
    @Resource
    JsonRedisTemplate<SysWebSocketDO> jsonRedisTemplate;

    /**
     * 启动项目 或者springboot销毁时：断开当前 WebSocket 的所有连接
     */
    @Override
    @Transactional
    public void offlineAllForCurrent() {

        log.info("下线数据库的 WebSocket 连接，条件：server = {}", NettyServer.ipAndPort);

        lambdaUpdate().eq(SysWebSocketDO::getServer, NettyServer.ipAndPort).eq(SysWebSocketDO::getEnableFlag, true)
            .set(SysWebSocketDO::getEnableFlag, false).set(BaseEntity::getUpdateTime, new Date())
            .set(BaseEntity::getUpdateId, UserUtil.getCurrentUserIdDefault()).update(); // 更新

    }

    /**
     * 强退，通过 idSet
     */
    @Override
    @Transactional
    public String retreatAndNoticeByIdSet(NotEmptyIdSet notEmptyIdSet) {

        List<SysWebSocketDO> sysWebSocketDOList =
            lambdaQuery().in(BaseEntityTwo::getId, notEmptyIdSet.getIdSet()).eq(SysWebSocketDO::getEnableFlag, true)
                .groupBy(SysWebSocketDO::getJwtHash).select(SysWebSocketDO::getJwtHash).list();

        if (sysWebSocketDOList.size() != 0) {

            Set<String> jwtHashSet =
                sysWebSocketDOList.stream().map(SysWebSocketDO::getJwtHash).collect(Collectors.toSet());

            jsonRedisTemplate.delete(jwtHashSet);

            offlineByWebSocketIdSet(notEmptyIdSet.getIdSet()); // 更新

            // 并且给 消息中间件推送，进行下线操作
            KafkaUtil.loginExpired(notEmptyIdSet.getIdSet());

        }

        return BaseBizCodeEnum.API_RESULT_OK.getMsg();
    }

    /**
     * 全部强退
     */
    @Override
    @Transactional
    public String retreatAndNoticeAll() {

        MyJwtUtil.removeAllJwtHash();

        List<SysWebSocketDO> sysWebSocketDOList =
            lambdaQuery().eq(BaseEntityThree::getEnableFlag, true).select(BaseEntityTwo::getId).list();

        if (sysWebSocketDOList.size() != 0) {

            Set<Long> webSocketIdSet =
                sysWebSocketDOList.stream().map(BaseEntityTwo::getId).collect(Collectors.toSet());

            lambdaUpdate().eq(SysWebSocketDO::getEnableFlag, true).set(SysWebSocketDO::getEnableFlag, false)
                .set(BaseEntity::getUpdateId, UserUtil.getCurrentUserIdDefault())
                .set(BaseEntityTwo::getUpdateTime, new Date()).update(); // 更新

            // 并且给 消息中间件推送，进行下线操作
            KafkaUtil.loginExpired(webSocketIdSet);
        }

        return BaseBizCodeEnum.API_RESULT_OK.getMsg();
    }

    /**
     * 获取 webSocket连接地址和随机码
     */
    @Override
    public SysWebSocketRegisterVO register(NotNullByte notNullByte) {

        Byte value = notNullByte.getValue();

        SysWebSocketDO sysWebSocketDO = new SysWebSocketDO(); // 存放到redis，用于匹配连接

        SysWebSocketTypeEnum sysWebSocketTypeEnum = SysWebSocketTypeEnum.getByCode(value);
        if (sysWebSocketTypeEnum == null) {
            ApiResultVO.error(BaseBizCodeEnum.PARAMETER_CHECK_ERROR);
        }

        sysWebSocketDO.setType(sysWebSocketTypeEnum);

        setWebSocketForRegister(sysWebSocketDO); // 设置：webSocketDO的其他属性值

        String uuid = IdUtil.simpleUUID();

        // 放到 redis里面，一段时间后自动过期
        ValueOperations<String, SysWebSocketDO> ops = jsonRedisTemplate.opsForValue();

        ops.set(NettyServer.webSocketRegisterCodePreKey + uuid, sysWebSocketDO, BaseConstant.SECOND_30_EXPIRE_TIME,
            TimeUnit.MILLISECONDS);

        SysWebSocketRegisterVO sysWebSocketRegisterVO = new SysWebSocketRegisterVO();

        // 注意：这里需要额外处理一下返回的地址，如果是 ip，则返回 ip:port，反之则返回 webSocketAddress
        sysWebSocketRegisterVO.setWebSocketUrl(
            Validator.isIpv4(BaseConfiguration.adminProperties.getWebSocketAddress()) ? NettyServer.ipAndPort :
                BaseConfiguration.adminProperties.getWebSocketAddress());

        sysWebSocketRegisterVO.setCode(uuid);

        return sysWebSocketRegisterVO;
    }

    /**
     * {@link com.admin.websocket.configuration.MyNettyWebSocketHandler#channelRead}
     * 设置：webSocketDO的其他属性值
     */
    private void setWebSocketForRegister(SysWebSocketDO sysWebSocketDO) {

        Long currentUserId = UserUtil.getCurrentUserId();

        sysWebSocketDO.setCreateId(currentUserId);
        sysWebSocketDO.setUpdateId(currentUserId);

        String ip = ServletUtil.getClientIP(httpServletRequest);
        sysWebSocketDO.setIp(ip);

        String uaStr = httpServletRequest.getHeader(Header.USER_AGENT.getValue());

        UserAgent ua = UserAgentUtil.parse(uaStr);

        sysWebSocketDO.setRegion(IpUtil.getRegion(ip));
        sysWebSocketDO.setBrowser(ua.getBrowser().toString() + "/" + ua.getVersion());
        sysWebSocketDO.setOs(ua.getOs().toString());
        sysWebSocketDO.setMobileFlag(ua.isMobile());
        sysWebSocketDO.setServer(NettyServer.ipAndPort);

        sysWebSocketDO.setCategory(RequestUtil.getSysRequestCategoryEnum(httpServletRequest));

        // 备注：这里不用担心 jwt不存在的问题，因为 JwtAuthorizationFilter已经处理过了，所以这里一定会有 jwt
        sysWebSocketDO.setJwtHash(MyJwtUtil
            .generateRedisJwtHash(httpServletRequest.getHeader(BaseConstant.JWT_HEADER_KEY),
                sysWebSocketDO.getCreateId(), sysWebSocketDO.getCategory()));

    }

    /**
     * 分页排序查询
     */
    @Override
    public Page<SysWebSocketDO> myPage(SysWebSocketPageDTO dto) {
        return lambdaQuery().eq(dto.getId() != null, BaseEntityTwo::getId, dto.getId())
            .eq(dto.getCreateId() != null, BaseEntityTwo::getCreateId, dto.getCreateId())
            .like(StrUtil.isNotBlank(dto.getRegion()), SysWebSocketDO::getRegion, dto.getRegion())
            .like(StrUtil.isNotBlank(dto.getIp()), SysWebSocketDO::getIp, dto.getIp())
            .like(StrUtil.isNotBlank(dto.getBrowser()), SysWebSocketDO::getBrowser, dto.getBrowser())
            .like(StrUtil.isNotBlank(dto.getOs()), SysWebSocketDO::getOs, dto.getOs())
            .eq(dto.getMobileFlag() != null, SysWebSocketDO::getMobileFlag, dto.getMobileFlag())
            .eq(dto.getType() != null, SysWebSocketDO::getType, dto.getType())
            .like(StrUtil.isNotBlank(dto.getServer()), SysWebSocketDO::getServer, dto.getServer())
            .eq(dto.getEnableFlag() != null, SysWebSocketDO::getEnableFlag, dto.getEnableFlag())
            .eq(dto.getCategory() != null, SysWebSocketDO::getCategory, dto.getCategory())
            .le(dto.getEndCreateTime() != null, BaseEntity::getCreateTime, dto.getEndCreateTime())
            .ge(dto.getBeginCreateTime() != null, BaseEntity::getCreateTime, dto.getBeginCreateTime())
            .eq(BaseEntityThree::getDelFlag, false).orderByDesc(dto.notHasOrder(), BaseEntity::getCreateTime)
            .page(dto.getPage(true));
    }

    /**
     * 更改在线状态
     */
    @Override
    @Transactional
    public String changeType(NotNullByteAndId notNullByteAndId) {

        SysWebSocketTypeEnum sysWebSocketTypeEnum = SysWebSocketTypeEnum.getByCode(notNullByteAndId.getValue());

        if (sysWebSocketTypeEnum == null) {
            ApiResultVO.error(BaseBizCodeEnum.PARAMETER_CHECK_ERROR);
        }

        SysWebSocketDO sysWebSocketDO = new SysWebSocketDO();

        sysWebSocketDO.setId(notNullByteAndId.getId());
        sysWebSocketDO.setType(sysWebSocketTypeEnum);

        updateById(sysWebSocketDO);

        return BaseBizCodeEnum.API_RESULT_OK.getMsg();
    }

    /**
     * 离线：数据库的连接数据，通过 webSocketIdSet
     */
    @Override
    @Transactional
    public void offlineByWebSocketIdSet(Set<Long> webSocketIdSet) {

        lambdaUpdate().in(BaseEntityTwo::getId, webSocketIdSet).eq(SysWebSocketDO::getEnableFlag, true)
            .set(SysWebSocketDO::getEnableFlag, false).set(BaseEntity::getUpdateTime, new Date())
            .set(BaseEntity::getUpdateId, UserUtil.getCurrentUserIdDefault()).update(); // 更新

    }

    /**
     * 离线：数据库的连接数据，通过 userIdSet
     */
    @Override
    public void offlineByUserIdSet(Set<Long> userIdSet) {

        lambdaUpdate().in(BaseEntity::getCreateId, userIdSet).eq(SysWebSocketDO::getEnableFlag, true)
            .set(SysWebSocketDO::getEnableFlag, false).set(BaseEntityTwo::getUpdateTime, new Date())
            .set(BaseEntity::getUpdateId, UserUtil.getCurrentUserIdDefault()).update(); // 更新

    }

}




