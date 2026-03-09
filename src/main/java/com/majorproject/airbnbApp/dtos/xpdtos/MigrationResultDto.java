package com.majorproject.airbnbApp.dtos.xpdtos;
import lombok.*;


@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MigrationResultDto {
    private int totalBookingsProcessed;
    private int skipped;
    private String message;
}
