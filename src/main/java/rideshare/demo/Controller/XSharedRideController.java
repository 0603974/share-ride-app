package rideshare.demo.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import rideshare.demo.Entity.*;
import rideshare.demo.Entity.Request.PickupLocationRequest;
import rideshare.demo.Service.SharedRideService;
import rideshare.demo.Service.UserService;
import rideshare.demo.configs.RESTResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/x-ride")
public class XSharedRideController {
    private final UserService userService;
    private final SharedRideService sharedRideService;


    @PostMapping("/pickup-location")
    public ResponseEntity<RESTResponse<Location>> addPickupLocation( @RequestBody PickupLocationRequest request, Authentication authentication){

        User user = userService.findUserByUsername(authentication.getName());
        SharedRide sharedRide = sharedRideService.getRideByDriverIdAndStatusREST(user.getId(),"pending");

        Location service =  sharedRideService.addPickupLocation(sharedRide.getId(), request);

        return ResponseEntity.ok(
                RESTResponse.<Location>builder()
                        .data(service)
                        .message("Pickup location successfully added!")
                        .success(true)
                        .build()
        );
    }

    @GetMapping("/pickup-location")
    public ResponseEntity<RESTResponse<List<Location>>> getPickupLocation( Authentication authentication){
        User user = userService.findUserByUsername(authentication.getName());
        SharedRide sharedRide = sharedRideService.getRideByDriverIdAndStatusREST(user.getId(),"pending");
        List<Location> service =  sharedRideService.getPickupLocation(sharedRide.getId());
        return ResponseEntity.ok(
                RESTResponse.<List<Location>>builder()
                        .data(service)
                        .message("Pickup location successfully retrived!")
                        .success(true)
                        .build()
        );
    }

}
