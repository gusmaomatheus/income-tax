package br.com.matheusgusmao.incometax.auth.dto.register;

import io.swagger.v3.oas.annotations.media.Schema;

public record RegisterUserRequest(@Schema(description = "FirstName", example = "John") String firstName,
                                  @Schema(description = "Lastname", example = "Snow") String lastname,
                                  @Schema(description = "Email to be used as login", example = "know.nothing@snow.com") String email,
                                  @Schema(description = "Password", example = "n3243#kFdj$") String password) {
}