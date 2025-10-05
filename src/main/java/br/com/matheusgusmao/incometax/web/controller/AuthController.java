package br.com.matheusgusmao.incometax.web.controller;

import br.com.matheusgusmao.incometax.web.dto.auth.AuthRequest;
import br.com.matheusgusmao.incometax.web.dto.auth.AuthResponse;
import br.com.matheusgusmao.incometax.web.dto.register.RegisterUserRequest;
import br.com.matheusgusmao.incometax.web.dto.register.RegisterUserResponse;
import br.com.matheusgusmao.incometax.domain.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Tag(name = "Registration/Authentication API")
public class AuthController {
    private final AuthService authService;

    @Operation(
            summary = "Register a new user.",
            description = "Returns the new user id."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Successful operation.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RegisterUserRequest.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "User email is already registered.",
                    content = @Content(schema = @Schema(hidden = true))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication fails.",
                    content = @Content(schema = @Schema(hidden = true))
            ),
            @ApiResponse(responseCode = "500",
                    description = "Internal Server Error.",
                    content = @Content(schema = @Schema(hidden = true))
            )
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterUserRequest request) {
        final RegisterUserResponse response = authService.register(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Authenticates the user using email and password.",
            description = "Returns a JWT credential to be used in future requests."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "Successful operation.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Email not found.",
                    content = @Content(schema = @Schema(hidden = true))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication fails.",
                    content = @Content(schema = @Schema(hidden = true))
            )
    })
    @PostMapping("/auth")
    public ResponseEntity<?> authenticate(@RequestBody AuthRequest request) {
        final AuthResponse response = authService.authenticate(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}