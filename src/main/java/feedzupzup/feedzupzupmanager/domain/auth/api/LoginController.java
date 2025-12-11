package feedzupzup.feedzupzupmanager.domain.auth.api;

import feedzupzup.feedzupzupmanager.domain.auth.api.domain.AdminProperties;
import feedzupzup.feedzupzupmanager.domain.auth.api.dto.LoginRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LoginController {

    private final AdminProperties adminProperties;

    @PostMapping("/api/auth/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request, HttpSession session) {
        if (adminProperties.getId().equals(request.id()) &&
            adminProperties.getPassword().equals(request.password())) {
            session.setAttribute("admin", true);
            return ResponseEntity.ok("Login successful");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }

    @PostMapping("/api/auth/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("Logout successful");
    }
}
