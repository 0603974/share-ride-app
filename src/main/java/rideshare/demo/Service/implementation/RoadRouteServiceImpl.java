package rideshare.demo.Service.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import rideshare.demo.Entity.Request.RoadRouteRequest;
import rideshare.demo.Entity.Respons.geocode.Features;
import rideshare.demo.Entity.Respons.geocode.GeoCodeResponse;
import rideshare.demo.Entity.Respons.geocode.Properties;
import rideshare.demo.Entity.RoadRoute;
import rideshare.demo.Repository.RoadRouteRepository;
import rideshare.demo.Service.RoadRouteService;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * This is a service class that contains the logic for making api calls to the geocode api
 * the geocode api returns coordinates and other details for a query location
 * @ref https://geocodeapi.io
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RoadRouteServiceImpl implements RoadRouteService {
    private final RestTemplate restTemplate;
    private final RoadRouteRepository roadRouteRepository;
    //Geoocode api-key from the application.properties file
    @Value("${geocode_api_key}")
    private String geocodeApiKey;

    @Override
    public GeoCodeResponse getLocationInfo(String searchQuery) {
        GeoCodeResponse response = null;
        try {
            response = restTemplate.getForObject(getUrlFrom(searchQuery), GeoCodeResponse.class);
            System.out.println(response);
        } catch (Exception e) {
            log.error("rest template error = > ", e);
        }
        return response;
    }

    //this methods generates the complete url for the geocode api by concatenating the location to search and the api key
    private String getUrlFrom(String searchQuery) {
        String url = "";
        try {
            url = new URL(
                    String.format("https://app.geocodeapi.io/api/v1/search?text=%s&apikey=%s", searchQuery, geocodeApiKey)
            ).toString();
        } catch (MalformedURLException exception) {
            log.error("geolocation url creation error => ", exception);
        }

        return url;
    }


    private RoadRoute buildRoute(GeoCodeResponse apiResponse) {
        Features features = apiResponse.getFeatures().get(1);
        List<Double> coordinates = features.getGeometry().getCoordinates();
        Properties properties = features.getProperties();
        Double latitude = coordinates.get(1);
        Double longitude = coordinates.get(0);

        String city = properties.getName();
        String country = properties.getCountry();

        return RoadRoute.builder()
                .city(city)
                .country(country)
                .latitude(latitude)
                .longitude(longitude)
                .build();
    }
}
