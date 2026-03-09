package com.majorproject.airbnbApp.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "pricing_strategy")
public class PricingStrategy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String strategyName; // e.g. "SURGE", "OCCUPANCY", "MY_WEEKEND_BOOST"

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal multiplicationFactor; // e.g. 1.25 means 25% increase

    @Column(nullable = false)
    private Boolean isDefault; // true = system default, false = custom by manager

    // null for default strategies, set for custom ones
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_manager_id")
    private User createdByManager;

    @Column(nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Constructor for seeding default strategies
    public PricingStrategy(String strategyName, BigDecimal multiplicationFactor) {
        this.strategyName = strategyName;
        this.multiplicationFactor = multiplicationFactor;
        this.isDefault = true;
        this.isActive = true;
    }
}