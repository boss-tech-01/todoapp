package com.myproject.todoapp.controller.user;

import com.myproject.todoapp.entity.user.User;
import com.myproject.todoapp.exception.BadRequestException;
import com.myproject.todoapp.payload.messages.ErrorMessages;
import com.myproject.todoapp.payload.request.user.UserLoginRequestDTO;
import com.myproject.todoapp.payload.request.user.UserRegisterRequestDTO;
import com.myproject.todoapp.security.jwt.JwtUtils;
import com.myproject.todoapp.service.user.AuthService;
import com.myproject.todoapp.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.Duration;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Value("${app.security.cookie.secure}")
    private boolean isSecure;

    //1 - Register
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid UserRegisterRequestDTO dto){
        return new ResponseEntity<>(authService.register(dto), HttpStatus.CREATED);
    }

    //2 - Login...
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody @Valid UserLoginRequestDTO dto) {
        User user = userService.findByUsername(dto.getUsername());

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BadRequestException(ErrorMessages.PASSWORD_INVALID);
        }

        String access = jwtUtils.generateAccessToken(user.getUsername());
        String refresh = jwtUtils.generateRefreshToken(user.getUsername());

        ResponseCookie cookie = ResponseCookie.from("refreshToken", refresh)
                .httpOnly(true)
                .secure(isSecure) //Coming soon with HTTPS
                .sameSite("None")
                .path("/")
                .maxAge(Duration.ofDays(7))
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(Map.of("jwt", access));
    }

    //3 - Refresh
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refresh(@CookieValue("refreshToken") String refreshToken) {
        if (!jwtUtils.validateRefreshToken(refreshToken)) {
            throw new BadRequestException(ErrorMessages.ILLEGAL_REFRESH_TOKEN);
        }

        String username = jwtUtils.extractUsernameFromToken(refreshToken);

        return ResponseEntity.ok(Map.of("newAccessToken", jwtUtils.generateAccessToken(username)));
    }
}
