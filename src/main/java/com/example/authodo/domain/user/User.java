package com.example.authodo.domain.user;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User {

    private final Long id;

    private final String email;

    private final String password;

    private final String nickname;

    private final LocalDateTime createdAt;

    private final LocalDateTime modifiedAt;

    public static User create(String email, String password, String nickname) {
        return User.builder()
            .id(null)
            .email(email)
            .password(password)
            .nickname(nickname)
            .build();
    }
}
