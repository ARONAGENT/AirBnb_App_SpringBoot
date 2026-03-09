package com.majorproject.airbnbApp.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(
        name = "hotel_pricing_strategy",
        uniqueConstraints = @UniqueConstraint(columnNames = {"hotel_id", "pricing_strategy_id"})
)
public class HotelPricingStrategy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pricing_strategy_id", nullable = false)
    private PricingStrategy pricingStrategy;

    @Column(nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public HotelPricingStrategy(Hotel hotel, PricingStrategy pricingStrategy) {
        this.hotel = hotel;
        this.pricingStrategy = pricingStrategy;
        this.isActive = true;
    }
}