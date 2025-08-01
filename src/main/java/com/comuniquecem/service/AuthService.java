package com.comuniquecem.service;

import com.comuniquecem.dto.request.LoginRequest;
import com.comuniquecem.dto.request.RegisterRequest;
import com.comuniquecem.dto.response.AuthResponse;
import com.comuniquecem.dto.response.UserSummaryResponse;
import com.comuniquecem.entity.Institution;
import com.comuniquecem.entity.User;
import com.comuniquecem.entity.enums.UserRole;
import com.comuniquecem.exception.BusinessException;
import com.comuniquecem.repository.InstitutionRepository;
import com.comuniquecem.repository.UserRepository;
import com.comuniquecem.security.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Service para autenticação e autorização
 */
@Service
@Transactional
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InstitutionRepository institutionRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Autentica um usuário e retorna token JWT
     */
    public AuthResponse login(LoginRequest request) {
        logger.info("Tentativa de login para email: {}", request.getEmail());

        try {
            // Autenticar com Spring Security
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            // Buscar usuário
            User user = userRepository.findByEmailAndActiveTrue(request.getEmail())
                .orElseThrow(() -> new BusinessException("Usuário não encontrado ou inativo"));

            // Verificar se instituição está ativa
            if (!user.getInstitution().getActive()) {
                throw new BusinessException("Instituição inativa");
            }

            // Atualizar último login
            user.setLastLoginAt(LocalDateTime.now());
            user.setOnline(true);
            userRepository.save(user);

            // Gerar token JWT
            String token = jwtService.generateToken(user);

            // Criar resposta de usuário
            UserSummaryResponse userResponse = new UserSummaryResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getProfilePictureUrl(),
                user.getOnline(),
                user.getActive(),
                user.getInstitution().getName(),
                user.getCreatedAt()
            );

            logger.info("Login realizado com sucesso para usuário: {}", user.getEmail());

            return new AuthResponse(token, userResponse);

        } catch (Exception e) {
            logger.error("Erro no login para email {}: {}", request.getEmail(), e.getMessage());
            throw new BusinessException("Credenciais inválidas");
        }
    }

    /**
     * Registra um novo usuário
     */
    public AuthResponse register(RegisterRequest request) {
        logger.info("Tentativa de registro para email: {}", request.getEmail());

        // Verificar se email já existe
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BusinessException("Email já cadastrado");
        }

        // Buscar instituição pelo código
        Institution institution = institutionRepository.findByCodeAndActiveTrue(request.getInstitutionCode())
            .orElseThrow(() -> new BusinessException("Código de instituição inválido ou instituição inativa"));

        // Criar novo usuário
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.STUDENT); // Por padrão, novos usuários são estudantes
        user.setInstitution(institution);
        user.setProfilePictureUrl(request.getProfilePictureUrl());
        user.setActive(true);
        user.setOnline(true);
        user.setLastLoginAt(LocalDateTime.now());

        user = userRepository.save(user);

        // Gerar token JWT
        String token = jwtService.generateToken(user);

        // Criar resposta de usuário
        UserSummaryResponse userResponse = new UserSummaryResponse(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getRole(),
            user.getProfilePictureUrl(),
            user.getOnline(),
            user.getActive(),
            user.getInstitution().getName(),
            user.getCreatedAt()
        );

        logger.info("Usuário registrado com sucesso: {}", user.getEmail());

        return new AuthResponse(token, userResponse);
    }

    /**
     * Realiza logout do usuário
     */
    public void logout(String userEmail) {
        logger.info("Logout para usuário: {}", userEmail);

        User user = userRepository.findByEmailAndActiveTrue(userEmail)
            .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        user.setOnline(false);
        userRepository.save(user);

        logger.info("Logout realizado com sucesso para usuário: {}", userEmail);
    }

    /**
     * Valida se o token JWT é válido e retorna informações do usuário
     */
    public UserSummaryResponse validateToken(String token) {
        try {
            String email = jwtService.extractUsername(token);
            User user = userRepository.findByEmailAndActiveTrue(email)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

            if (jwtService.isTokenValid(token, user)) {
                return new UserSummaryResponse(
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getRole(),
                    user.getProfilePictureUrl(),
                    user.getOnline(),
                    user.getActive(),
                    user.getInstitution().getName(),
                    user.getCreatedAt()
                );
            }

            throw new BusinessException("Token inválido");

        } catch (Exception e) {
            logger.error("Erro na validação do token: {}", e.getMessage());
            throw new BusinessException("Token inválido");
        }
    }
}
