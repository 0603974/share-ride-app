package rideshare.demo.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rideshare.demo.Entity.*;
import rideshare.demo.Entity.Request.PickupLocationRequest;
import rideshare.demo.Entity.Respons.geocode.GeoCodeResponse;
import rideshare.demo.Entity.Respons.geocode.Properties;
import rideshare.demo.Repository.JourneyRepository;
import rideshare.demo.Repository.LocationRepository;
import rideshare.demo.Repository.SharedRideRepository;
import rideshare.demo.configs.NoAvailableRideException;
import rideshare.demo.security.NotificationService;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SharedRideService {

    private final SharedRideRepository sharedRideRepository;
    private final UserService userService;
    private final CarService carService;
    private final JourneyRepository journeyRepository;
    private final NotificationService notificationService;
    private final RoadRouteService roadRouteService;
    private final LocationRepository locationRepository;


    public void saveSharedRide(SharedRide sharedRide, String email) {
        User currentUser = userService.findUser(email);
        Car car = carService.findCarByUserId(sharedRide.getDriverId());
        sharedRide.setDriverId(currentUser.getId());
        sharedRide.setStatus("pending");
        sharedRideRepository.save(sharedRide);
    }

    public void updateSharedRideAndMail(SharedRide sharedRide, String mailBody) {
        String mailSubject = "RideShare- Password Reset";
        sharedRide.setUpdatedAt(LocalDateTime.now());
        sharedRideRepository.save(sharedRide);
        List<Journey> activeJourney = journeyRepository.findByRideIdAndStatus(sharedRide.getId(), "active");
        List<Journey> acceptedJourney = journeyRepository.findByRideIdAndStatus(sharedRide.getId(), "accepted");
        List<Journey> linkedJourney = new ArrayList<>();
        linkedJourney.addAll(activeJourney);
        linkedJourney.addAll(acceptedJourney);

        if (!linkedJourney.isEmpty()) {
            for (Journey eachJourney : linkedJourney) {
                User user = userService.findById(eachJourney.getUserId());
//                try {
//                    notificationService.sendNotification(user.getEmailAddress(),mailSubject,mailBody);
//                } catch (MessagingException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
            }
        }
    }

    public void updateSharedRide(SharedRide sharedRide) {
        sharedRide.setUpdatedAt(LocalDateTime.now());
        sharedRideRepository.save(sharedRide);
    }

    public List<SharedRide> getAllPendingRides() {
        return sharedRideRepository.findByStatus("pending");
    }

    public SharedRide getOneRide(Long id) {
        return sharedRideRepository.getOneById(id);
    }

    public SharedRide getRideByDriverIdAndStatus(Long driverId, String status) {
        return sharedRideRepository.findByDriverIdAndStatus(driverId, status);
    }

    public SharedRide getRideByDriverIdAndStatusREST(Long driverId, String status) {
        return Optional
                .ofNullable(sharedRideRepository.findByDriverIdAndStatus(driverId, status))
                .orElseThrow(NoAvailableRideException::new);
    }

    public List<SharedRide> getAllInMotionRides() {
        return sharedRideRepository.findByStatus("in-motion");
    }

    public Long countRide() {
        return sharedRideRepository.count();
    }

    public Long countByStatus(String status) {
        return sharedRideRepository.countAllByStatus(status);
    }

    public List<SharedRide> getAllRides() {
        return sharedRideRepository.findAll();
    }

    //this method returns all saved pickup location for a shared ride
    public List<Location> getPickupLocation(Long rideId) {
        SharedRide foundRide = sharedRideRepository.findById(rideId).orElseThrow(() -> new NoAvailableRideException("Invalid ride"));
        return foundRide.getPickupLocations();
    }
    //this method is used to add a pickup location to a shared ride
    public Location addPickupLocation(Long rideId, PickupLocationRequest request) {

        SharedRide foundRide = sharedRideRepository.findById(rideId).orElseThrow(() -> new NoAvailableRideException("Invalid ride"));

        GeoCodeResponse foundRoute = roadRouteService.getLocationInfo(request.getCity());
        final Double latitude = foundRoute.getFeatures().get(0).getGeometry().getCoordinates().get(1);
        final Double longitude = foundRoute.getFeatures().get(0).getGeometry().getCoordinates().get(0);
        Properties properties = foundRoute.getFeatures().get(0).getProperties();

        Location pickupLocation = locationRepository.save(
                Location.builder()
                        .city(properties.getName())
                        .country(properties.getCountry())
                        .latitude(latitude)
                        .longitude(longitude)
                        .build()
        );

        foundRide.getPickupLocations().add(pickupLocation);
        sharedRideRepository.save(foundRide);

        return pickupLocation;
    }

    //This method performs the ride matching using the matching algorithm
    public List<SharedRide> matchUserWithRide(SharedRide request) {

        List<SharedRide> availableRides = sharedRideRepository.findByStatus("pending");

        return availableRides.stream()
                .filter(ride -> calculateDistance(request, ride) && calculateTime(request, ride))
                .collect(Collectors.toList());
    }

    //this method is used to determine the  eligibility of a ride using its time when performing the matching algorithm
    private boolean calculateTime(SharedRide request, SharedRide ride) {
        int userTime = convertDateTimeStringToSeconds(request.getTime());
        int rideTime = convertTimeStringToSecondsForDB(ride.getTime());
        int addedTimeForAvailability = 15;
        int driversAvailableTime = rideTime + addedTimeForAvailability;

        return userTime >= rideTime && userTime <= driversAvailableTime;
    }

    //this method is used to convert a saved ride time to seconds
    private int convertTimeStringToSecondsForDB(String time){
        return LocalTime.parse(time).getSecond();
    }

    //this method coverts a time string to its equivalents in seconds
    private int convertDateTimeStringToSeconds(String time){
        return LocalTime.parse(time).getSecond();
    }

    //this method is used to calculate the distance between the driver and user when performing the ride matching.
    private boolean calculateDistance(SharedRide request, SharedRide ride) {
        double longitude = Math.abs(request.getLongitude() - ride.getLongitude());
        double latitude = Math.abs(request.getLatitude() - ride.getLatitude());

        double squareOfLatitude = Math.pow(longitude, 2);
        double squareOfLongitude = Math.pow(latitude, 2);

        return squareOfLatitude + squareOfLongitude <= 15;

    }
}
