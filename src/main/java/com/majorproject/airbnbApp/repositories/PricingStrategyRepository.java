package com.majorproject.airbnbApp.repositories;

import com.majorproject.airbnbApp.entities.PricingStrategy;
import com.majorproject.airbnbApp.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PricingStrategyRepository extends JpaRepository<PricingStrategy,Long> {
    // All default system strategies
    List<PricingStrategy> findByIsDefaultTrue();

    // All custom strategies created by a specific manager
    List<PricingStrategy> findByCreatedByManagerAndIsDefaultFalse(User manager);

    // Check if a default strategy with this name already exists (for seeding)
    Optional<PricingStrategy> findByStrategyNameAndIsDefaultTrue(String strategyName);

    // All strategies available to a manager (defaults + their own customs)
    // Used in GET /admin/strategies
    List<PricingStrategy> findByIsDefaultTrueOrCreatedByManager(User manager);
}
