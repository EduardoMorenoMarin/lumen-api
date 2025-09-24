package com.librerialumen.api.service.impl;

import com.librerialumen.api.domain.model.User;
import com.librerialumen.api.exception.BusinessException;
import com.librerialumen.api.mapper.UserMapper;
import com.librerialumen.api.repository.UserRepository;
import com.librerialumen.api.service.AuditService;
import com.librerialumen.api.service.UserService;
import com.librerialumen.api.web.dto.user.UserCreateDTO;
import com.librerialumen.api.web.dto.user.UserStatusUpdateDTO;
import com.librerialumen.api.web.dto.user.UserViewDTO;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final UserMapper userMapper;
  private final AuditService auditService;

  @Override
  public UserViewDTO create(UserCreateDTO dto) {
    String normalizedEmail = dto.getEmail().toLowerCase(Locale.ROOT);
    if (userRepository.existsByEmail(normalizedEmail)) {
      throw new BusinessException("USER_EMAIL_EXISTS", "Email already registered");
    }

    User user = userMapper.toEntity(dto);
    user.setEmail(normalizedEmail);
    user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
    user.setRole(dto.getRole().toUpperCase(Locale.ROOT));
    user.setActive(dto.getActive() != null ? dto.getActive() : true);

    User saved = userRepository.save(user);
    auditService.record("User", saved.getId().toString(), "CREATE", null,
        Map.of("email", saved.getEmail(), "role", saved.getRole()));
    return userMapper.toView(saved);
  }

  @Override
  @Transactional(readOnly = true)
  public UserViewDTO get(UUID userId) {
    return userRepository.findById(userId)
        .map(userMapper::toView)
        .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "User not found"));
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserViewDTO> list() {
    return userMapper.toViewList(userRepository.findAll());
  }

  @Override
  public UserViewDTO updateStatus(UUID userId, UserStatusUpdateDTO dto) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "User not found"));
    user.setActive(dto.getActive());
    User saved = userRepository.save(user);
    auditService.record("User", saved.getId().toString(), "STATUS_UPDATE", null,
        Map.of("active", saved.isActive()));
    return userMapper.toView(saved);
  }
}
