package rideshare.demo.Service;
import rideshare.demo.Entity.Location;
import rideshare.demo.Entity.Request.PickupLocationRequest;

import java.util.List;

public interface PickupLocationService {
    Location addPickupLocation( Long rideId,  PickupLocationRequest request);
    List<Location> getPickupLocation( Long rideId);
}
