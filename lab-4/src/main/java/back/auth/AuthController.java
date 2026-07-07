package back.auth;

import back.user.UserEntity;
import back.user.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;

    public AuthController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req) {
        UserEntity u = userService.register(req.username(), req.password());
        String token = jwtService.generateToken(u);
        return ResponseEntity.ok(new AuthResponse(u.getUsername(), token));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        UserEntity u = userService.findByUsername(req.username())
                .orElseThrow(() -> new IllegalArgumentException("Неверный логин или пароль"));
        if (!userService.checkPassword(u, req.password())) {
            throw new IllegalArgumentException("Неверный логин или пароль");
        }
        String token = jwtService.generateToken(u);
        return ResponseEntity.ok(new AuthResponse(u.getUsername(), token));
    }

    public record RegisterRequest(
            @NotBlank(message = "Логин обязателен")
            @Size(min = 3, max = 64, message = "Логин должен быть от 3 до 64 символов")
            String username,
            @NotBlank(message = "Пароль обязателен")
            @Size(min = 6, max = 128, message = "Пароль должен быть от 6 до 128 символов")
            String password
    ) {}

    public record LoginRequest(
            @NotBlank(message = "Логин обязателен") String username,
            @NotBlank(message = "Пароль обязателен") String password
    ) {}

    public record AuthResponse(String username, String token) {}
}