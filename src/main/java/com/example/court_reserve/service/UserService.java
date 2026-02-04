package com.example.court_reserve.service;

import com.example.court_reserve.controller.request.UserRequest;
import com.example.court_reserve.entity.User;
import com.example.court_reserve.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public Page<User> findAll(Pageable pageable){
        return repository.findAll(pageable);
    }

    public Optional<User> findById(Long id){
        return repository.findById(id);
    }

    public User save(User user){
        String password=user.getPassword();
        user.setPassword(passwordEncoder.encode(password));
        return repository.save(user);
    }

    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EmptyResultDataAccessException("Usuário não encontrado com o ID: " + id, 1);
        }
        userRepository.deleteById(id);
    }


    public void updatePassword(Long id, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EmptyResultDataAccessException("Usuário não encontrado com o ID: " + id, 1));

        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        userRepository.save(user);
    }
}
