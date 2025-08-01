package com.comuniquecem.repository;

import com.comuniquecem.entity.Institution;
import com.comuniquecem.entity.User;
import com.comuniquecem.entity.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositório para operações com User
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Busca usuário por email (usado para autenticação)
     */
    Optional<User> findByEmail(String email);

    /**
     * Busca usuário ativo por email (usado para autenticação)
     */
    Optional<User> findByEmailAndActiveTrue(String email);

    /**
     * Verifica se existe usuário com o email
     */
    boolean existsByEmail(String email);

    /**
     * Busca usuários por instituição
     */
    Page<User> findByInstitutionAndActiveTrue(Institution institution, Pageable pageable);

    /**
     * Busca usuários por instituição ID
     */
    Page<User> findByInstitutionIdAndActiveTrue(UUID institutionId, Pageable pageable);

    /**
     * Busca usuários por instituição ID e role
     */
    Page<User> findByInstitutionIdAndRoleAndActiveTrue(UUID institutionId, UserRole role, Pageable pageable);

    /**
     * Busca usuários online
     */
    List<User> findByOnlineTrue();

    /**
     * Conta usuários por instituição ID e ativos
     */
    long countByInstitutionIdAndActiveTrue(UUID institutionId);

    /**
     * Conta usuários por instituição ID, role e ativos
     */
    long countByInstitutionIdAndRoleAndActiveTrue(UUID institutionId, UserRole role);

    /**
     * Busca usuários por instituição e role
     */
    Page<User> findByInstitutionAndRoleAndActiveTrue(Institution institution, UserRole role, Pageable pageable);

    /**
     * Busca usuários por nome (contendo) na instituição
     */
    @Query("SELECT u FROM User u WHERE u.institution = :institution AND u.active = true AND " +
           "LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<User> findByInstitutionAndNameContainingIgnoreCaseAndActiveTrue(
            @Param("institution") Institution institution, 
            @Param("name") String name, 
            Pageable pageable);

    /**
     * Busca usuários online da instituição
     */
    @Query("SELECT u FROM User u WHERE u.institution = :institution AND u.online = true AND u.active = true")
    List<User> findOnlineUsersByInstitution(@Param("institution") Institution institution);

    /**
     * Busca usuários por múltiplos roles na instituição
     */
    @Query("SELECT u FROM User u WHERE u.institution = :institution AND u.role IN :roles AND u.active = true")
    Page<User> findByInstitutionAndRoleInAndActiveTrue(
            @Param("institution") Institution institution, 
            @Param("roles") List<UserRole> roles, 
            Pageable pageable);

    /**
     * Atualiza status online do usuário
     */
    @Modifying
    @Query("UPDATE User u SET u.online = :online, u.updatedAt = :updatedAt WHERE u.id = :userId")
    void updateOnlineStatus(@Param("userId") UUID userId, @Param("online") boolean online, @Param("updatedAt") LocalDateTime updatedAt);

    /**
     * Busca administradores da instituição
     */
    @Query("SELECT u FROM User u WHERE u.institution = :institution AND u.role IN ('ADMIN', 'SUPER_ADMIN') AND u.active = true")
    List<User> findAdminsByInstitution(@Param("institution") Institution institution);

    /**
     * Busca professores da instituição
     */
    @Query("SELECT u FROM User u WHERE u.institution = :institution AND u.role = 'TEACHER' AND u.active = true")
    List<User> findTeachersByInstitution(@Param("institution") Institution institution);

    /**
     * Busca estudantes da instituição
     */
    @Query("SELECT u FROM User u WHERE u.institution = :institution AND u.role = 'STUDENT' AND u.active = true")
    Page<User> findStudentsByInstitution(@Param("institution") Institution institution, Pageable pageable);

    /**
     * Conta usuários por role na instituição
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.institution = :institution AND u.role = :role AND u.active = true")
    long countByInstitutionAndRoleAndActiveTrue(@Param("institution") Institution institution, @Param("role") UserRole role);

    /**
     * Busca usuários inativos por período
     */
    @Query("SELECT u FROM User u WHERE u.active = true AND u.updatedAt < :cutoffDate AND u.online = false")
    List<User> findInactiveUsers(@Param("cutoffDate") LocalDateTime cutoffDate);

    /**
     * Busca usuários com email não verificado
     */
    @Query("SELECT u FROM User u WHERE u.emailVerified = false AND u.active = true AND u.createdAt < :cutoffDate")
    List<User> findUnverifiedUsers(@Param("cutoffDate") LocalDateTime cutoffDate);

    /**
     * Busca usuários para notificações (ativos e com email verificado)
     */
    @Query("SELECT u FROM User u WHERE u.institution = :institution AND u.active = true AND u.emailVerified = true")
    List<User> findUsersForNotifications(@Param("institution") Institution institution);
}
