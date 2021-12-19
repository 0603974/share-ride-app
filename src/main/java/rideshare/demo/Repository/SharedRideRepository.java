package rideshare.demo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rideshare.demo.Entity.Journey;
import rideshare.demo.Entity.SharedRide;
import rideshare.demo.Entity.User;

import java.util.List;

@Repository
public interface SharedRideRepository extends JpaRepository<SharedRide,Long> {

    Long countAllByStatus(String status);
    SharedRide getOneById(Long id);
    List<SharedRide>findByStatus(String status);
    SharedRide findByDriverIdAndStatus(Long id, String status);
    List<SharedRide>findByOriginAndStatus(String origin, String status);
    List<SharedRide>findByDestinationAndStatus(String destination, String status);
    List<SharedRide>findByDateAndStatus(String date,String status);
    List<SharedRide>findByTimeAndStatus(String time, String status);
    List<SharedRide>findByOriginAndDestinationAndStatus(String origin,String destination, String status);
    List<SharedRide>findByOriginAndDateAndStatus(String origin, String date, String status);
    List<SharedRide>findByOriginAndTimeAndStatus(String origin, String time, String status);
    List<SharedRide>findByDestinationAndDateAndStatus(String destination, String date, String status);
    List<SharedRide>findByDestinationAndTimeAndStatus(String destination, String time, String status);
    List<SharedRide>findByDateAndTimeAndStatus(String date, String time, String status);
    List<SharedRide>findByOriginAndDestinationAndDateAndStatus(String origin, String destination, String date, String status);
    List<SharedRide>findByOriginAndDestinationAndTimeAndStatus(String origin, String destination, String time, String status);
    List<SharedRide>findByOriginAndDateAndTimeAndStatus(String origin, String date, String time, String status);
    List<SharedRide>findByDestinationAndDateAndTimeAndStatus(String destination, String date, String time, String status);
    List<SharedRide>findByOriginAndDestinationAndDateAndTimeAndStatus(String origin,String destination, String date, String time, String status);




}
