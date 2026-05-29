package com.gastos.service;

import com.gastos.dto.LoginDTO;
import com.gastos.dto.LoginResponseDTO;
import com.gastos.dto.RegisterUserDTO;
import com.gastos.dto.UserDTO;
import com.gastos.model.User;
import com.gastos.repository.UserRepository;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;

import java.time.Duration;
import java.util.Set;

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
        user.password = BcryptUtil.bcryptHash(dto.password);

        userRepository.persist(user);

        return toDTO(user);
    }

    public LoginResponseDTO login(LoginDTO dto) {
        String emailNormalizado = dto.email.trim().toLowerCase();

        User user = userRepository.findByEmail(emailNormalizado)
                .orElseThrow(() -> new BadRequestException("Email ou senha inválidos"));

        boolean senhaValida = BcryptUtil.matches(dto.password, user.password);

        if (!senhaValida) {
            throw new BadRequestException("Email ou senha inválidos");
        }

        String token = Jwt.issuer("saveup-api")
                .upn(user.email)
                .subject(String.valueOf(user.id))
                .groups(Set.of("USER"))
                .expiresIn(Duration.ofHours(2))
                .sign();

        LoginResponseDTO response = new LoginResponseDTO();
        response.token = token;
        response.user = toDTO(user);

        return response;
    }

    private UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.id = user.id;
        dto.name = user.name;
        dto.email = user.email;
        return dto;
    }
}