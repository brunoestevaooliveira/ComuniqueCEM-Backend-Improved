package com.comuniquecem.controller;

import com.comuniquecem.dto.response.UserResponse;
import com.comuniquecem.dto.response.UserSummaryResponse;
import com.comuniquecem.entity.enums.UserRole;
import com.comuniquecem.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller para operações com usuários
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "Usuários", description = "Endpoints para gerenciamento de usuários")
@SecurityRequirement(name = "Bearer Authentication")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    /**
     * Busca usuário por ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuário por ID", description = "Retorna informações detalhadas de um usuário")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
        @ApiResponse(responseCode = "401", description = "Não autorizado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or @userService.canAccessUser(authentication.name, #id)")
    public ResponseEntity<UserResponse> getUserById(
            @Parameter(description = "ID do usuário", required = true)
            @PathVariable UUID id,
            Authentication authentication) {
        
        logger.info("Buscando usuário por ID: {}", id);
        UserResponse user = userService.findById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * Busca usuários por instituição
     */
    @GetMapping("/institution/{institutionId}")
    @Operation(summary = "Buscar usuários por instituição", description = "Lista usuários de uma instituição com paginação")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de usuários retornada"),
        @ApiResponse(responseCode = "401", description = "Não autorizado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or @userService.canAccessInstitution(authentication.name, #institutionId)")
    public ResponseEntity<Page<UserSummaryResponse>> getUsersByInstitution(
            @Parameter(description = "ID da instituição", required = true)
            @PathVariable UUID institutionId,
            @Parameter(description = "Role do usuário (opcional)")
            @RequestParam(required = false) UserRole role,
            @PageableDefault(size = 20) Pageable pageable,
            Authentication authentication) {
        
        logger.info("Buscando usuários da instituição: {}, role: {}", institutionId, role);
        
        Page<UserSummaryResponse> users = role != null
            ? userService.findByInstitutionAndRole(institutionId, role, pageable)
            : userService.findByInstitution(institutionId, pageable);
            
        return ResponseEntity.ok(users);
    }

    /**
     * Busca usuários online
     */
    @GetMapping("/online")
    @Operation(summary = "Buscar usuários online", description = "Lista todos os usuários atualmente online")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de usuários online"),
        @ApiResponse(responseCode = "401", description = "Não autorizado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<UserSummaryResponse>> getOnlineUsers() {
        logger.info("Buscando usuários online");
        List<UserSummaryResponse> users = userService.findOnlineUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Atualiza perfil do usuário
     */
    @PutMapping("/{id}/profile")
    @Operation(summary = "Atualizar perfil", description = "Atualiza informações do perfil do usuário")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Perfil atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "401", description = "Não autorizado"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or @userService.isOwner(authentication.name, #id)")
    public ResponseEntity<UserResponse> updateProfile(
            @Parameter(description = "ID do usuário", required = true)
            @PathVariable UUID id,
            @Parameter(description = "Nome do usuário")
            @RequestParam(required = false) String name,
            @Parameter(description = "Telefone do usuário")
            @RequestParam(required = false) String phone,
            @Parameter(description = "Biografia do usuário")
            @RequestParam(required = false) String bio,
            @Parameter(description = "URL da foto de perfil")
            @RequestParam(required = false) String profilePictureUrl,
            Authentication authentication) {
        
        logger.info("Atualizando perfil do usuário: {}", id);
        UserResponse user = userService.updateProfile(id, name, phone, bio, profilePictureUrl);
        return ResponseEntity.ok(user);
    }

    /**
     * Atualiza senha do usuário
     */
    @PutMapping("/{id}/password")
    @Operation(summary = "Atualizar senha", description = "Atualiza a senha do usuário")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Senha atualizada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Senha atual incorreta"),
        @ApiResponse(responseCode = "401", description = "Não autorizado"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN') or @userService.isOwner(authentication.name, #id)")
    public ResponseEntity<Void> updatePassword(
            @Parameter(description = "ID do usuário", required = true)
            @PathVariable UUID id,
            @Parameter(description = "Senha atual", required = true)
            @RequestParam String currentPassword,
            @Parameter(description = "Nova senha", required = true)
            @RequestParam String newPassword,
            Authentication authentication) {
        
        logger.info("Atualizando senha do usuário: {}", id);
        userService.updatePassword(id, currentPassword, newPassword);
        return ResponseEntity.ok().build();
    }

    /**
     * Ativa/desativa usuário
     */
    @PutMapping("/{id}/toggle-active")
    @Operation(summary = "Ativar/Desativar usuário", description = "Alterna o status ativo do usuário")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status alterado com sucesso"),
        @ApiResponse(responseCode = "401", description = "Não autorizado"),
        @ApiResponse(responseCode = "403", description = "Acesso negado"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Void> toggleActiveStatus(
            @Parameter(description = "ID do usuário", required = true)
            @PathVariable UUID id,
            Authentication authentication) {
        
        logger.info("Alterando status ativo do usuário: {}", id);
        userService.toggleActiveStatus(id);
        return ResponseEntity.ok().build();
    }
}
