package by.java.enterprise.jwtservice;

import by.java.enterprise.jwtservice.annotation.CurrentUserId;
import by.java.enterprise.jwtservice.annotation.CurrentUserRole;
import by.java.enterprise.jwtservice.storage.UserContext;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.UUID;

public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUserId.class) ||
                parameter.hasParameterAnnotation(CurrentUserRole.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                            ModelAndViewContainer mavContainer,
                                            NativeWebRequest webRequest,
                                            WebDataBinderFactory binderFactory) {
        if (parameter.hasParameterAnnotation(CurrentUserId.class)) {
            UUID userId = UserContext.getUserId();
            if (userId == null) {
                throw new IllegalStateException("user id not found in context. Ensure JWT filter is configured");
            }

            return userId;
        }

        if (parameter.hasParameterAnnotation(CurrentUserRole.class)) {
            String userRole = UserContext.getUserRole();
            if (userRole == null) {
                throw new IllegalStateException("user role not found in context. Ensure JWT filter is configured");
            }

            return userRole;
        }

        return null;
    }
}
