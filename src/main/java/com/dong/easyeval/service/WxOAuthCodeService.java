package com.dong.easyeval.service;

import com.dong.easyeval.common.WxOAuthCodeException;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Slf4j
@Service
public class WxOAuthCodeService {

    private static final String CODE_CACHE_PREFIX = "wx:oauth:code:";
    private static final int CODE_CACHE_SECONDS = 300;

    private final WxMpService wxMpService;
    private final JedisPool jedisPool;

    public WxOAuthCodeService(WxMpService wxMpService, JedisPool jedisPool) {
        this.wxMpService = wxMpService;
        this.jedisPool = jedisPool;
    }

    public String resolveOpenId(String code) throws WxErrorException {
        if (!StringUtils.hasText(code)) {
            throw new WxOAuthCodeException("未获取到微信授权信息，请从公众号菜单重新进入页面后再试");
        }

        String cachedOpenId = getCachedOpenId(code);
        if (StringUtils.hasText(cachedOpenId)) {
            return cachedOpenId;
        }

        try {
            WxOAuth2AccessToken accessToken = wxMpService.getOAuth2Service().getAccessToken(code);
            String openId = accessToken.getOpenId();
            cacheOpenId(code, openId);
            return openId;
        } catch (WxErrorException exception) {
            int errorCode = exception.getError() == null ? -1 : exception.getError().getErrorCode();
            if (errorCode == 40163) {
                cachedOpenId = getCachedOpenId(code);
                if (StringUtils.hasText(cachedOpenId)) {
                    return cachedOpenId;
                }
                throw new WxOAuthCodeException("当前页面授权已被使用，请返回公众号菜单重新进入后再提交", exception);
            }
            if (errorCode == 40029) {
                throw new WxOAuthCodeException("当前页面授权已失效，请返回公众号菜单重新进入后再试", exception);
            }
            throw exception;
        }
    }

    private String getCachedOpenId(String code) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.get(CODE_CACHE_PREFIX + code);
        } catch (Exception exception) {
            log.warn("读取微信 OAuth code 缓存失败, code={}", code, exception);
            return null;
        }
    }

    private void cacheOpenId(String code, String openId) {
        if (!StringUtils.hasText(openId)) {
            return;
        }
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.setex(CODE_CACHE_PREFIX + code, CODE_CACHE_SECONDS, openId);
        } catch (Exception exception) {
            log.warn("缓存微信 OAuth code 失败, code={}, openId={}", code, openId, exception);
        }
    }
}
