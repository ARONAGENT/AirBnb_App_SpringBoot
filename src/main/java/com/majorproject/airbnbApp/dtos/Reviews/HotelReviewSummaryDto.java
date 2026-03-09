package com.majorproject.airbnbApp.dtos.Reviews;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class HotelReviewSummaryDto implements Serializable {
    private Long hotelId;
    private String hotelName;
    private Double averageRating;       // e.g. 4.3
    private Long totalReviews;
    private List<ReviewResponseDto> reviews;
}
