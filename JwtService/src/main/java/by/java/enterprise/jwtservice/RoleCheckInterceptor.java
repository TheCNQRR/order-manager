package by.java.enterprise.jwtservice;

import by.java.enterprise.jwtservice.annotation.RequiredRole;
import by.java.enterprise.jwtservice.storage.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

public class RoleCheckInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod handlerMethod) {
            RequiredRole requiredRole = handlerMethod.getMethodAnnotation(RequiredRole.class);
            if (requiredRole != null) {
                String currentUserRole = UserContext.getUserRole();

                if (currentUserRole == null) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "authentication required");

                    return false;
                }

                boolean hasAccess = false;
                for (String allowedRole : requiredRole.value()) {
                    if (allowedRole.equals(currentUserRole)) {
                        hasAccess = true;
                        break;
                    }
                }

                if (!hasAccess) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "access denied");

                    return false;
                }
            }
        }

        return true;
    }
}
