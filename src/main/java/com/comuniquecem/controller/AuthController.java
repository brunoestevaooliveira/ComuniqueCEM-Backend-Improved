package com.comuniquecem.controller;

import com.comuniquecem.dto.request.LoginRequest;
import com.comuniquecem.dto.request.RegisterRequest;
import com.comuniquecem.dto.response.AuthResponse;
import com.comuniquecem.dto.response.UserSummaryResponse;
import com.comuniquecem.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller para autenticação e autorização
 */
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticação", description = "Endpoints para autenticação e autorização")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    /**
     * Endpoint público de health check
     */
    @GetMapping("/public/health")
    @Operation(summary = "Health check público", description = "Verifica se a API está funcionando")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "auth-service",
            "timestamp", java.time.Instant.now().toString()
        ));
    }

    /**
     * Endpoint para login
     */
    @PostMapping("/login")
    @Operation(summary = "Realizar login", description = "Autentica um usuário e retorna token JWT")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
        @ApiResponse(responseCode = "401", description = "Credenciais inválidas"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        logger.info("Tentativa de login para email: {}", request.getEmail());
        
        AuthResponse response = authService.login(request);
        
        logger.info("Login realizado com sucesso para: {}", request.getEmail());
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para registro
     */
    @PostMapping("/register")
    @Operation(summary = "Registrar usuário", description = "Registra um novo usuário no sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuário registrado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
        @ApiResponse(responseCode = "409", description = "Email já cadastrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        logger.info("Tentativa de registro para email: {}", request.getEmail());
        
        AuthResponse response = authService.register(request);
        
        logger.info("Usuário registrado com sucesso: {}", request.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Endpoint para logout
     */
    @PostMapping("/logout")
    @Operation(summary = "Realizar logout", description = "Realiza logout do usuário autenticado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Logout realizado com sucesso"),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Void> logout(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            authService.logout(email);
            logger.info("Logout realizado para: {}", email);
        }
        
        return ResponseEntity.ok().build();
    }

    /**
     * Endpoint para validar token
     */
    @GetMapping("/validate")
    @Operation(summary = "Validar token", description = "Valida o token JWT e retorna informações do usuário")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Token válido"),
        @ApiResponse(responseCode = "401", description = "Token inválido"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<UserSummaryResponse> validateToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            UserSummaryResponse userResponse = authService.validateToken(token);
            return ResponseEntity.ok(userResponse);
        }
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    /**
     * Endpoint para obter informações do usuário autenticado
     */
    @GetMapping("/me")
    @Operation(summary = "Informações do usuário", description = "Retorna informações do usuário autenticado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Informações recuperadas com sucesso"),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<UserSummaryResponse> getCurrentUser(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            // Buscar informações do usuário pela UserService
            // Por agora, retornamos um placeholder
            logger.info("Recuperando informações do usuário: {}", email);
            return ResponseEntity.ok().build();
        }
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
