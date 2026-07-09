package com.microservices.userservice.command.controller;

import com.microservices.userservice.command.command.CreateUserCommand;
import com.microservices.userservice.command.command.DeleteUserCommand;
import com.microservices.userservice.command.command.UpdateUserCommand;
import com.microservices.userservice.command.model.CreateUserRequestModel;
import com.microservices.userservice.command.model.UpdateUserRequestModel;
import com.microservices.userservice.domain.model.UserRegistrationProfile;
import com.microservices.userservice.domain.policy.UserRegistrationPolicy;
import com.microservices.userservice.infrastructure.identity.IdentityAccessService;
import com.microservices.userservice.query.readmodel.UserReadModel;
import com.microservices.userservice.query.readmodel.UserReadModelRepository;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserCommandController {
    private final CommandGateway commandGateway;
    private final IdentityAccessService identityAccessService;
    private final UserReadModelRepository userRepository;

    @PostMapping
    public ResponseEntity<String> createUser(@RequestBody CreateUserRequestModel model) {
        UserRegistrationProfile profile = UserRegistrationProfile.of(
                model.getEmail(),
                model.getUsername(),
                model.getFirstName(),
                model.getLastName(),
                model.getDob(),
                model.getName(),
                model.getPassword());
        UserRegistrationPolicy.validate(profile);

        String userId = identityAccessService.createIdentityUser(profile);
        commandGateway.sendAndWait(CreateUserCommand.from(userId, profile));
        return ResponseEntity.ok(userId);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateUser(
            @PathVariable Long id,
            @RequestBody UpdateUserRequestModel model) {
        String userId = resolveUserId(id);
        commandGateway.sendAndWait(UpdateUserCommand.from(userId, model));
        return ResponseEntity.ok(userId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        commandGateway.sendAndWait(new DeleteUserCommand(resolveUserId(id)));
        return ResponseEntity.noContent().build();
    }

    private String resolveUserId(Long id) {
        UserReadModel user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        if (user.getUserId() == null || user.getUserId().isBlank()) {
            throw new IllegalStateException("User aggregate id is missing for id: " + id);
        }
        return user.getUserId();
    }
}
