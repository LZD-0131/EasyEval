package com.dong.easyeval.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "admin.security")
public class AdminSecurityProperties {

    private List<String> allowedIps = new ArrayList<>(List.of("127.0.0.1", "::1", "192.168.20.102"));

    public List<String> getAllowedIps() {
        return allowedIps;
    }

    public void setAllowedIps(List<String> allowedIps) {
        this.allowedIps = allowedIps;
    }
}
