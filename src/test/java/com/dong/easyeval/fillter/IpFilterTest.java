package com.dong.easyeval.fillter;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class IpFilterTest {

    @Test
    void shouldAllowFirstForwardedIpWhenItIsWhitelisted() throws Exception {
        IpFilter ipFilter = new IpFilter(Set.of("116.27.5.255"));
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);
        request.addHeader("X-Forwarded-For", "116.27.5.255, 10.0.0.2");

        ipFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldRejectIpOutsideWhitelist() throws Exception {
        IpFilter ipFilter = new IpFilter(Set.of("127.0.0.1"));
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);
        request.setRemoteAddr("8.8.8.8");

        ipFilter.doFilter(request, response, filterChain);

        assertEquals(403, response.getStatus());
        assertTrue(response.getContentAsString().contains("Access denied"));
    }
}
