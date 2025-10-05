package br.com.matheusgusmao.incometax.domain.model.declaration;

import lombok.Getter;

import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

@Getter
public final class Declaration {

    private final Long id;
    private final UUID taxpayerId;
    private final int year;
    private final DeclarationStatus status;

    private static final Pattern YEAR_REGEX_PATTERN = Pattern.compile("^\\d{4}$");

    public Declaration(UUID taxpayerId, int year) {
        Objects.requireNonNull(taxpayerId, "Taxpayer ID cannot be null.");

        if (!YEAR_REGEX_PATTERN.matcher(Integer.toString(year)).matches()) {
            throw new IllegalArgumentException("Invalid year format: " + year);
        }

        this.id = null;
        this.taxpayerId = taxpayerId;
        this.year = year;
        this.status = DeclarationStatus.EDITING;
    }

    public Declaration(Long id, UUID taxpayerId, int year, DeclarationStatus status) {
        this.id = id;
        this.taxpayerId = taxpayerId;
        this.year = year;
        this.status = status;
    }
}