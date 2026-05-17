package by.java.enterprise.jwtservice.config;

import by.java.enterprise.jwtservice.CurrentUserArgumentResolver;
import by.java.enterprise.jwtservice.RoleCheckInterceptor;
import by.java.enterprise.jwtservice.filter.JwtAuthenticationFilter;
import by.java.enterprise.jwtservice.service.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@AutoConfiguration
public class JwtAutoConfiguration  implements WebMvcConfigurer {

    @Bean
    @ConditionalOnMissingBean
    public JwtService jwtService(@Value("${jwt.secret}") String secret) {
        return new JwtService(secret);
    }

    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtFilter(JwtService jwtService) {
        FilterRegistrationBean<JwtAuthenticationFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new JwtAuthenticationFilter(jwtService));
        registration.addUrlPatterns("/*");
        registration.setOrder(1);

        return registration;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new RoleCheckInterceptor());
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new CurrentUserArgumentResolver());
    }
}
