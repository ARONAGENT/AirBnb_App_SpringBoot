package com.majorproject.airbnbApp.controllers;

import com.majorproject.airbnbApp.dtos.Reviews.HotelReviewSummaryDto;
import com.majorproject.airbnbApp.dtos.Reviews.ReviewRequestDto;
import com.majorproject.airbnbApp.dtos.Reviews.ReviewResponseDto;
import com.majorproject.airbnbApp.services.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Tag(name = "Hotel reviews", description = "APIs for Hotel reviews")
@RestController
@RequestMapping("/hotels")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping("/{hotelId}/reviews")
    @Operation(summary = "Add Review", description = "Add a Review for a Hotel")
    public ResponseEntity<ReviewResponseDto> addReview(
            @PathVariable Long hotelId,
            @RequestBody @Valid ReviewRequestDto dto) {

        ReviewResponseDto response = reviewService.addReview(hotelId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{hotelId}/reviews")
    @Operation(summary = "Get Reviews", description = "Get All the reviews By hotelId ")
    public ResponseEntity<List<ReviewResponseDto>> getReviewsForHotel(
            @PathVariable Long hotelId) {

        return ResponseEntity.ok(reviewService.getReviewsForHotel(hotelId));
    }

    @GetMapping("/{hotelId}/reviews/summary")
    @Operation(summary = "Get Hotel Summary", description = "Gives Average Rating of hotel and Summary of reviews")
    public ResponseEntity<HotelReviewSummaryDto> getHotelReviewSummary(
            @PathVariable Long hotelId) {

        return ResponseEntity.ok(reviewService.getHotelReviewSummary(hotelId));
    }

    @PutMapping("/reviews/{reviewId}")
    @Operation(summary = "Update", description = "Update the review")
    public ResponseEntity<ReviewResponseDto> updateReview(
            @PathVariable Long reviewId,
            @RequestBody @Valid ReviewRequestDto dto) {

        return ResponseEntity.ok(reviewService.updateReview(reviewId, dto));
    }

    @DeleteMapping("/reviews/{reviewId}")
    @Operation(summary = "Delete review", description = "Delete the Review {hotel Manager only}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }
}
