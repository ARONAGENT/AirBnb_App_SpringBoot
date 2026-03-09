package com.majorproject.airbnbApp.dtos.Reviews;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class ReviewResponseDto implements Serializable {
    private Long id;
    private Long hotelId;
    private String hotelName;
    private Long userId;        // was guestId
    private String userName;    // was guestName
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
