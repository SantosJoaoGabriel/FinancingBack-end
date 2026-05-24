package com.gastos.service;

import com.gastos.dto.RegisterUserDTO;
import com.gastos.dto.UserDTO;
import com.gastos.model.User;
import com.gastos.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;

@ApplicationScoped
public class AuthService {

    @Inject
    UserRepository userRepository;

    @Transactional
    public UserDTO register(RegisterUserDTO dto) {
        String emailNormalizado = dto.email.trim().toLowerCase();

        if (userRepository.findByEmail(emailNormalizado).isPresent()) {
            throw new BadRequestException("Já existe um usuário cadastrado com este email");
        }

        User user = new User();
        user.name = dto.name.trim();
        user.email = emailNormalizado;
        user.password = dto.password; // substituir por hash seguro

        userRepository.persist(user);

        return toDTO(user);
    }

    private UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.id = user.id;
        dto.name = user.name;
        dto.email = user.email;
        return dto;
    }
}