package com.grocerymanagementsys.staff.utility;

import com.grocerymanagementsys.staff.interfaces.AdminOnly;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Component
public class StaffAuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI().substring(request.getContextPath().length());
        // The login page and the (hidden) sign-up page are reachable without being logged in.
        boolean loginPage = path.equals("/staff/login") || path.equals("/staff/signup");
        HttpSession session = request.getSession(false);
        StaffUser user = StaffSession.current(session);

        if (!loginPage && user == null) {
            response.sendRedirect(request.getContextPath() + "/staff/login");
            return false;
        }

        if ("POST".equals(request.getMethod()) && !validToken(session, request.getParameter("_csrf"))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid or missing form token. Reload the page and try again.");
            return false;
        }

        if (user != null && !user.isAdmin() && isAdminOnly(handler)) {
            response.sendRedirect(request.getContextPath() + "/staff/inventory/items?denied");
            return false;
        }
        return true;
    }

    private boolean isAdminOnly(Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            return false;
        }
        HandlerMethod method = (HandlerMethod) handler;
        return method.hasMethodAnnotation(AdminOnly.class)
                || method.getBeanType().isAnnotationPresent(AdminOnly.class);
    }

    private boolean validToken(HttpSession session, String submitted) {
        if (session == null || submitted == null) {
            return false;
        }
        String expected = (String) session.getAttribute(StaffSession.CSRF);
        return expected != null && MessageDigest.isEqual(
                expected.getBytes(StandardCharsets.UTF_8), submitted.getBytes(StandardCharsets.UTF_8));
    }
}
