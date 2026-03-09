package com.majorproject.airbnbApp.services.impl;

import com.majorproject.airbnbApp.dtos.Reviews.HotelReviewSummaryDto;
import com.majorproject.airbnbApp.dtos.Reviews.ReviewRequestDto;
import com.majorproject.airbnbApp.dtos.Reviews.ReviewResponseDto;
import com.majorproject.airbnbApp.entities.Hotel;
import com.majorproject.airbnbApp.entities.Review;
import com.majorproject.airbnbApp.entities.User;
import com.majorproject.airbnbApp.repositories.HotelRepository;
import com.majorproject.airbnbApp.repositories.ReviewRepository;
import com.majorproject.airbnbApp.services.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.majorproject.airbnbApp.utils.AppUtils.getCurrentUser;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final HotelRepository hotelRepository;

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "hotelReviews", key = "#hotelId"),          // invalidate list cache
            @CacheEvict(value = "hotelReviewSummary", key = "#hotelId")     // invalidate summary cache
    })
    public ReviewResponseDto addReview(Long hotelId, ReviewRequestDto dto) {
        log.info("Adding review for hotelId: {}, cache evicted", hotelId);
        User currentUser = getCurrentUser();

        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new RuntimeException("Hotel not found with id: " + hotelId));

        Review review = new Review();
        review.setHotel(hotel);
        review.setUser(currentUser);
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());

        return mapToDto(reviewRepository.save(review));
    }

    @Override
    @Transactional
    @Caching(
            put = {
                    @CachePut(value = "review", key = "#reviewId")              // update single review cache
            },
            evict = {
                    @CacheEvict(value = "hotelReviews", allEntries = true),     // invalidate all hotel review lists
                    @CacheEvict(value = "hotelReviewSummary", allEntries = true) // invalidate all summaries (avg changes)
            }
    )
    public ReviewResponseDto updateReview(Long reviewId, ReviewRequestDto dto) {
        log.info("Updating reviewId: {}, cache updated", reviewId);
        User currentUser = getCurrentUser();

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found with id: " + reviewId));

        if (!review.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You are not authorized to update this review.");
        }

        review.setRating(dto.getRating());
        review.setComment(dto.getComment());

        return mapToDto(reviewRepository.save(review));
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "review", key = "#reviewId"),               // remove single review from cache
            @CacheEvict(value = "hotelReviews", allEntries = true),         // invalidate all hotel review lists
            @CacheEvict(value = "hotelReviewSummary", allEntries = true)    // invalidate all summaries (avg changes)
    })
    public void deleteReview(Long reviewId) {
        log.info("Deleting reviewId: {}, cache evicted", reviewId);
        User currentUser = getCurrentUser();

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found with id: " + reviewId));

        boolean isAuthor = review.getUser().getId().equals(currentUser.getId());
        boolean isHotelOwner = review.getHotel().getOwner().getId().equals(currentUser.getId());

        if (!isAuthor && !isHotelOwner) {
            throw new RuntimeException("You are not authorized to delete this review.");
        }

        reviewRepository.delete(review);
    }

    @Override
    @Cacheable(value = "hotelReviews", key = "#hotelId")               // cache list per hotelId
    public List<ReviewResponseDto> getReviewsForHotel(Long hotelId) {
        log.info("Fetching reviews from DB for hotelId: {} (cache miss)", hotelId);
        hotelRepository.findById(hotelId)
                .orElseThrow(() -> new RuntimeException("Hotel not found with id: " + hotelId));

        return reviewRepository.findByHotelId(hotelId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "hotelReviewSummary", key = "#hotelId")         // cache summary per hotelId
    public HotelReviewSummaryDto getHotelReviewSummary(Long hotelId) {
        log.info("Fetching review summary from DB for hotelId: {} (cache miss)", hotelId);
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new RuntimeException("Hotel not found with id: " + hotelId));

        Double avg = reviewRepository.findAverageRatingByHotelId(hotelId);
        Long count = reviewRepository.countByHotelId(hotelId);
        List<ReviewResponseDto> reviews = getReviewsForHotel(hotelId);

        HotelReviewSummaryDto summary = new HotelReviewSummaryDto();
        summary.setHotelId(hotel.getId());
        summary.setHotelName(hotel.getName());
        summary.setAverageRating(avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0);
        summary.setTotalReviews(count);
        summary.setReviews(reviews);

        return summary;
    }

    private ReviewResponseDto mapToDto(Review review) {
        ReviewResponseDto dto = new ReviewResponseDto();
        dto.setId(review.getId());
        dto.setHotelId(review.getHotel().getId());
        dto.setHotelName(review.getHotel().getName());
        dto.setUserId(review.getUser().getId());
        dto.setUserName(review.getUser().getName());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setCreatedAt(review.getCreatedAt());
        dto.setUpdatedAt(review.getUpdatedAt());
        return dto;
    }
}