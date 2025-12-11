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
    ) throws Exception {
        final HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("admin") == null) {
            final String requestURI = request.getRequestURI();

            // API 요청인 경우 예외 발생
            if (requestURI.startsWith("/api/")) {
                throw new UnauthorizedException();
            }

            // 페이지 요청인 경우 로그인 페이지로 리다이렉트
            response.sendRedirect("/login");
            return false;
        }

        return true;
    }
}