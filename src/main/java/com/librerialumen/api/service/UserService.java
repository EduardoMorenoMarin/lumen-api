package com.librerialumen.api.service;

import com.librerialumen.api.web.dto.user.UserCreateDTO;
import com.librerialumen.api.web.dto.user.UserStatusUpdateDTO;
import com.librerialumen.api.web.dto.user.UserViewDTO;
import java.util.List;
import java.util.UUID;

public interface UserService {

  UserViewDTO create(UserCreateDTO dto);

  UserViewDTO get(UUID userId);

  List<UserViewDTO> list();

  UserViewDTO updateStatus(UUID userId, UserStatusUpdateDTO dto);
}
