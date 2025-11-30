package se.ifmo.database.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import se.ifmo.api.dto.dot.DotDto;
import se.ifmo.database.entity.Dot;

@Mapper(componentModel = MappingConstants.ComponentModel.JAKARTA_CDI)
public interface DotMapper {
    @Mapping(target="id", ignore=true)
    @Mapping(target="owner", ignore=true)
    @Mapping(target="timestamp", expression="java(java.time.Instant.ofEpochMilli(req.timestamp()))")
    Dot toEntity(DotDto req);

    @Mapping(target="timestamp", expression="java(dot.getTimestamp().toEpochMilli())")
    DotDto toDto(Dot dot);
}
