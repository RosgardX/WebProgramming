package back.user;

import back.exсeption.DuplicateUserException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository users, PasswordEncoder passwordEncoder) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
    }

    public UserEntity register(String username, String rawPassword) {
        if (users.existsByUsername(username)) {
            throw new DuplicateUserException("Аккаунт с таким именем пользователя уже существует");
        }
        UserEntity u = new UserEntity();
        u.setUsername(username);
        u.setPasswordHash(passwordEncoder.encode(rawPassword));
        u.setRole("USER");
        return users.save(u);
    }

    public Optional<UserEntity> findByUsername(String username) {
        return users.findByUsername(username);
    }

    public boolean checkPassword(UserEntity user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getPasswordHash());
    }
}