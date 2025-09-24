package com.librerialumen.api.web.controller;

import com.librerialumen.api.service.UserService;
import com.librerialumen.api.web.dto.user.UserCreateDTO;
import com.librerialumen.api.web.dto.user.UserStatusUpdateDTO;
import com.librerialumen.api.web.dto.user.UserViewDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Validated
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Users", description = "Administration of system users")
@SecurityRequirement(name = "bearerAuth")
public class UsersController {

  private final UserService userService;

  @PostMapping
  @Operation(summary = "Create user", description = "Registers a new system user")
  public UserViewDTO create(@Valid @RequestBody UserCreateDTO dto) {
    return userService.create(dto);
  }

  @GetMapping
  @Operation(summary = "List users", description = "Returns all system users")
  public List<UserViewDTO> list() {
    return userService.list();
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get user", description = "Fetches a user by identifier")
  public UserViewDTO get(
      @Parameter(description = "User identifier") @PathVariable("id") UUID id) {
    return userService.get(id);
  }

  @PutMapping("/{id}/status")
  @Operation(summary = "Update user status", description = "Updates activation status for a user")
  public UserViewDTO updateStatus(
      @Parameter(description = "User identifier") @PathVariable("id") UUID id,
      @Valid @RequestBody UserStatusUpdateDTO dto) {
    return userService.updateStatus(id, dto);
  }
}
