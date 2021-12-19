package rideshare.demo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rideshare.demo.Entity.RoadRoute;

import java.util.Optional;

@Repository
public interface RoadRouteRepository extends JpaRepository<RoadRoute, Long> {
    Optional<RoadRoute> findByCityAndCountry(String city, String country);
}
