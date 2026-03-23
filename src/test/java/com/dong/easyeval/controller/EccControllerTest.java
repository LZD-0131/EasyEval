package com.dong.easyeval.controller;

import com.dong.easyeval.common.ApiResponse;
import com.dong.easyeval.common.WxOAuthCodeException;
import com.dong.easyeval.request.EccSubmitRequest;
import com.dong.easyeval.service.ITCorrectionCountService;
import com.dong.easyeval.service.ITCorrectionRequestService;
import com.dong.easyeval.service.ITUserService;
import com.dong.easyeval.service.WxOAuthCodeService;
import com.dong.easyeval.service.YoudaoEssayEvalService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class EccControllerTest {

    @Test
    void submitShouldReturnFriendlyMessageWhenOauthCodeAlreadyUsed() throws Exception {
        EccController controller = new EccController();
        YoudaoEssayEvalService essayEvalService = mock(YoudaoEssayEvalService.class);
        JedisPool jedisPool = mock(JedisPool.class);
        Jedis jedis = mock(Jedis.class);
        WxOAuthCodeService wxOAuthCodeService = mock(WxOAuthCodeService.class);

        ReflectionTestUtils.setField(controller, "essayEvalService", essayEvalService);
        ReflectionTestUtils.setField(controller, "objectMapper", new ObjectMapper());
        ReflectionTestUtils.setField(controller, "countService", mock(ITCorrectionCountService.class));
        ReflectionTestUtils.setField(controller, "correctionRequestService", mock(ITCorrectionRequestService.class));
        ReflectionTestUtils.setField(controller, "userService", mock(ITUserService.class));
        ReflectionTestUtils.setField(controller, "jedisPool", jedisPool);
        ReflectionTestUtils.setField(controller, "wxOAuthCodeService", wxOAuthCodeService);

        when(jedisPool.getResource()).thenReturn(jedis);
        when(jedis.set(anyString(), anyString(), any())).thenReturn("OK");
        when(wxOAuthCodeService.resolveOpenId("used-code"))
                .thenThrow(new WxOAuthCodeException("当前页面授权已被使用，请返回公众号菜单重新进入后再提交"));

        EccSubmitRequest request = new EccSubmitRequest();
        request.setTitle("Test title");
        request.setContent("Test content");
        request.setGrade("high");
        request.setCode("used-code");

        ApiResponse<Object> response = controller.submit(request);

        assertEquals(500, response.getStatus());
        assertEquals("当前页面授权已被使用，请返回公众号菜单重新进入后再提交", response.getMessage());
        verifyNoInteractions(essayEvalService);
    }
}
