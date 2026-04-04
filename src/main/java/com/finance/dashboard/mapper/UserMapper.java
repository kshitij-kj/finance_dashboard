package com.finance.dashboard.mapper;

import com.finance.dashboard.dto.response.UserResponse;
import com.finance.dashboard.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "role", expression = "java(user.getRole().name())")
    UserResponse toResponse(User user);

    List<UserResponse> toResponseList(List<User> users);
}
