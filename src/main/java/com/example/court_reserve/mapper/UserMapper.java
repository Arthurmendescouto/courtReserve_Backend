package com.example.court_reserve.mapper;

import com.example.court_reserve.controller.request.UserRequest;
import com.example.court_reserve.controller.response.UserResponse;
import com.example.court_reserve.entity.Role;
import com.example.court_reserve.entity.User;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UserMapper {
    public static User toUser(UserRequest request) {
        if (request == null) return null;
        return User.create(request.name(), request.email(), request.password(), Role.CLIENT);

    }

    public static UserResponse toUserResponse(User user) {
        if (user == null) return null;
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail().trim())
                .build();
    }
}
