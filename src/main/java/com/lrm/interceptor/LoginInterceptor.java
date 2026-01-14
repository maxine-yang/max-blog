package com.lrm.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Author: maxine yang
 * Login interceptor: protects all /admin/** paths (except /admin and /admin/login)
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(LoginInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        Object user = request.getSession().getAttribute("user");
        
        logger.info("Interceptor check: URI={}, user={}", requestURI, user != null ? "logged in" : "not logged in");
        
        if (user == null) {
            logger.warn("Unauthenticated user attempted to access protected admin page: {}, redirecting to login", requestURI);
            response.sendRedirect("/admin");
            return false; // Block request from continuing
        }
        
        logger.debug("User is logged in, allowing access: {}", requestURI);
        return true; // Allow request to continue
    }
}
