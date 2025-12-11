package feedzupzup.feedzupzupmanager.domain.auth.api;

import feedzupzup.feedzupzupmanager.domain.auth.application.AuthService;
import feedzupzup.feedzupzupmanager.domain.auth.api.dto.LoginRequest;
import feedzupzup.feedzupzupmanager.global.util.CookieGenerator;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LoginController {

    private final AuthService authService;
    private final CookieGenerator cookieGenerator;

    @PostMapping("/api/auth/login")
    public ResponseEntity<Void> login(
            @RequestBody final LoginRequest request,
            final HttpSession session
    ) {
        authService.validateCredentials(request);
        session.setAttribute("admin", true);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/auth/logout")
    public ResponseEntity<Void> logout(final HttpSession session, final HttpServletResponse response) {
        session.invalidate();
        response.addCookie(cookieGenerator.createExpiredSessionCookie());
        return ResponseEntity.noContent().build();
    }
}
