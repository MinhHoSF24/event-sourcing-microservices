package com.microservices.userservice.command.event;

import com.microservices.userservice.mapper.UserMapper;
import com.microservices.userservice.query.readmodel.UserReadModel;
import com.microservices.userservice.query.readmodel.UserReadModelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserEventHandler {
    private final UserReadModelRepository userRepository;
    private final UserMapper userMapper;

    @EventHandler
    public void on(UserCreatedEvent event) {
        log.info("event=UserCreatedEvent userId={} email={}", event.getUserId(), event.getEmail());
        userRepository.save(userMapper.toUserReadModel(event));
    }

    @EventHandler
    public void on(UserUpdatedEvent event) {
        UserReadModel user = userRepository.findByUserId(event.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with userId: " + event.getUserId()));
        userMapper.updateUserFromEvent(event, user);
        userRepository.save(user);
        log.info("event=UserUpdatedEvent userId={}", event.getUserId());
    }

    @EventHandler
    public void on(UserDeletedEvent event) {
        userRepository.findByUserId(event.getUserId()).ifPresent(user -> {
            userRepository.delete(user);
            log.info("event=UserDeletedEvent userId={}", event.getUserId());
        });
    }
}
