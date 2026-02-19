package com.example.court_reserve.controller;

import java.time.LocalDateTime;
import java.util.Map;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException; // IMPORTANTE
import org.springframework.web.bind.annotation.*;

import com.example.court_reserve.controller.request.CourtRequest;
import com.example.court_reserve.controller.response.CourtResponse;
import com.example.court_reserve.entity.Court;
import com.example.court_reserve.entity.SportType;
import com.example.court_reserve.mapper.CourtMapper;
import com.example.court_reserve.service.CourtService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Court", description = "Recurso responsável pelas quadras.")
@RestController
@RequestMapping("/court_reserve/courts")
@RequiredArgsConstructor
public class CourtController {
    private final CourtService courtService;

    @Operation(summary = "Listar quadras", description = "Retorna todas as quadras cadastradas.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de quadras revelada com sucesso."),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso proibido.")
    })
    @GetMapping
    public ResponseEntity<Page<CourtResponse>> getAllCourts(
            @ParameterObject Pageable pageable,
            @RequestParam(required = false) SportType sportType,
            @Parameter(description = "Nome da quadra para busca textual")
            @RequestParam(required = false) String name,
            @Parameter(description = "Data de início (ISO 8601)", example = "2024-12-21T14:00:00")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime availableFrom,
            @Parameter(description = "Data de fim (ISO 8601)", example = "2024-12-21T15:00:00")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime availableTo
    ){
        Page<CourtResponse> page = courtService.findAll(pageable, sportType, availableFrom, availableTo, name)
                .map(CourtMapper::toCourtResponse);
        return ResponseEntity.ok(page);
    }

    @Operation(summary = "Buscar quadra por ID", description = "Retorna uma quadra específica pelo ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Quadra encontrada."),
            @ApiResponse(responseCode = "404", description = "Quadra não encontrada."),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso proibido.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CourtResponse> getCourtsById(@PathVariable Long id){
        return courtService.findById(id)
                .map(court -> ResponseEntity.ok(CourtMapper.toCourtResponse(court)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Criar quadra", description = "Cria uma nova quadra.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Quadra criada com sucesso."),
            @ApiResponse(responseCode = "400", description = "Dados inválidos."),
            @ApiResponse(responseCode = "401", description = "Não autorizado."),
            @ApiResponse(responseCode = "403", description = "Acesso proibido. Apenas administradores podem criar quadras.")
    })
    @PostMapping
    public ResponseEntity<CourtResponse> saveCourt(@RequestBody CourtRequest request){
        Court newCourt = CourtMapper.toCourt(request);
        Court savedCourt = courtService.save(newCourt);
        return ResponseEntity.status(HttpStatus.CREATED).body(CourtMapper.toCourtResponse(savedCourt));
    }

    @Operation(summary = "Deletar quadra", description = "Remove uma quadra pelo ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Quadra deletada com sucesso."),
            @ApiResponse(responseCode = "409", description = "Conflito. A quadra não pode ser deletada pois possui reservas associadas."),
            @ApiResponse(responseCode = "403", description = "Acesso proibido. Apenas administradores podem deletar quadras.")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteByCourtId(@PathVariable Long id){
        courtService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(summary = "Atualizar quadra", description = "Atualiza uma quadra existente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Quadra atualizada com sucesso."),
            @ApiResponse(responseCode = "404", description = "Quadra não encontrada."),
            @ApiResponse(responseCode = "400", description = "Dados inválidos."),
            @ApiResponse(responseCode = "401", description = "Não autorizado."),
            @ApiResponse(responseCode = "403", description = "Acesso proibido. Apenas administradores podem atualizar quadras.")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<CourtResponse> patchCourt(@PathVariable Long id, @RequestBody CourtRequest request) {
        Court updatedCourt = courtService.updateCourt(id, request);
        return ResponseEntity.ok(CourtMapper.toCourtResponse(updatedCourt));
    }

    // --- TRATADORES DE EXCEÇÃO ---

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccessDeniedException(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "Acesso proibido. Apenas administradores podem realizar esta ação."));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", "Esta quadra não pode ser excluída pois possui reservas associadas."));
    }

    @ExceptionHandler(org.springframework.dao.EmptyResultDataAccessException.class)
    public ResponseEntity<Map<String, String>> handleEmptyResult(org.springframework.dao.EmptyResultDataAccessException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Quadra não encontrada. O ID informado não existe."));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
    }
}