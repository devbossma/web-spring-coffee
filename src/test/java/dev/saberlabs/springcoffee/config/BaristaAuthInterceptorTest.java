package dev.saberlabs.springcoffee.config;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;

class BaristaAuthInterceptorTest {

    private final BaristaAuthInterceptor interceptor = new BaristaAuthInterceptor();

    @Test
    void blocksAndRedirectsWhenNotLoggedIn() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        boolean proceed = interceptor.preHandle(request, response, null);

        assertThat(proceed).isFalse();
        assertThat(response.getRedirectedUrl()).isEqualTo("/barista/login");
    }

    @Test
    void letsTheRequestThroughWhenLoggedIn() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.getSession(true).setAttribute(BaristaAuthInterceptor.SESSION_ATTRIBUTE, "barista");
        MockHttpServletResponse response = new MockHttpServletResponse();

        boolean proceed = interceptor.preHandle(request, response, null);

        assertThat(proceed).isTrue();
        assertThat(response.getRedirectedUrl()).isNull();
    }
}
