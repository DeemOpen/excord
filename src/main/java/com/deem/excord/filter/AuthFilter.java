package com.deem.excord.filter;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.catalina.comet.CometEvent;
import org.apache.catalina.comet.CometFilterChain;
import org.apache.catalina.filters.RequestFilter;
import org.apache.juli.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class AuthFilter extends RequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(AuthFilter.class);

    @Bean
    public FilterRegistrationBean remoteAddressFilter() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new AuthFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        return filterRegistrationBean;
    }

    @Override
    public void doFilter(ServletRequest sRequest, ServletResponse sResponse, FilterChain fc) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) sRequest;
        HttpServletResponse response = (HttpServletResponse) sResponse;
        String nextUrl;
        if (request.getQueryString() != null) {
            nextUrl = request.getRequestURI() + "?" + request.getQueryString();
        } else {
            nextUrl = request.getRequestURI();
        }

        if (!request.getRequestURI().equals("/") && !request.getRequestURI().contains("/login") && !request.getRequestURI().startsWith("/fonts/") && !request.getRequestURI().startsWith("/css/") && !request.getRequestURI().startsWith("/js/") && !request.getRequestURI().startsWith("/images/")) {
            //Create session if one doesnt exist.
            HttpSession session = request.getSession(true);
            if (session.getAttribute("authName") == null) {
                if (!request.getRequestURI().equals("/login")) {
                    session.setAttribute("nextUrl", nextUrl);
                }
                response.sendRedirect("/login");
                return;
            }
        }
        fc.doFilter(sRequest, sResponse);
    }

    @Override
    protected Log getLogger() {
        return null;
    }

    @Override
    public void doFilterEvent(CometEvent ce, CometFilterChain cfc) throws IOException, ServletException {

    }

}
