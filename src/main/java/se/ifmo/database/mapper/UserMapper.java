package se.ifmo.database.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import se.ifmo.api.dto.auth.UserDto;
import se.ifmo.database.entity.User;

import java.util.UUID;

@Mapper(componentModel = MappingConstants.ComponentModel.JAKARTA_CDI)
public interface UserMapper {

    @Mapping(target = "id", source = "uuid", qualifiedByName = "uuidToString")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "password", source = "passwordHash")
    UserDto toDto(User user);

    @Mapping(target = "uuid", source = "id", qualifiedByName = "stringToUuid")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "passwordHash", source = "password")
    @Mapping(target = "salt", ignore = true)
    User toEntity(UserDto dto);

    @Named("uuidToString")
    default String uuidToString(UUID uuid) {
        return uuid != null ? uuid.toString() : null;
    }

    @Named("stringToUuid")
    default UUID stringToUuid(String id) {
        return id != null ? UUID.fromString(id) : null;
    }
}
