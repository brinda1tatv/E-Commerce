package com.eCommerce.service;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class CarrierInteceptor implements HandlerInterceptor {

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

        System.out.println("carrierInterceptor");

        if (!isBlocked && !isDeleted && userId != null && roleId != null && roleId == 5) {
            return true;
        } else {
            response.sendRedirect(request.getContextPath() + "/noaccess");
            return false;
        }

    }
}
