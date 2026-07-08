package com.microservices.userservice.services.implement;

import com.microservices.userservice.dto.CreateUserRequestDTO;
import com.microservices.userservice.dto.LoginRequestDTO;
import com.microservices.userservice.dto.UpdateUserRequestDTO;
import com.microservices.userservice.dto.UserResponseDTO;
import com.microservices.userservice.dto.identities.Credential;
import com.microservices.userservice.dto.identities.TokenExchangeParam;
import com.microservices.userservice.dto.identities.TokenExchangeResponse;
import com.microservices.userservice.dto.identities.UserCreationParam;
import com.microservices.userservice.dto.identities.UserTokenExchangeParam;
import com.microservices.userservice.entities.User;
import com.microservices.userservice.mapper.UserMapper;
import com.microservices.userservice.repositories.IdentityClient;
import com.microservices.userservice.repositories.UserRepository;

import com.microservices.userservice.services.IUserService;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;

    private final IdentityClient identityClient;

    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, IdentityClient identityClient, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.identityClient = identityClient;
        this.userMapper = userMapper;
    }

    @Value("${idp.client-id}")
    @NonFinal
    String clientId;

    @Value("${idp.client-secret}")
    @NonFinal
    String clientSecret;

    @Override
    public UserResponseDTO createUser(CreateUserRequestDTO dto) {
        var token = identityClient.exchangeClientToken(TokenExchangeParam.builder()
                .grant_type("client_credentials")
                .client_secret(clientSecret)
                .client_id(clientId)
                .scope("openid")
                .build());

        log.info("Token info {}", token);
        var creationResponse = identityClient.createUser(UserCreationParam.builder()
                .username(dto.getUsername())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .enabled(true)
                .emailVerified(false)
                .credentials(List.of(Credential.builder()
                        .type("password")
                        .temporary(false)
                        .value(dto.getPassword())
                        .build()))
                .build(), "Bearer " + token.getAccessToken());

        String userId = extractUserId(creationResponse);
        log.info("UserId {}", userId);

        User user = userMapper.toUser(dto);
        user.setUserId(userId);
        userRepository.save(user);
        return userMapper.toUserResponseDTO(user);
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream().map(userMapper::toUserResponseDTO).collect(Collectors.toList());
    }

    @Override
    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return userMapper.toUserResponseDTO(user);
    }

    @Override
    public UserResponseDTO updateUser(Long id, UpdateUserRequestDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        userMapper.updateUser(user, dto);

        return userMapper.toUserResponseDTO(userRepository.save(user));
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    // private UserResponseDTO toDTO(User user) {
    //     return UserResponseDTO.builder()
    //             .userId(user.getUserId())
    //             .email(user.getEmail())
    //             .username(user.getUsername())
    //             .firstName(user.getFirstName())
    //             .lastName(user.getLastName())
    //             .dob(user.getDob())
    //             .name(user.getName())
    //             .id(user.getId())
    //             .build();
    // }

    private String extractUserId(ResponseEntity<?> response) {
        List<String> locations = response.getHeaders().get("Location");
        if (locations == null || locations.isEmpty()) {
            throw new IllegalStateException("Location header is missing in the response");
        }

        String location = locations.get(0);
        String[] splitedStr = location.split("/");
        return splitedStr[splitedStr.length - 1];
    }

     @Override
    public TokenExchangeResponse login(LoginRequestDTO dto) {
        var token = identityClient.exchangeUserToken(UserTokenExchangeParam.builder()
                .grant_type("password")
                .client_id(clientId)
                .client_secret(clientSecret)
                .scope("openid")
                .username(dto.getUsername())
                .password(dto.getPassword())
                .build());
        return token;
    }

}