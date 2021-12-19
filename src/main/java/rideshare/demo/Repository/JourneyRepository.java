package rideshare.demo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rideshare.demo.Entity.Journey;

import java.util.List;

public interface JourneyRepository extends JpaRepository<Journey,Long> {

    List<Journey> findByUserIdAndStatus(Long id, String status);
    List<Journey> findByRideIdAndStatus(Long id, String status);
    List<Journey>findByStatus(String status);
    Long countByRideIdAndStatusAndProgress(Long id, String status, String progress);
}
