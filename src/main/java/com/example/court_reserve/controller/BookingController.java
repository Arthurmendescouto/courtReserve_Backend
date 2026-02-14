package com.example.court_reserve.controller;

import java.util.List;
import java.util.Map;
import java.net.URI;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.court_reserve.controller.request.BookingRequest;
import com.example.court_reserve.controller.response.BookingResponse;
import com.example.court_reserve.entity.Booking;
import com.example.court_reserve.mapper.BookingMapper;
import com.example.court_reserve.service.BookingService;
import jakarta.persistence.EntityNotFoundException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/court_reserve/bookings")
@RequiredArgsConstructor
@Tag(name = "Booking", description = "Recurso responsável pelos agendamentos.")
public class BookingController {

    private final BookingService bookingService;

    @Operation(summary = "Listar agendamentos", description = "Retorna todos os agendamentos cadastrados.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de agendamentos revelada com sucesso."),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso proibido.")
    })
    @GetMapping
    public ResponseEntity<Page<BookingResponse>> getAllBookings(@ParameterObject Pageable pageable) {
        Page<BookingResponse> bookings = bookingService.findAll(pageable)
                .map(booking -> BookingMapper.toBookingResponse(booking));
        return ResponseEntity.ok(bookings);
    }

    @Operation(summary = "Buscar agendamento por ID", description = "Retorna um agendamento específico pelo ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Agendamento encontrado."),
            @ApiResponse(responseCode = "404", description = "Agendamento não encontrado."),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso proibido.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getByBookingId(@PathVariable Long id) {
        return bookingService.findById(id)
                .map(booking -> ResponseEntity.ok(BookingMapper.toBookingResponse(booking)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Criar agendamento",
            description = "Cria um novo agendamento. **Nota:** Reservas para quadras de TÊNIS sofrem um acréscimo de 50% no valor da hora aos sábados e domingos."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Agendamento criado com sucesso.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BookingResponse.class),
                            examples = @ExampleObject(
                                    name = "Exemplo de Sucesso",
                                    summary = "Resposta para um agendamento bem-sucedido",
                                    value = """
                                            {
                                              "id": 1,
                                              "startDateTime": "2030-02-16T10:00:00",
                                              "endDateTime": "2030-02-16T11:00:00",
                                              "totalPrice": 120.0,
                                              "user": { "id": 2, "name": "Cliente Jogador", "email": "cliente@exemplo.com" },
                                              "court": { "id": 2, "name": "Ace Tennis Club", "sportType": "TENNIS", "pricePerHour": 80.0 }
                                            }"""))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou conflito de horário.",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "Conflito de Horário", summary = "Horário já reservado", value = "{\"message\": \"Já existe reserva para este horário nesta quadra.\"}"),
                                    @ExampleObject(name = "Datas Inválidas", summary = "Data de início após o fim", value = "{\"message\": \"Data de início deve ser anterior ao fim.\"}")
                            })),
            @ApiResponse(responseCode = "404", description = "Usuário ou Quadra não encontrado.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "Entidade não encontrada", value = "{\"message\": \"Quadra não encontrada.\"}")))
    })
    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados para criação da reserva",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Exemplo de Requisição",
                                    value = """
                                            {
                                              "userId": 2,
                                              "courtId": 2,
                                              "startDateTime": "2030-02-16T10:00:00",
                                              "endDateTime": "2030-02-16T11:00:00"
                                            }"""
                            )
                    )
            )
            @RequestBody BookingRequest request) {
        Booking saved = bookingService.createBooking(request);
        return ResponseEntity.created(URI.create("/court_reserve/bookings/" + saved.getId()))
                .body(BookingMapper.toBookingResponse(saved));
    }

    @Operation(
            summary = "Atualizar agendamento",
            description = "Atualiza um agendamento existente. O preço total será recalculado automaticamente com base nos novos horários e regras de fim de semana."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Agendamento atualizado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Agendamento não encontrado."),
            @ApiResponse(responseCode = "400", description = "Dados inválidos."),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso proibido.")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<BookingResponse> updateBooking(
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            examples = @ExampleObject(
                                    name = "Exemplo de Atualização",
                                    value = """
                                            {
                                              "userId": 2,
                                              "courtId": 2,
                                              "startDateTime": "2030-02-16T12:00:00",
                                              "endDateTime": "2030-02-16T13:00:00"
                                            }"""
                            )
                    )
            )
            @RequestBody BookingRequest request) {

        Booking updatedBooking = bookingService.update(id, request);
        return ResponseEntity.ok(BookingMapper.toBookingResponse(updatedBooking));
    }

    @Operation(summary = "Deletar agendamento", description = "Remove um agendamento pelo ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Agendamento deletado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Agendamento não encontrado."),
            @ApiResponse(responseCode = "403", description = "Acesso proibido.")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteByBookingId(@PathVariable Long id) {
        bookingService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleEntityNotFound(EntityNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", ex.getMessage()));
    }
}