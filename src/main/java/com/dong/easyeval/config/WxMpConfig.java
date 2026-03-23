package com.dong.easyeval.config;

import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class WxMpConfig {

    @Value("${wx.mp.app-id}")
    private String appId;

    @Value("${wx.mp.secret}")
    private String secret;

    @Value("${wx.mp.token}")
    private String token;

    @Bean
    public WxMpService wxMpService() {
        WxMpDefaultConfigImpl config = new WxMpDefaultConfigImpl();
        config.setAppId(appId);
        config.setSecret(secret);
        config.setToken(token);

        WxMpService service = new WxMpServiceImpl();
        
        // 关键修复：使用 Map 注册多套配置（或者至少注册一套默认的）
        Map<String, me.chanjar.weixin.mp.config.WxMpConfigStorage> configStorages = new HashMap<>();
        configStorages.put("default", config);
        configStorages.put(appId, config); // 同时用 AppID 注册，双重保险
        
        service.setMultiConfigStorages(configStorages, "default");
        return service;
    }
}
