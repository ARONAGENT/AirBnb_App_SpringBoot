package com.majorproject.airbnbApp.services;

import com.majorproject.airbnbApp.dtos.Reviews.HotelReviewSummaryDto;
import com.majorproject.airbnbApp.dtos.Reviews.ReviewRequestDto;
import com.majorproject.airbnbApp.dtos.Reviews.ReviewResponseDto;

import java.util.List;

public interface ReviewService {
    ReviewResponseDto addReview(Long hotelId, ReviewRequestDto dto);
    ReviewResponseDto updateReview(Long reviewId, ReviewRequestDto dto);
    void deleteReview(Long reviewId);
    List<ReviewResponseDto> getReviewsForHotel(Long hotelId);
    HotelReviewSummaryDto getHotelReviewSummary(Long hotelId);

}
