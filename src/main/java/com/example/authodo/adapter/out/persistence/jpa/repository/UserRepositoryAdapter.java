package com.example.authodo.adapter.out.persistence.jpa.repository;

import com.example.authodo.adapter.out.persistence.jpa.entity.UserJpaEntity;
import com.example.authodo.domain.user.User;
import com.example.authodo.domain.user.port.out.UserRepositoryPort;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final SpringDataUserRepository springDataUserRepository;

    private static UserJpaEntity toEntity(User user) {
        return UserJpaEntity.builder()
            .id(user.getId())
            .email(user.getEmail())
            .password(user.getPassword())
            .nickname(user.getNickname())
            .build();
    }

    private static User toDomain(UserJpaEntity userJpaEntity) {
        return User.builder()
            .id(userJpaEntity.getId())
            .email(userJpaEntity.getEmail())
            .password(userJpaEntity.getPassword())
            .nickname(userJpaEntity.getNickname())
            .createdAt(userJpaEntity.getCreatedAt())
            .modifiedAt(userJpaEntity.getModifiedAt())
            .build();
    }

    @Override
    public User save(User user) {
        UserJpaEntity saved = springDataUserRepository.save(toEntity(user));
        return toDomain(saved);
    }

    @Override
    public Optional<User> findById(Long id) {
        return springDataUserRepository.findById(id).map(UserRepositoryAdapter::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return springDataUserRepository.findByEmail(email).map(UserRepositoryAdapter::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return springDataUserRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByNickname(String nickname) {
        return springDataUserRepository.existsByNickname(nickname);
    }
}
