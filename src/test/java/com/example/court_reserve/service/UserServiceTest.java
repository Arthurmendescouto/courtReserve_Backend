package com.example.court_reserve.service;

import com.example.court_reserve.entity.User;
import com.example.court_reserve.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Deve salvar usuário com sucesso")
    void deveSalvarUsuario() {
        User user = User.create("Teste", "teste@email.com", "123");
        
        when(passwordEncoder.encode(any())).thenReturn("senhaCriptografada");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User salvo = userService.save(user);

        assertNotNull(salvo);
        assertEquals("Teste", salvo.getName());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Deve encontrar usuário por ID existente")
    void deveEncontrarUsuarioPorId() {
        User user = User.create("Teste", "teste@email.com", "123");
        user.setId(1L); // Define o ID para o teste passar
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<User> encontrado = userService.findById(1L);

        assertTrue(encontrado.isPresent());
        assertEquals(1L, encontrado.get().getId());
    }

    // Nota: Se o seu UserService.findById retorna Optional, ele não lança erro.
    // Se ele retornasse User direto, faríamos o teste de erro aqui.
    // Vou assumir o padrão do seu código onde o Service repassa o Optional do Repository.
    @Test
    @DisplayName("Deve retornar vazio quando usuário não existe")
    void deveRetornarVazioSeNaoExiste() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<User> encontrado = userService.findById(99L);

        assertFalse(encontrado.isPresent());
    }
}