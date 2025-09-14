package com.majorproject.airbnbApp.controllers;

import com.majorproject.airbnbApp.dtos.RoomDto;
import com.majorproject.airbnbApp.services.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Tag(name = "Room", description = "Admin APIs for managing hotel rooms")
@RestController
@RequestMapping("/admin/hotels/{hotelId}/rooms")
@Slf4j
@RequiredArgsConstructor
public class RoomAdminController {

    private final RoomService roomService;

    @Operation(
            summary = "Create a new room in a hotel",
            description = "Creates a new room under the specified hotel ID with the provided room details"
    )
    @PostMapping
    public ResponseEntity<RoomDto> createRoom(@PathVariable Long hotelId,
                                              @RequestBody @Valid RoomDto roomDto) {
        RoomDto roomDto1 = roomService.createNewRoom(hotelId, roomDto);
        return new ResponseEntity<>(roomDto1, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Get all rooms in a hotel",
            description = "Fetches the list of all rooms available in the specified hotel"
    )
    @GetMapping
    public ResponseEntity<List<RoomDto>> allRooms(@PathVariable Long hotelId) {
        return ResponseEntity.ok(roomService.getAllRoomsInHotel(hotelId));
    }

    @Operation(
            summary = "Get a room by ID",
            description = "Fetches the details of a specific room in the hotel by its room ID"
    )
    @GetMapping("/{roomId}")
    public ResponseEntity<RoomDto> getRoomById(@PathVariable Long hotelId,
                                               @PathVariable Long roomId) {
        return ResponseEntity.ok(roomService.getRoomById(roomId));
    }

    @Operation(
            summary = "Delete a room by ID",
            description = "Deletes the specified room from the hotel using its room ID"
    )
    @DeleteMapping("/{roomId}")
    public ResponseEntity<Void> deleteRoomById(@PathVariable Long hotelId,
                                               @PathVariable Long roomId) {
        roomService.deleteRoomById(roomId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Update a room by ID",
            description = "Updates the details of the specified room in the hotel using the room ID and request body"
    )
    @PutMapping("/{roomId}")
    public ResponseEntity<RoomDto> updateRoomById(@PathVariable Long hotelId,
                                                  @PathVariable Long roomId,
                                                  @RequestBody @Valid RoomDto roomDto) {
        return ResponseEntity.ok(roomService.updateRoomById(hotelId, roomId, roomDto));
    }

}
