package com.dong.easyeval.config;

import com.dong.easyeval.fillter.IpFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<IpFilter> ipFilter(AdminSecurityProperties adminSecurityProperties) {
        FilterRegistrationBean<IpFilter> registrationBean = new FilterRegistrationBean<>();
        Set<String> allowedIps = new HashSet<>(adminSecurityProperties.getAllowedIps());
        registrationBean.setFilter(new IpFilter(allowedIps));
        registrationBean.addUrlPatterns("/api/admin/pay");
        return registrationBean;
    }
}
