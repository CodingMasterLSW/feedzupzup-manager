package feedzupzup.feedzupzupmanager.global.interceptor;

import feedzupzup.feedzupzupmanager.global.error.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminAuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Object handler
    ) {
        final HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("admin") == null) {
            throw new UnauthorizedException();
        }

        return true;
    }
}