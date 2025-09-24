package com.librerialumen.api.mapper;

import com.librerialumen.api.domain.model.User;
import com.librerialumen.api.web.dto.user.UserCreateDTO;
import com.librerialumen.api.web.dto.user.UserViewDTO;
import java.util.List;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

  @Mapping(target = "passwordHash", ignore = true)
  @Mapping(target = "active", ignore = true)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  User toEntity(UserCreateDTO dto);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateEntity(UserCreateDTO dto, @MappingTarget User user);

  UserViewDTO toView(User user);

  List<UserViewDTO> toViewList(List<User> users);
}
