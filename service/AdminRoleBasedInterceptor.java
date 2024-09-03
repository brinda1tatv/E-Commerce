package com.eCommerce.service;

import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;

@Service
public class AdminRoleBasedInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;

            Integer userRole = getUserRoleFromSession(request);

            System.out.println("roleInterceptor");

            String requestURI = request.getRequestURI();
            if (requestURI.startsWith("/admin/home")){
                return true;
            }

            if (hasAnnotation(handlerMethod, MasterAdminTask.class) && userRole!=1) {
                response.sendRedirect(request.getContextPath() + "/noaccess");
                return false;
            }
            if (hasAnnotation(handlerMethod, SalesAdminTask.class) && userRole!=3) {
                response.sendRedirect(request.getContextPath() + "/noaccess");
                return false;
            }
            if (hasAnnotation(handlerMethod, InventoryAdminTask.class) && userRole!=4) {
                response.sendRedirect(request.getContextPath() + "/noaccess");
                return false;
            }
        }
        return true;
    }

    private boolean hasAnnotation(HandlerMethod handlerMethod, Class<? extends Annotation> annotationClass) {
        return handlerMethod.getMethodAnnotation(annotationClass) != null ||
                handlerMethod.getBeanType().getAnnotation(annotationClass) != null;
    }

    private Integer getUserRoleFromSession(HttpServletRequest request) {
        return (Integer) request.getSession().getAttribute("roleId");
    }
}
