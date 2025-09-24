package com.librerialumen.api.mapper;

import com.librerialumen.api.domain.model.User;
import com.librerialumen.api.web.dto.auth.MeResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface AuthMapper {

  MeResponse toMeResponse(User user);
}
