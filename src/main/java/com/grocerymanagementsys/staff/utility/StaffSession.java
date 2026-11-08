package com.grocerymanagementsys.staff.utility;

import jakarta.servlet.http.HttpSession;
import java.security.SecureRandom;
import java.util.Base64;

public final class StaffSession {
    public static final String USER = "staffUser";
    public static final String CSRF = "staffCsrf";
    private static final SecureRandom RANDOM = new SecureRandom();

    private StaffSession() { }

    public static StaffUser current(HttpSession session) {
        return session == null ? null : (StaffUser) session.getAttribute(USER);
    }

    public static StaffUser require(HttpSession session) {
        StaffUser user = current(session);
        if (user == null) {
            throw new IllegalStateException("No staff user in session");
        }
        return user;
    }

    public static String csrfToken(HttpSession session) {
        String token = (String) session.getAttribute(CSRF);
        if (token == null) {
            byte[] bytes = new byte[32];
            RANDOM.nextBytes(bytes);
            token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
            session.setAttribute(CSRF, token);
        }
        return token;
    }
}
