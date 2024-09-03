package com.eCommerce.service;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionRegistry {

    private static final Map<String, HttpSession> sessions = new ConcurrentHashMap<>();

    public static void addSession(HttpSession session) {
        sessions.put(session.getId(), session);
    }

    public static void removeSession(String sessionId) {
        sessions.remove(sessionId);
    }

    public static HttpSession getSession(String sessionId) {
        return sessions.get(sessionId);
    }

}
