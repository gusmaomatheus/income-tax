package br.com.matheusgusmao.incometax.functional;

import br.com.matheusgusmao.incometax.domain.model.expense.DeductibleExpense;
import br.com.matheusgusmao.incometax.domain.model.expense.ExpenseType;
import br.com.matheusgusmao.incometax.domain.model.income.Income;
import br.com.matheusgusmao.incometax.domain.model.income.IncomeType;
import br.com.matheusgusmao.incometax.domain.service.DeclarationService;
import br.com.matheusgusmao.incometax.infra.persistence.entity.declaration.DeclarationEntity;
import br.com.matheusgusmao.incometax.infra.persistence.entity.user.UserEntity;
import br.com.matheusgusmao.incometax.infra.persistence.repository.DeclarationRepository;
import br.com.matheusgusmao.incometax.infra.persistence.repository.UserRepository;
import br.com.matheusgusmao.incometax.web.dto.auth.AuthRequest;
import br.com.matheusgusmao.incometax.web.dto.auth.AuthResponse;
import br.com.matheusgusmao.incometax.web.dto.declaration.CreateDeclarationRequest;
import br.com.matheusgusmao.incometax.web.dto.declaration.DeclarationResponse;
import br.com.matheusgusmao.incometax.web.dto.register.RegisterUserRequest;
import br.com.matheusgusmao.incometax.web.dto.register.RegisterUserResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Declaration Functional Tests")
class DeclarationFunctionalTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DeclarationRepository declarationRepository;

    @Autowired
    private DeclarationService declarationService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String authToken;
    private UUID userId;

    @BeforeEach
    void setUp() throws Exception {
        // Clean up database
        declarationRepository.deleteAll();
        userRepository.deleteAll();

        // Register and authenticate a test user
        var registerRequest = new RegisterUserRequest("João", "Silva", "joao@test.com", "password123");
        var registerResponse = performRegister(registerRequest);
        userId = registerResponse.getId();

        var authRequest = new AuthRequest("joao@test.com", "password123");
        var authResponse = performAuth(authRequest);
        authToken = authResponse.token();
    }

    @Nested
    @DisplayName("Given a taxpayer wants to manage their tax declaration")
    class DeclarationManagementTests {

        @Test
        @DisplayName("When taxpayer creates declaration Then declaration should be created successfully")
        void shouldCreateDeclarationSuccessfully() throws Exception {
            // Given
            var createRequest = new CreateDeclarationRequest(2025);

            // When
            var result = mockMvc.perform(post("/declarations")
                            .header("Authorization", "Bearer " + authToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.year").value(2025))
                    .andExpect(jsonPath("$.status").value("EDITING"))
                    .andReturn();

            // Then
            var response = objectMapper.readValue(result.getResponse().getContentAsString(), DeclarationResponse.class);
            assertThat(response.id()).isNotNull();
            assertThat(response.year()).isEqualTo(2025);
            assertThat(response.status()).isEqualTo("EDITING");
        }

        @Test
        @DisplayName("When taxpayer tries to create duplicate declaration Then error should be returned")
        void shouldRejectDuplicateDeclaration() throws Exception {
            // Given
            var createRequest = new CreateDeclarationRequest(2025);
            
            // Create first declaration
            mockMvc.perform(post("/declarations")
                            .header("Authorization", "Bearer " + authToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest)))
                    .andExpect(status().isCreated());

            // When & Then - Try to create duplicate
            mockMvc.perform(post("/declarations")
                            .header("Authorization", "Bearer " + authToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest)))
                    .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("When taxpayer adds income to declaration Then income should be added successfully")
        void shouldAddIncomeSuccessfully() throws Exception {
            // Given
            var declarationId = createDeclaration();
            var incomeRequest = """
                    {
                        "payingSource": "Company ABC",
                        "type": "SALARY",
                        "value": 50000.00
                    }
                    """;

            // When
            mockMvc.perform(post("/declarations/{id}/incomes", declarationId)
                            .header("Authorization", "Bearer " + authToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(incomeRequest))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.incomes").isArray())
                    .andExpect(jsonPath("$.incomes[0].payingSource").value("Company ABC"))
                    .andExpect(jsonPath("$.incomes[0].type").value("SALARY"))
                    .andExpect(jsonPath("$.incomes[0].value").value(50000.00));

            // Then
            var declaration = declarationService.findById(declarationId);
            assertThat(declaration.getIncomes()).hasSize(1);
            assertThat(declaration.getIncomes().get(0).getPayingSource()).isEqualTo("Company ABC");
        }

        @Test
        @DisplayName("When taxpayer adds deductible expense Then expense should be added successfully")
        void shouldAddDeductibleExpenseSuccessfully() throws Exception {
            // Given
            var declarationId = createDeclaration();
            var expenseRequest = """
                    {
                        "description": "Plano de saúde",
                        "type": "HEALTH",
                        "value": 1200.00
                    }
                    """;

            // When
            mockMvc.perform(post("/declarations/{id}/expenses", declarationId)
                            .header("Authorization", "Bearer " + authToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(expenseRequest))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.deductibleExpenses").isArray())
                    .andExpect(jsonPath("$.deductibleExpenses[0].description").value("Plano de saúde"))
                    .andExpect(jsonPath("$.deductibleExpenses[0].type").value("HEALTH"))
                    .andExpect(jsonPath("$.deductibleExpenses[0].value").value(1200.00));

            // Then
            var declaration = declarationService.findById(declarationId);
            assertThat(declaration.getDeductibleExpenses()).hasSize(1);
            assertThat(declaration.getDeductibleExpenses().get(0).getDescription()).isEqualTo("Plano de saúde");
        }

        @Test
        @DisplayName("When taxpayer adds dependent Then dependent should be added successfully")
        void shouldAddDependentSuccessfully() throws Exception {
            // Given
            var declarationId = createDeclaration();
            var dependentRequest = """
                    {
                        "name": "Maria Silva",
                        "cpf": "12345678901",
                        "birthDate": "2010-05-15"
                    }
                    """;

            // When
            mockMvc.perform(post("/declarations/{id}/dependents", declarationId)
                            .header("Authorization", "Bearer " + authToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(dependentRequest))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.name").value("Maria Silva"))
                    .andExpect(jsonPath("$.cpf").value("12345678901"));

            // Then
            var declaration = declarationService.findById(declarationId);
            assertThat(declaration.getDependents()).hasSize(1);
            assertThat(declaration.getDependents().get(0).getName()).isEqualTo("Maria Silva");
        }

        @Test
        @DisplayName("When taxpayer submits complete declaration Then declaration should be submitted successfully")
        void shouldSubmitDeclarationSuccessfully() throws Exception {
            // Given
            var declarationId = createDeclaration();
            
            // Add income (required for submission)
            var incomeRequest = """
                    {
                        "payingSource": "Company ABC",
                        "type": "SALARY",
                        "value": 50000.00
                    }
                    """;
            
            mockMvc.perform(post("/declarations/{id}/incomes", declarationId)
                            .header("Authorization", "Bearer " + authToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(incomeRequest))
                    .andExpect(status().isCreated());

            // When
            mockMvc.perform(post("/declarations/{id}/submit", declarationId)
                            .header("Authorization", "Bearer " + authToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("DELIVERED"))
                    .andExpect(jsonPath("$.deliveryDate").exists());

            // Then
            var declaration = declarationService.findById(declarationId);
            assertThat(declaration.getStatus().toString()).isEqualTo("DELIVERED");
            assertThat(declaration.getDeliveryDate()).isNotNull();
        }

        @Test
        @DisplayName("When taxpayer tries to submit declaration without incomes Then error should be returned")
        void shouldRejectSubmissionWithoutIncomes() throws Exception {
            // Given
            var declarationId = createDeclaration();

            // When & Then
            mockMvc.perform(post("/declarations/{id}/submit", declarationId)
                            .header("Authorization", "Bearer " + authToken))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Given a taxpayer wants to view their declaration history")
    class DeclarationHistoryTests {

        @Test
        @DisplayName("When taxpayer requests declaration history Then all declarations should be returned")
        void shouldReturnDeclarationHistory() throws Exception {
            // Given
            createDeclaration();
            createDeclaration(2024);

            // When
            mockMvc.perform(get("/declarations/history")
                            .header("Authorization", "Bearer " + authToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$[0].year").exists())
                    .andExpect(jsonPath("$[1].year").exists());

            // Then
            var history = declarationService.getDeclarationHistory(userId);
            assertThat(history).hasSize(2);
        }
    }

    private Long createDeclaration() throws Exception {
        return createDeclaration(2025);
    }

    private Long createDeclaration(int year) throws Exception {
        var createRequest = new CreateDeclarationRequest(year);
        var result = mockMvc.perform(post("/declarations")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        var response = objectMapper.readValue(result.getResponse().getContentAsString(), DeclarationResponse.class);
        return response.id();
    }

    private RegisterUserResponse performRegister(RegisterUserRequest request) throws Exception {
        var result = mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readValue(result.getResponse().getContentAsString(), RegisterUserResponse.class);
    }

    private AuthResponse performAuth(AuthRequest request) throws Exception {
        var result = mockMvc.perform(post("/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        return objectMapper.readValue(result.getResponse().getContentAsString(), AuthResponse.class);
    }
}
