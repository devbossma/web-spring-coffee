package dev.saberlabs.springcoffee.config;

import dev.saberlabs.springcoffee.singleton.DailySequenceGenerator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Gives every browser session a receipt-style id the first time it shows up, so there is
 * something to tell "my orders" apart by without ever asking for a name.
 */
@Component
public class CustomerIdentificationInterceptor implements HandlerInterceptor {

    public static final String SESSION_ATTRIBUTE = "customerId";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        var session = request.getSession(true);
        if (session.getAttribute(SESSION_ATTRIBUTE) == null) {
            session.setAttribute(SESSION_ATTRIBUTE, DailySequenceGenerator.getInstance().nextCustomerId());
        }
        return true;
    }
}
