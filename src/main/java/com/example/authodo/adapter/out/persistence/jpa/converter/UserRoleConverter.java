package com.example.authodo.adapter.out.persistence.jpa.converter;

import com.example.authodo.domain.user.enums.UserRole;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class UserRoleConverter extends BaseEnumConverter<UserRole> {

    public UserRoleConverter() {
        super(UserRole.class);
    }
}
