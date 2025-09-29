package br.com.matheusgusmao.incometax.authentication;

import br.com.matheusgusmao.incometax.authentication.io.auth.AuthRequest;
import br.com.matheusgusmao.incometax.authentication.io.auth.AuthResponse;
import br.com.matheusgusmao.incometax.authentication.io.register.RegisterUserRequest;
import br.com.matheusgusmao.incometax.authentication.io.register.RegisterUserResponse;
import br.com.matheusgusmao.incometax.infra.exception.custom.EntityAlreadyExists;
import br.com.matheusgusmao.incometax.infra.security.config.jwt.JwtService;
import br.com.matheusgusmao.incometax.infra.security.user.model.Role;
import br.com.matheusgusmao.incometax.infra.security.user.model.User;
import br.com.matheusgusmao.incometax.infra.security.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public RegisterUserResponse register(RegisterUserRequest request) {
        userRepository.findByEmail(request.email()).ifPresent(_ -> {
            throw new EntityAlreadyExists("Email already registered: " + request.email());
        });

        final String encodedPassword = passwordEncoder.encode(request.password());
        final UUID uuid = UUID.randomUUID();
        final User user = User.builder().id(uuid).firstName(request.firstName()).lastName(request.lastname()).email(request.email()).password(encodedPassword).role(Role.USER).build();

        userRepository.save(user);

        return new RegisterUserResponse(uuid);
    }

    public AuthResponse authenticate(AuthRequest request) {
        final var authentication = new UsernamePasswordAuthenticationToken(request.username(), request.password());
        authenticationManager.authenticate(authentication);

        final User user = userRepository.findByEmail(request.username()).orElseThrow(() -> new UsernameNotFoundException("Email not found: " + request.username()));
        final String token = jwtService.generateToken(user);

        return new AuthResponse(token);
    }
}