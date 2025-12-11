package feedzupzup.feedzupzupmanager.domain.auth.application;

import feedzupzup.feedzupzupmanager.domain.auth.api.domain.AdminProperties;
import feedzupzup.feedzupzupmanager.domain.auth.api.dto.LoginRequest;
import feedzupzup.feedzupzupmanager.global.error.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AdminProperties adminProperties;

    public void validateCredentials(final LoginRequest request) {
        if (!isValidCredentials(request)) {
            throw new BadRequestException("아이디 또는 비밀번호가 일치하지 않습니다.");
        }
    }

    private boolean isValidCredentials(final LoginRequest request) {
        return adminProperties.getId().equals(request.id()) &&
               adminProperties.getPassword().equals(request.password());
    }
}


