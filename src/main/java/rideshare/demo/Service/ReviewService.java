package rideshare.demo.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rideshare.demo.Entity.Review;
import rideshare.demo.Repository.ReviewRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReviewService {
    @Autowired
    ReviewRepository reviewRepository;

    public List<Review>findReviewByDriverId(Long driverId){
        return reviewRepository.findByDriverId(driverId);
    }

    public void saveReview(Review review){
        review.setCreatedAt(LocalDateTime.now());
        review.setUpdatedAt(LocalDateTime.now());
        reviewRepository.save(review);
    }

    public List<Review>getAllReviews(){
        return reviewRepository.findAll();
    }

}
