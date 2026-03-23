package com.dong.easyeval.controller;

import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HEllo {

    @Autowired
    private WxMpService wxMpService;

    @GetMapping("/getUrl")
    public String getUrl(@RequestParam String url) {
        return wxMpService.getOAuth2Service().buildAuthorizationUrl(url, "snsapi_base", "state");
    }
}
