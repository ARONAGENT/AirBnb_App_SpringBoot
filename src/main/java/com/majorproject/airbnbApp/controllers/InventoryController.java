package com.majorproject.airbnbApp.controllers;

import com.majorproject.airbnbApp.dtos.InventoryDto;
import com.majorproject.airbnbApp.dtos.UpdateInventoryRequestDto;
import com.majorproject.airbnbApp.services.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Tag(name = "Inventory", description = "Admin APIs for managing room inventory")
@RestController
@RequestMapping("/admin/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @Operation(
            summary = "Get all inventory for a specific room",
            description = "Fetches the list of all inventory items associated with the given room ID"
    )
    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<List<InventoryDto>> getAllInventoryByRoom(@PathVariable Long roomId) {
        return ResponseEntity.ok(inventoryService.getAllInventoryByRoom(roomId));
    }

    @Operation(
            summary = "Update inventory for a specific room",
            description = "Updates the inventory items for the given room ID based on the request data"
    )
    @PatchMapping("/rooms/{roomId}")
    public ResponseEntity<Void> updateInventory(@PathVariable Long roomId,
                                                @RequestBody @Valid UpdateInventoryRequestDto updateInventoryRequestDto) {
        inventoryService.updateInventory(roomId, updateInventoryRequestDto);
        return ResponseEntity.noContent().build();
    }
}
