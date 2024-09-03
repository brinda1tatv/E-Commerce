package com.eCommerce.service;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Service
public class AdminInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        HttpSession session = request.getSession(false);

        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/noaccess");
            return false;
        }

        Integer roleId = (Integer) session.getAttribute("roleId");
        Integer userId = (Integer) session.getAttribute("userId");
        Boolean isBlocked = (Boolean) session.getAttribute("isBlocked");
        Boolean isDeleted = (Boolean) session.getAttribute("isDeleted");

        if (isBlocked == null) isBlocked = false;
        if (isDeleted == null) isDeleted = false;

        System.out.println("admininterceptor");

        if (!isBlocked && !isDeleted && userId != null && roleId != null && (roleId == 1 || roleId==3 || roleId==4)) {
            return true;
        } else {
            response.sendRedirect(request.getContextPath() + "/noaccess");
            return false;
        }
    }

}
