package com.librerialumen.api.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.librerialumen.api.domain.model.User;
import com.librerialumen.api.exception.BusinessException;
import com.librerialumen.api.mapper.UserMapper;
import com.librerialumen.api.repository.UserRepository;
import com.librerialumen.api.service.AuditService;
import com.librerialumen.api.web.dto.user.UserCreateDTO;
import com.librerialumen.api.web.dto.user.UserStatusUpdateDTO;
import com.librerialumen.api.web.dto.user.UserViewDTO;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

  @Mock
  private UserRepository userRepository;
  @Mock
  private PasswordEncoder passwordEncoder;
  @Mock
  private UserMapper userMapper;
  @Mock
  private AuditService auditService;

  @InjectMocks
  private UserServiceImpl userService;

  @Test
  void create_shouldNormalizeEmailEncodePasswordAndUppercaseRole() {
    UserCreateDTO dto = UserCreateDTO.builder()
        .email("NewUser@Lumen.test")
        .password("secret123")
        .role("employee")
        .active(null)
        .build();

    when(userRepository.existsByEmail("newuser@lumen.test")).thenReturn(false);
    User userEntity = new User();
    when(userMapper.toEntity(dto)).thenReturn(userEntity);
    when(passwordEncoder.encode("secret123")).thenReturn("ENCODED");
    when(userRepository.save(userEntity)).thenAnswer(invocation -> {
      User saved = invocation.getArgument(0);
      saved.setId(UUID.randomUUID());
      return saved;
    });
    UserViewDTO view = new UserViewDTO();
    when(userMapper.toView(any(User.class))).thenReturn(view);

    UserViewDTO result = userService.create(dto);

    assertSame(view, result);

    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    verify(userRepository).save(userCaptor.capture());
    User saved = userCaptor.getValue();
    assertEquals("newuser@lumen.test", saved.getEmail());
    assertEquals("ENCODED", saved.getPasswordHash());
    assertEquals("EMPLOYEE", saved.getRole());
    assertTrue(saved.isActive());
    verify(auditService).record(eq("User"), anyString(), eq("CREATE"), any(), any());
  }

  @Test
  void create_shouldFailWhenEmailExists() {
    UserCreateDTO dto = UserCreateDTO.builder()
        .email("existing@lumen.test")
        .password("secret123")
        .role("ADMIN")
        .build();
    when(userRepository.existsByEmail("existing@lumen.test")).thenReturn(true);

    BusinessException ex = assertThrows(BusinessException.class, () -> userService.create(dto));
    assertEquals("USER_EMAIL_EXISTS", ex.getCode());
  }

  @Test
  void updateStatus_shouldToggleActiveFlag() {
    UUID userId = UUID.randomUUID();
    UserStatusUpdateDTO dto = UserStatusUpdateDTO.builder()
        .active(false)
        .build();
    User user = new User();
    user.setId(userId);
    user.setActive(true);

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(userRepository.save(user)).thenReturn(user);
    UserViewDTO view = new UserViewDTO();
    when(userMapper.toView(user)).thenReturn(view);

    UserViewDTO result = userService.updateStatus(userId, dto);

    assertSame(view, result);
    assertEquals(false, user.isActive());
    verify(auditService).record(eq("User"), anyString(), eq("STATUS_UPDATE"), any(), any());
  }
}

