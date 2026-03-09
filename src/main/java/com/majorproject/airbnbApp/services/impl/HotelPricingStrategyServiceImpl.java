package com.majorproject.airbnbApp.services.impl;

import com.majorproject.airbnbApp.dtos.strategyDto.CustomStrategyRequestDto;
import com.majorproject.airbnbApp.dtos.strategyDto.HolidayRequestDto;
import com.majorproject.airbnbApp.dtos.strategyDto.HotelStrategyUpdateDto;
import com.majorproject.airbnbApp.dtos.strategyDto.PricingStrategyResponseDto;
import com.majorproject.airbnbApp.entities.*;
import com.majorproject.airbnbApp.exceptions.ResourceNotFoundException;
import com.majorproject.airbnbApp.exceptions.UnAuthorisedException;
import com.majorproject.airbnbApp.repositories.*;
import com.majorproject.airbnbApp.services.HotelPricingStrategyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.majorproject.airbnbApp.utils.AppUtils.getCurrentUser;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class HotelPricingStrategyServiceImpl implements HotelPricingStrategyService {

    private final PricingStrategyRepository pricingStrategyRepository;
    private final HotelPricingStrategyRepository hotelPricingStrategyRepository;
    private final HotelRepository hotelRepository;
    private final HolidayRepository holidayRepository;

    // ── 1. Manager creates a custom strategy ─────────────────────────────
    @Override
    public PricingStrategyResponseDto createCustomStrategy(CustomStrategyRequestDto dto) {
        User currentManager = getCurrentUser();

        // Edge: strategy name must not be blank
        if (dto.getStrategyName() == null || dto.getStrategyName().isBlank()) {
            throw new IllegalArgumentException("Strategy name must not be blank.");
        }

        // Edge: multiplication factor must be positive
        if (dto.getMultiplicationFactor() == null || dto.getMultiplicationFactor().doubleValue() <= 0) {
            throw new IllegalArgumentException("Multiplication factor must be a positive value.");
        }

        // Edge: prevent using reserved default strategy names
        List<String> reservedNames = List.of("BASED", "SURGE", "OCCUPANCY", "HOLIDAY", "URGENCY");
        if (reservedNames.contains(dto.getStrategyName().toUpperCase())) {
            throw new IllegalArgumentException("Strategy name '" + dto.getStrategyName()
                    + "' is reserved. Please use a different name.");
        }

        PricingStrategy strategy = new PricingStrategy();
        strategy.setStrategyName(dto.getStrategyName().toUpperCase());
        strategy.setMultiplicationFactor(dto.getMultiplicationFactor());
        strategy.setIsDefault(false);
        strategy.setCreatedByManager(currentManager);
        strategy.setIsActive(true);

        PricingStrategy saved = pricingStrategyRepository.save(strategy);
        log.info("Custom strategy '{}' created by manager ID: {}", saved.getStrategyName(), currentManager.getId());
        return toResponseDto(saved);
    }

    // ── 2. Get all strategies available to logged-in manager ─────────────
    @Override
    public List<PricingStrategyResponseDto> getAvailableStrategies() {
        User currentManager = getCurrentUser();

        return pricingStrategyRepository
                .findByIsDefaultTrueOrCreatedByManager(currentManager)
                .stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    // ── 3. Manager updates active strategies for their hotel ──────────────
    @Override
    public List<PricingStrategyResponseDto> updateHotelStrategies(Long hotelId, HotelStrategyUpdateDto dto) {
        User currentManager = getCurrentUser();

        // Edge: hotel must exist
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: " + hotelId));

        if (!Boolean.TRUE.equals(hotel.getActive())) {
            throw new IllegalStateException(
                    "Hotel '" + hotel.getName() + "' is not activated yet. " +
                            "Please activate the hotel first to initialize inventory before applying pricing strategies."
            );
        }

        // Edge: only the owner of the hotel can update strategies
        if (!hotel.getOwner().getId().equals(currentManager.getId())) {
            throw new UnAuthorisedException("This user does not own the hotel with id: " + hotelId);
        }

        // Edge: strategy list must not be null or empty
        if (dto.getStrategyIds() == null || dto.getStrategyIds().isEmpty()) {
            throw new IllegalArgumentException("At least one strategy must be selected.");
        }

        // Edge: all strategy IDs must exist + belong to this manager or be default
        dto.getStrategyIds().forEach(strategyId -> {
            PricingStrategy strategy = pricingStrategyRepository.findById(strategyId)
                    .orElseThrow(() -> new ResourceNotFoundException("Strategy not found with id: " + strategyId));

            boolean isDefault = strategy.getIsDefault();
            boolean isOwnedByManager = strategy.getCreatedByManager() != null &&
                    strategy.getCreatedByManager().getId().equals(currentManager.getId());

            if (!isDefault && !isOwnedByManager) {
                throw new UnAuthorisedException("Strategy with id: " + strategyId + " does not belong to you.");
            }
        });

        // Deactivate all existing strategies for this hotel
        List<HotelPricingStrategy> existing = hotelPricingStrategyRepository.findByHotel(hotel);
        existing.forEach(s -> s.setIsActive(false));
        hotelPricingStrategyRepository.saveAll(existing);

        // Activate the newly selected ones
        List<HotelPricingStrategy> updated = dto.getStrategyIds().stream().map(strategyId -> {
            PricingStrategy strategy = pricingStrategyRepository.findById(strategyId).get();

            HotelPricingStrategy link = hotelPricingStrategyRepository
                    .findByHotelAndPricingStrategyId(hotel, strategyId)
                    .orElse(new HotelPricingStrategy(hotel, strategy));

            link.setIsActive(true);
            return hotelPricingStrategyRepository.save(link);
        }).collect(Collectors.toList());

        log.info("Manager ID: {} updated strategies for hotel ID: {} => {}",
                currentManager.getId(), hotelId, dto.getStrategyIds());

        return updated.stream()
                .map(h -> toResponseDto(h.getPricingStrategy()))
                .collect(Collectors.toList());
    }

    // ── 4. Get active strategies for a hotel ─────────────────────────────
    @Override
    public List<PricingStrategyResponseDto> getHotelStrategies(Long hotelId) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: " + hotelId));

        // ✅ NEW: Only return strategies for active hotels
        if (!Boolean.TRUE.equals(hotel.getActive())) {
            throw new IllegalStateException(
                    "Hotel '" + hotel.getName() + "' is not activated yet. Activate it first."
            );
        }

        return hotelPricingStrategyRepository.findByHotelAndIsActiveTrue(hotel)
                .stream()
                .map(h -> toResponseDto(h.getPricingStrategy()))
                .collect(Collectors.toList());
    }

    // ── 5. Add/update a holiday ───────────────────────────────────────────
    @Override
    public Holiday saveHoliday(HolidayRequestDto dto) {
        // Edge: date must not be null
        if (dto.getDate() == null) {
            throw new IllegalArgumentException("Holiday date must not be null.");
        }

        // Edge: name must not be blank
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new IllegalArgumentException("Holiday name must not be blank.");
        }

        Holiday holiday = holidayRepository.findByDate(dto.getDate())
                .orElse(new Holiday());
        holiday.setDate(dto.getDate());
        holiday.setName(dto.getName());
        holiday.setIsHoliday(dto.getIsHoliday() != null ? dto.getIsHoliday() : true);
        return holidayRepository.save(holiday);
    }

    // ── Helper ────────────────────────────────────────────────────────────
    private PricingStrategyResponseDto toResponseDto(PricingStrategy s) {
        PricingStrategyResponseDto dto = new PricingStrategyResponseDto();
        dto.setId(s.getId());
        dto.setStrategyName(s.getStrategyName());
        dto.setMultiplicationFactor(s.getMultiplicationFactor());
        dto.setIsDefault(s.getIsDefault());
        dto.setIsActive(s.getIsActive());
        return dto;
    }
}