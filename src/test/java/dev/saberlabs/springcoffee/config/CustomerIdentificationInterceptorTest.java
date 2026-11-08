package dev.saberlabs.springcoffee.config;

import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerIdentificationInterceptorTest {

    private final CustomerIdentificationInterceptor interceptor = new CustomerIdentificationInterceptor();

    @Test
    void assignsACustomerIdOnFirstVisit() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        boolean proceed = interceptor.preHandle(request, new MockHttpServletResponse(), null);

        assertThat(proceed).isTrue();
        HttpSession session = request.getSession(false);
        assertThat(session).isNotNull();
        assertThat((String) session.getAttribute(CustomerIdentificationInterceptor.SESSION_ATTRIBUTE))
                .startsWith("CUST-");
    }

    @Test
    void leavesAnExistingCustomerIdAlone() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.getSession(true).setAttribute(CustomerIdentificationInterceptor.SESSION_ATTRIBUTE, "CUST-FIXED");

        interceptor.preHandle(request, new MockHttpServletResponse(), null);

        assertThat(request.getSession(false).getAttribute(CustomerIdentificationInterceptor.SESSION_ATTRIBUTE))
                .isEqualTo("CUST-FIXED");
    }
}
