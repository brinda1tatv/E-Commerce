package com.eCommerce.helper;

import javax.servlet.http.HttpServletRequest;

public class GetContextPath {

    public String getProjectBaseURL(HttpServletRequest request) {

        String requestURL = request.getRequestURL().toString();
        String requestURI = request.getRequestURI();

        String baseURL = requestURL.substring(0, requestURL.indexOf(requestURI));

        String contextPath = request.getContextPath();

        if (contextPath != null && !contextPath.isEmpty()) {
            baseURL = baseURL + contextPath;
        }

        return baseURL;
    }

}
