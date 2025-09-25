package com.librerialumen.api.web.controller;

import com.librerialumen.api.exception.BusinessException;
import com.librerialumen.api.repository.UserRepository;
import com.librerialumen.api.service.InventoryService;
import com.librerialumen.api.web.dto.inventory.InventoryAdjustmentRequest;
import com.librerialumen.api.web.dto.inventory.InventoryAdjustmentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.security.Principal;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
@Validated
@PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
@Tag(name = "Inventory", description = "Stock adjustments and queries")
@SecurityRequirement(name = "bearerAuth")
public class InventoryController {

  private final InventoryService inventoryService;
  private final UserRepository userRepository;

  @PostMapping("/adjust")
  @Operation(summary = "Adjust stock", description = "Registers a manual stock adjustment for a product")
  public InventoryAdjustmentResponse adjustStock(
      @Valid @RequestBody InventoryAdjustmentRequest request,
      Principal principal) {
    UUID actorId = resolveActorId(principal);
    int stock = inventoryService.adjustStock(request.getProductId(), request.getDelta(),
        request.getReason(), actorId);
    return InventoryAdjustmentResponse.builder()
        .productId(request.getProductId())
        .stock(stock)
        .adjustedAt(Instant.now())
        .build();
  }

  @GetMapping("/products/{productId}/stock")
  @Operation(summary = "Get stock", description = "Returns the current stock for a product")
  public InventoryAdjustmentResponse getStock(
      @Parameter(description = "Product identifier") @PathVariable("productId") UUID productId) {
    int stock = inventoryService.getCurrentStock(productId);
    return InventoryAdjustmentResponse.builder()
        .productId(productId)
        .stock(stock)
        .adjustedAt(Instant.now())
        .build();
  }

  private UUID resolveActorId(Principal principal) {
    if (principal == null) {
      throw new BusinessException("AUTH_REQUIRED", "Authentication required");
    }
    return userRepository.findByEmail(principal.getName())
        .map(com.librerialumen.api.domain.model.User::getId)
        .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "Authenticated user not found"));
  }
}
