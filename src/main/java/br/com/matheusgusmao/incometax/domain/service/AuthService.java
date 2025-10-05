package br.com.matheusgusmao.incometax.domain.service;

import br.com.matheusgusmao.incometax.web.dto.auth.AuthRequest;
import br.com.matheusgusmao.incometax.web.dto.auth.AuthResponse;
import br.com.matheusgusmao.incometax.web.dto.register.RegisterUserRequest;
import br.com.matheusgusmao.incometax.web.dto.register.RegisterUserResponse;
import br.com.matheusgusmao.incometax.infra.exception.custom.EntityAlreadyExistsException;
import br.com.matheusgusmao.incometax.infra.security.jwt.JwtService;
import br.com.matheusgusmao.incometax.infra.persistence.entity.user.Role;
import br.com.matheusgusmao.incometax.infra.persistence.entity.user.UserEntity;
import br.com.matheusgusmao.incometax.infra.persistence.repository.UserRepository;
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
            throw new EntityAlreadyExistsException("Email already registered: " + request.email());
        });

        final String encodedPassword = passwordEncoder.encode(request.password());
        final UUID uuid = UUID.randomUUID();
        final UserEntity user = UserEntity.builder().id(uuid).firstName(request.firstName()).lastName(request.lastName()).email(request.email()).password(encodedPassword).role(Role.USER).build();

        final UserEntity savedUser = userRepository.save(user);

        return new RegisterUserResponse(savedUser.getId());
    }

    public AuthResponse authenticate(AuthRequest request) {
        final var authentication = new UsernamePasswordAuthenticationToken(request.username(), request.password());
        authenticationManager.authenticate(authentication);

        final UserEntity user = userRepository.findByEmail(request.username()).orElseThrow(() -> new UsernameNotFoundException("Email not found: " + request.username()));
        final String token = jwtService.generateToken(user);

        return new AuthResponse(token);
    }
}