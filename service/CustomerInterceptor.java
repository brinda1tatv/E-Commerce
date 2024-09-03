package com.eCommerce.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Service
public class CustomerInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        HttpSession session = request.getSession(false);

        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/noaccess");
            return false;
        }

        Integer roleId = (Integer) session.getAttribute("roleId");

        Integer userId = (Integer) session.getAttribute("userId");

        System.out.println("customerinterceptor");

        Boolean isBlocked = (Boolean) session.getAttribute("isBlocked");
        Boolean isDeleted = (Boolean) session.getAttribute("isDeleted");

        if (isBlocked == null) isBlocked = false;
        if (isDeleted == null) isDeleted = false;

        if (!isBlocked && !isDeleted && userId != null && roleId != null && roleId==2) {
            return true;
        } else {
            response.sendRedirect(request.getContextPath() + "/noaccess");
            return false;
        }

    }
}
