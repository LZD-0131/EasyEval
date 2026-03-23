package com.dong.easyeval.service;

import com.dong.easyeval.common.WxOAuthCodeException;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import me.chanjar.weixin.common.error.WxError;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.common.service.WxOAuth2Service;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class WxOAuthCodeServiceTest {

    @Test
    void resolveOpenIdShouldReturnCachedValueWithoutCallingWechat() throws Exception {
        WxMpService wxMpService = mock(WxMpService.class);
        JedisPool jedisPool = mock(JedisPool.class);
        Jedis jedis = mock(Jedis.class);
        when(jedisPool.getResource()).thenReturn(jedis);
        when(jedis.get("wx:oauth:code:code-1")).thenReturn("openid-1");

        WxOAuthCodeService service = new WxOAuthCodeService(wxMpService, jedisPool);

        String openId = service.resolveOpenId("code-1");

        assertEquals("openid-1", openId);
        verifyNoInteractions(wxMpService);
    }

    @Test
    void resolveOpenIdShouldCacheOpenIdAfterSuccessfulWechatExchange() throws Exception {
        WxMpService wxMpService = mock(WxMpService.class);
        WxOAuth2Service oAuth2Service = mock(WxOAuth2Service.class);
        WxOAuth2AccessToken accessToken = mock(WxOAuth2AccessToken.class);
        JedisPool jedisPool = mock(JedisPool.class);
        Jedis jedis = mock(Jedis.class);

        when(jedisPool.getResource()).thenReturn(jedis);
        when(jedis.get("wx:oauth:code:code-1")).thenReturn(null);
        when(wxMpService.getOAuth2Service()).thenReturn(oAuth2Service);
        when(oAuth2Service.getAccessToken("code-1")).thenReturn(accessToken);
        when(accessToken.getOpenId()).thenReturn("openid-1");

        WxOAuthCodeService service = new WxOAuthCodeService(wxMpService, jedisPool);

        String openId = service.resolveOpenId("code-1");

        assertEquals("openid-1", openId);
        verify(jedis).setex("wx:oauth:code:code-1", 300, "openid-1");
    }

    @Test
    void resolveOpenIdShouldUseCacheWhenWechatReportsCodeAlreadyUsed() throws Exception {
        WxMpService wxMpService = mock(WxMpService.class);
        WxOAuth2Service oAuth2Service = mock(WxOAuth2Service.class);
        JedisPool jedisPool = mock(JedisPool.class);
        Jedis jedis = mock(Jedis.class);

        when(jedisPool.getResource()).thenReturn(jedis);
        when(jedis.get("wx:oauth:code:code-1")).thenReturn(null, "openid-1");
        when(wxMpService.getOAuth2Service()).thenReturn(oAuth2Service);
        when(oAuth2Service.getAccessToken("code-1"))
                .thenThrow(new WxErrorException(new WxError(40163, "code been used")));

        WxOAuthCodeService service = new WxOAuthCodeService(wxMpService, jedisPool);

        String openId = service.resolveOpenId("code-1");

        assertEquals("openid-1", openId);
    }

    @Test
    void resolveOpenIdShouldThrowFriendlyMessageWhenCodeAlreadyUsedAndCacheMissing() throws Exception {
        WxMpService wxMpService = mock(WxMpService.class);
        WxOAuth2Service oAuth2Service = mock(WxOAuth2Service.class);
        JedisPool jedisPool = mock(JedisPool.class);
        Jedis jedis = mock(Jedis.class);

        when(jedisPool.getResource()).thenReturn(jedis);
        when(jedis.get("wx:oauth:code:code-1")).thenReturn(null).thenReturn((String) null);
        when(wxMpService.getOAuth2Service()).thenReturn(oAuth2Service);
        when(oAuth2Service.getAccessToken("code-1"))
                .thenThrow(new WxErrorException(new WxError(40163, "code been used")));

        WxOAuthCodeService service = new WxOAuthCodeService(wxMpService, jedisPool);

        WxOAuthCodeException exception = assertThrows(WxOAuthCodeException.class, () -> service.resolveOpenId("code-1"));

        assertEquals(40163, ((WxErrorException) exception.getCause()).getError().getErrorCode());
    }
}
