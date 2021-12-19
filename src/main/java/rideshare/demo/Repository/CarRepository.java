package rideshare.demo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rideshare.demo.Entity.Car;

@Repository
public interface CarRepository extends JpaRepository<Car,Long> {
     Car findByUserId(Long id);

}
