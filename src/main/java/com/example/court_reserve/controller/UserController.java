package com.example.court_reserve.controller;

import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.court_reserve.controller.request.PasswordUpdateRequest;
import com.example.court_reserve.controller.response.UserResponse;
import com.example.court_reserve.mapper.UserMapper;
import com.example.court_reserve.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer"
)
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "User",description = "Recurso responsável pelos usuários.")
@RestController
@RequestMapping("/court_reserve/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldError().getDefaultMessage();
        return ResponseEntity.badRequest().body(Map.of("error", errorMessage));
    }

    @Operation(summary = "Listar usuários", description = "Retorna todos os usuários cadastrados.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de usuários revelada com sucesso."),
        @ApiResponse(responseCode = "401", description = "Não autorizado"),
        @ApiResponse(responseCode = "403", description = "Acesso proibido.")
    })
    @GetMapping
    public ResponseEntity<Page<UserResponse>> getAllUsers(@ParameterObject Pageable pageable){
        Page<UserResponse> users=userService.findAll(pageable)
                .map(user -> UserMapper.toUserResponse(user));
        return ResponseEntity.ok(users);
    }
    @Operation(summary = "Buscar usuário por ID", description = "Retorna um usuário específico pelo ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuário encontrado."),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado."),
        @ApiResponse(responseCode = "401", description = "Não autorizado"),
        @ApiResponse(responseCode = "403", description = "Acesso proibido.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id){
        return userService.findById(id)
                .map(user -> ResponseEntity.ok(UserMapper.toUserResponse(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Deletar usuário", description = "Remove um usuário pelo ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Usuário deletado com sucesso."),
            @ApiResponse(responseCode = "404", description = "usuário não encontrado."),
            @ApiResponse(responseCode = "409",description ="Conflito. O usuário não pode ser deletado pois possui reservas associadas."),
            @ApiResponse(responseCode = "403", description = "Acesso proibido.")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteByUserId(@PathVariable Long id){
        userService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    @Operation(summary = "Atualizar senha do usuário", description = "Atualiza a senha de um usuário existente.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Senha atualizada com sucesso."),
        @ApiResponse(responseCode = "400", description = "Dados inválidos."),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado."),
        @ApiResponse(responseCode = "401", description = "Não autorizado"),
        @ApiResponse(responseCode = "403", description = "Acesso proibido.")
    })
    @PatchMapping("/{id}/password")
    public ResponseEntity<Void> updatePassword(@PathVariable Long id, @Valid @RequestBody PasswordUpdateRequest request) {
        userService.updatePassword(id, request.password());
        return ResponseEntity.noContent().build();
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        String errorMessage = "Este usuário não pode ser excluído pois possui reservas associadas.";

        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", errorMessage));
    }
    @ExceptionHandler(org.springframework.dao.EmptyResultDataAccessException.class)
    public ResponseEntity<Map<String, String>> handleEmptyResult(org.springframework.dao.EmptyResultDataAccessException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Usuário não encontrado. O ID informado não existe."));
    }
}
