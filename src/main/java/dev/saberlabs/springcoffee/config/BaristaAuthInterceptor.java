package dev.saberlabs.springcoffee.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

/** Gate for everything under {@code /barista} except the login page itself. */
@Component
public class BaristaAuthInterceptor implements HandlerInterceptor {

    public static final String SESSION_ATTRIBUTE = "barista";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException {
        var session = request.getSession(true);
        if (session.getAttribute(SESSION_ATTRIBUTE) != null) {
            return true;
        }
        response.sendRedirect(request.getContextPath() + "/barista/login");
        return false;
    }
}
