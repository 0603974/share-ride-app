package rideshare.demo.Service;

import rideshare.demo.Entity.Respons.geocode.GeoCodeResponse;

public interface RoadRouteService {
     GeoCodeResponse getLocationInfo(String searchQuery);
}
