package com.majorproject.airbnbApp.dtos.xpdtos;

import lombok.*;

import java.io.Serializable;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PlaceVisitedDto implements Serializable {
    private Long id;
    private String placeName;
    private String hotelName;
    private String visitedAt;
}
