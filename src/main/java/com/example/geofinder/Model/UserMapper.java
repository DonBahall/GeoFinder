package com.example.geofinder.Model;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;


@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
    @Mappings({
            @Mapping(target="id", source="user.id"),
            @Mapping(target="name", source="user.name"),
            @Mapping(target="email", source="user.email")
    })
    UserDto map(UserModel user);
}