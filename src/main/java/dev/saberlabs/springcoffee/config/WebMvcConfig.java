package dev.saberlabs.springcoffee.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final CustomerIdentificationInterceptor customerIdentificationInterceptor;
    private final BaristaAuthInterceptor baristaAuthInterceptor;

    public WebMvcConfig(CustomerIdentificationInterceptor customerIdentificationInterceptor,
                         BaristaAuthInterceptor baristaAuthInterceptor) {
        this.customerIdentificationInterceptor = customerIdentificationInterceptor;
        this.baristaAuthInterceptor = baristaAuthInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(customerIdentificationInterceptor)
                .excludePathPatterns("/barista/**", "/css/**", "/js/**");
        registry.addInterceptor(baristaAuthInterceptor)
                .addPathPatterns("/barista/console", "/barista/orders/**")
                .excludePathPatterns("/barista/login", "/barista/logout");
    }
}
