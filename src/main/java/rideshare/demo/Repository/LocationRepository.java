package rideshare.demo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rideshare.demo.Entity.Location;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
}
