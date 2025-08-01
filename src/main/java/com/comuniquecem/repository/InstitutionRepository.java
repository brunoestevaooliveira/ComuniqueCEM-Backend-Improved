package com.comuniquecem.repository;

import com.comuniquecem.entity.Institution;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositório para operações com Institution
 */
@Repository
public interface InstitutionRepository extends JpaRepository<Institution, UUID> {

    /**
     * Busca instituição por nome
     */
    Optional<Institution> findByName(String name);

    /**
     * Busca instituição por código e ativa
     */
    Optional<Institution> findByCodeAndActiveTrue(String code);

    /**
     * Verifica se existe instituição com o nome (ignorando case)
     */
    boolean existsByNameIgnoreCase(String name);

    /**
     * Login da instituição
     */
    @Query("SELECT i FROM Institution i WHERE i.name = :name AND i.password = :password AND i.active = true")
    Optional<Institution> findByNameAndPasswordAndActiveTrue(@Param("name") String name, @Param("password") String password);

    /**
     * Login de professores
     */
    @Query("SELECT i FROM Institution i WHERE i.name = :name AND i.teacherPassword = :password AND i.active = true")
    Optional<Institution> findByNameAndTeacherPasswordAndActiveTrue(@Param("name") String name, @Param("password") String password);

    /**
     * Busca instituições ativas
     */
    Page<Institution> findByActiveTrue(Pageable pageable);

    /**
     * Busca instituições por nome (contendo)
     */
    Page<Institution> findByNameContainingIgnoreCaseAndActiveTrue(String name, Pageable pageable);

    /**
     * Conta usuários por instituição
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.institution.id = :institutionId AND u.active = true")
    long countActiveUsersByInstitutionId(@Param("institutionId") UUID institutionId);

    /**
     * Conta notícias por instituição
     */
    @Query("SELECT COUNT(n) FROM News n WHERE n.institution.id = :institutionId")
    long countNewsByInstitutionId(@Param("institutionId") UUID institutionId);

    /**
     * Conta questões por instituição
     */
    @Query("SELECT COUNT(q) FROM Question q WHERE q.institution.id = :institutionId AND q.active = true")
    long countActiveQuestionsByInstitutionId(@Param("institutionId") UUID institutionId);
}
