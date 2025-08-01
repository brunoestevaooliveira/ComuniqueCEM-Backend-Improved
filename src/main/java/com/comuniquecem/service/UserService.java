package com.comuniquecem.service;

import com.comuniquecem.dto.response.UserResponse;
import com.comuniquecem.dto.response.UserSummaryResponse;
import com.comuniquecem.dto.response.InstitutionSummaryResponse;
import com.comuniquecem.entity.User;
import com.comuniquecem.entity.enums.UserRole;
import com.comuniquecem.exception.BusinessException;
import com.comuniquecem.exception.ResourceNotFoundException;
import com.comuniquecem.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service para operações com usuários
 */
@Service
@Transactional
public class UserService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Implementação do UserDetailsService para Spring Security
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmailAndActiveTrue(email)
            .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));
    }

    /**
     * Busca usuário por ID
     */
    @Transactional(readOnly = true)
    public UserResponse findById(UUID id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        return mapToUserResponse(user);
    }

    /**
     * Busca usuário por email
     */
    @Transactional(readOnly = true)
    public UserResponse findByEmail(String email) {
        User user = userRepository.findByEmailAndActiveTrue(email)
            .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        return mapToUserResponse(user);
    }

    /**
     * Busca usuários por instituição
     */
    @Transactional(readOnly = true)
    public Page<UserSummaryResponse> findByInstitution(UUID institutionId, Pageable pageable) {
        return userRepository.findByInstitutionIdAndActiveTrue(institutionId, pageable)
            .map(this::mapToUserSummaryResponse);
    }

    /**
     * Busca usuários por instituição e role
     */
    @Transactional(readOnly = true)
    public Page<UserSummaryResponse> findByInstitutionAndRole(UUID institutionId, UserRole role, Pageable pageable) {
        return userRepository.findByInstitutionIdAndRoleAndActiveTrue(institutionId, role, pageable)
            .map(this::mapToUserSummaryResponse);
    }

    /**
     * Busca usuários online
     */
    @Transactional(readOnly = true)
    public List<UserSummaryResponse> findOnlineUsers() {
        return userRepository.findByOnlineTrue()
            .stream()
            .map(this::mapToUserSummaryResponse)
            .collect(Collectors.toList());
    }

    /**
     * Atualiza perfil do usuário
     */
    public UserResponse updateProfile(UUID userId, String name, String phone, String bio, String profilePictureUrl) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        user.setName(name);
        user.setPhone(phone);
        user.setBio(bio);
        user.setProfilePictureUrl(profilePictureUrl);

        user = userRepository.save(user);

        logger.info("Perfil atualizado para usuário: {}", user.getEmail());

        return mapToUserResponse(user);
    }

    /**
     * Atualiza senha do usuário
     */
    public void updatePassword(UUID userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new BusinessException("Senha atual incorreta");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        logger.info("Senha atualizada para usuário: {}", user.getEmail());
    }

    /**
     * Ativa/desativa usuário
     */
    public void toggleActiveStatus(UUID userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        user.setActive(!user.getActive());
        userRepository.save(user);

        logger.info("Status ativo alterado para usuário {}: {}", user.getEmail(), user.getActive());
    }

    /**
     * Atualiza status online do usuário
     */
    public void updateOnlineStatus(UUID userId, boolean online) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        user.setOnline(online);
        userRepository.save(user);
    }

    /**
     * Verifica se o email já existe
     */
    @Transactional(readOnly = true)
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Conta usuários por instituição
     */
    @Transactional(readOnly = true)
    public long countByInstitution(UUID institutionId) {
        return userRepository.countByInstitutionIdAndActiveTrue(institutionId);
    }

    /**
     * Conta usuários por instituição e role
     */
    @Transactional(readOnly = true)
    public long countByInstitutionAndRole(UUID institutionId, UserRole role) {
        return userRepository.countByInstitutionIdAndRoleAndActiveTrue(institutionId, role);
    }

    // Métodos de mapeamento privados
    private UserResponse mapToUserResponse(User user) {
        InstitutionSummaryResponse institutionResponse = new InstitutionSummaryResponse(
            user.getInstitution().getId(),
            user.getInstitution().getName(),
            user.getInstitution().getCode(),
            user.getInstitution().getAddress(),
            user.getInstitution().getPhone(),
            user.getInstitution().getEmail(),
            user.getInstitution().getLogoUrl(),
            user.getInstitution().getActive(),
            user.getInstitution().getCreatedAt()
        );

        return new UserResponse(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getRole(),
            user.getProfilePictureUrl(),
            user.getOnline(),
            user.getActive(),
            user.getPhone(),
            user.getBio(),
            institutionResponse,
            user.getCreatedAt(),
            user.getUpdatedAt(),
            user.getLastLoginAt()
        );
    }

    private UserSummaryResponse mapToUserSummaryResponse(User user) {
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
}
