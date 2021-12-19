package rideshare.demo.Controller;

import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
//import com.cloudinary.utils.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import rideshare.demo.Entity.*;
import rideshare.demo.Entity.Request.DriverProfileRequest;
import rideshare.demo.Repository.JourneyRepository;
import rideshare.demo.Service.*;
import rideshare.demo.security.NotificationService;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
public class DriverController {

    @Autowired
    CloudinaryConfig cloudc;
    @Autowired
    UserService service;
    @Autowired
    CarService carService;
    @Autowired
    SharedRideService sharedRideService;
    @Autowired
    JourneyService journeyService;
    @Autowired
    JourneyRepository journeyRepository;
    @Autowired
    ReviewService reviewService;
    @Autowired
    NotificationService notificationService;
    @Autowired
    private HttpServletRequest request;


    SimpleDateFormat formatter = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");

    /**
     * This controller routed to the driver registration page
     * @return driver register page
     */
    @GetMapping("/driver-register")
    private String getDriverRegistrationPage(){
        return "driver-register";
    }

    /**
     * This controller collects the driver details from the HTML form and stores it and return the verification page on success and register page on invalid registration
     * @param driver
     * @param model
     * @return
     */
    @PostMapping("driver-registration")
    private String registerDriver(Driver driver, Car car, User user, Model model, @RequestParam("imageFile") MultipartFile file) throws IOException {
        /**
         * The program collects all the Html input into the driver object and allocates it respectively to the objects to save. i.e. assign value to the new car and the new driver that has been initialised.
         */
        user.setFirstName(driver.getFirstName());
        user.setLastName(driver.getLastName());
        user.setEmailAddress(driver.getEmailAddress());
        user.setUsername(driver.getUsername());
        user.setPhoneNumber(driver.getPhoneNumber());
        user.setPassword(driver.getPassword());
        user.setRole("DRIVER");
        boolean status = service.saveUser(user);
        System.out.println("heyyyyy"+file.getOriginalFilename());

        /**
         * This portions verifies that the user account saved successfuly before saving the car else the car would be saved and not have a driver
         */
        if(status==true) {
            try{
                Map uploadResult = cloudc.upload(file.getBytes(),
                        ObjectUtils.asMap("resourcetype", "auto"));
                car.setCarImgUrl(uploadResult.get("url").toString());
            }
            catch (IOException e) {
            e.printStackTrace();
            return "redirect:/register";
            }
            car.setUserId(user.getId());
            car.setMake(driver.getMake());
            car.setModel(driver.getModel());
            car.setCategory(driver.getCategory());
            car.setCategory(driver.getCategory());
            car.setColor(driver.getColor());
            car.setPlateNumber(driver.getPlateNumber());
            car.setNoSeat(driver.getNoSeat());
            System.out.println("heyyyyy"+car.getCarImgUrl());

            carService.saveCar(car);
            /**
             * Model.addAttribute is used to pass parameter to the page, here we passing a true status because the registration was successful
             */
            model.addAttribute("status","true");
            return "driver-register";
        }else {
            model.addAttribute("status", "false");
        }
        return "driver-register";
    }


    /**
     *
     * @param authentication
     * @param model
     *
     * This controller routes to the driver profile page
     */
    @GetMapping("/driver-profile")
    private String getDriverProfilePage(Authentication authentication, Model model){
        User existingUser = service.findUserByUsername(authentication.getName());
        Car userCar = carService.findCarByUserId(existingUser.getId());
        model.addAttribute("user",existingUser);
        model.addAttribute("car", userCar);
        return "driver-profile";
    }

    /**
     *
     * @param authentication
     * @param model
     *
     * This controller routes to the update driver profile page
     */
    @PostMapping("/update-driver-profile")
    private String updateDriverProfile(DriverProfileRequest driverProfileRequest, Authentication authentication, Model model){
        User existingUser = service.findUserByUsername(authentication.getName());
        Car userCar = carService.findCarByUserId(existingUser.getId());
        boolean status = service.editDriverProfile(driverProfileRequest,authentication.getName());
        model.addAttribute("user",existingUser);
        model.addAttribute("car", userCar);
        String success = "";
        if(status==false){
            success="false";
        }
            success="true";

        model.addAttribute("success",success);
        return "driver-profile";
    }

    /**
     *
     * @param authentication
     * @param model
     *
     * This controller routes to the driver ride page
     */
    @GetMapping("/driver-ride")
    private String getDriverRidePage(Authentication authentication, Model model){
        User user = service.findUserByUsername(authentication.getName());
        SharedRide sharedRide = sharedRideService.getRideByDriverIdAndStatus(user.getId(),"pending");
        SharedRide sharedRide1 = sharedRideService.getRideByDriverIdAndStatus(user.getId(),"in-motion");
        Car car = carService.findCarByUserId(user.getId());
        model.addAttribute("car", car);
        List<Review> review = reviewService.findReviewByDriverId(user.getId());

        if(sharedRide1!=null){
            return "redirect:/ride-initiated";
        }

        if(sharedRide!=null) {

            List<Journey> rideJourney = journeyService.getJourneyByRideIdAndStatus(sharedRide.getId());
            model.addAttribute("driver", user);
            model.addAttribute("ride", sharedRide);

            System.out.println(user + "" + car + "" + sharedRide);
            if (rideJourney != null) {
                model.addAttribute("rideJourney", rideJourney);
            } else {
                model.addAttribute("journey", "No Passengers");
            }
            return "driver-ride";


        }else{
            model.addAttribute("car",car);
            model.addAttribute("review",review);
            model.addAttribute("username",user.getUsername());
            model.addAttribute("status","no-ride");
            return "driver";
        }
    }

    /**
     *
     * @param authentication
     * this controller routes to the user edit ride page
     */
    @PostMapping("/edit-ride")
    private String editRide(Authentication authentication,SharedRide sharedRide){
        String mail = "your ride driver has updated ride details and would like you to check and confirm if you are okay with it. if not feel free to cancel the ride. visit  http://localhost:8080/current-ride";
        User user = service.findUserByUsername(authentication.getName());
        SharedRide sharedRide1 = sharedRideService.getRideByDriverIdAndStatus(user.getId(),"pending");
        if(sharedRide1==null){
            //
        }else {
            sharedRide1.setDestination(sharedRide.getDestination());
            sharedRide1.setOrigin(sharedRide.getOrigin());
            sharedRide1.setDate(sharedRide.getDate());
            sharedRide1.setTime(sharedRide.getTime());
            sharedRide1.setMeetUp(sharedRide.getMeetUp());
            sharedRideService.updateSharedRideAndMail(sharedRide1,mail);
        }
        return "redirect:/edit-success";
    }

    /**
     *
     * @param authentication
     * @param model
     *
     * This controller routes to the edit success page
     */
    @GetMapping("/edit-success")
    private String editSuccessPage(Authentication authentication, Model model){
        User user = service.findUserByUsername(authentication.getName());

        SharedRide sharedRide1 = sharedRideService.getRideByDriverIdAndStatus(user.getId(),"in-motion");
        SharedRide sharedRide = sharedRideService.getRideByDriverIdAndStatus(user.getId(),"pending");

        Car car = carService.findCarByUserId(user.getId());
        model.addAttribute("car", car);
        List<Review> review = reviewService.findReviewByDriverId(user.getId());

        if(sharedRide1!=null){
            return "redirect:/ride-initiated";
        }

        if(sharedRide!=null) {
            List<Journey> rideJourney = journeyService.getJourneyByRideIdAndStatus(sharedRide.getId());
            model.addAttribute("driver", user);
            model.addAttribute("ride", sharedRide);
            model.addAttribute("status","rideEdited");
            if (rideJourney != null) {
                model.addAttribute("rideJourney", rideJourney);
            } else {
                model.addAttribute("journey", "No Passengers");
            }
            return "driver-ride";
        }else{
            model.addAttribute("review",review);
            model.addAttribute("car",car);
            model.addAttribute("username",user.getUsername());

            return "driver";
        }
    }

    /**
     *
     * @param authentication
     * @param model
     *
     * This controller routes to the start ride page
     */
    @GetMapping("/start-ride")
    private String startRide(Authentication authentication,Model model){
        User user = service.findUserByUsername(authentication.getName());
        SharedRide sharedRide = sharedRideService.getRideByDriverIdAndStatus(user.getId(),"pending");
        Car car = carService.findCarByUserId(user.getId());
        List<Journey>acceptedJourney = journeyRepository.findByRideIdAndStatus(sharedRide.getId(), "accepted");
        List<Journey>activeJourney = journeyRepository.findByRideIdAndStatus(sharedRide.getId(),"active");
        List<Journey> rideJourney = journeyService.getJourneyByRideIdAndStatus(sharedRide.getId());
        String status="";
            if(sharedRide!=null) {
                if (activeJourney.isEmpty() && acceptedJourney.isEmpty()) {
                    status="no-journey";
                } else if (!activeJourney.isEmpty()&& acceptedJourney.isEmpty()) {
                    status="no-accepted";
                } else if (!activeJourney.isEmpty() && !acceptedJourney.isEmpty()) {
                    System.out.println("here:" +activeJourney+"huu"+acceptedJourney);
                    status="no-accepted";
                } else if (activeJourney.isEmpty() && !acceptedJourney.isEmpty()) {
                    sharedRide.setStatus("in-motion");
                    sharedRideService.updateSharedRide(sharedRide);
                    for (Journey eachJourney : acceptedJourney) {
                        eachJourney.setProgress("in-motion");
                        eachJourney.setStartTime(formatter.format(new Date()));
                        journeyService.saveRide(eachJourney);
                    }
                    return "redirect:/ride-initiated";
                }
            }else {
                return "redirect:/driver";
            }
        if(!rideJourney.isEmpty()) {
            model.addAttribute("rideJourney", rideJourney);
        }else{
            model.addAttribute("journey","No Passengers");
        }
        model.addAttribute("status", status);
        model.addAttribute("driver", user);
        model.addAttribute("car",car);
        model.addAttribute("ride",sharedRide);
        return "driver-ride";
    }

    /**
     * @param authentication
     * @param model
     * This controller routes to the initiated ride page
     */
    @GetMapping("/ride-initiated")
    public String rideStartedSuccess(Authentication authentication,Model model){
        User user = service.findUserByUsername(authentication.getName());
        SharedRide sharedRide = sharedRideService.getRideByDriverIdAndStatus(user.getId(),"in-motion");
        Car car = carService.findCarByUserId(user.getId());
        List<Journey> rideJourney = journeyService.getJourneyByRideIdAndStatus(sharedRide.getId());
        model.addAttribute("driver", user);
        model.addAttribute("car",car);
        model.addAttribute("ride",sharedRide);
        model.addAttribute("status","success");
        if(rideJourney!=null) {
            model.addAttribute("rideJourney", rideJourney);
        }else{
            model.addAttribute("journey","No Passengers");
        }
        return "ride-initiated";
    }

    /**
     * @param authentication
     * @param model
     * This controller routes to the journey details page
     */
    @GetMapping("/journey-details/{id}")
    public String passengerDetails(@PathVariable Long id,Model model,Authentication authentication){
        Journey currentJourney = journeyService.getById(id);
        if(currentJourney==null){
//
        }
        User user = service.findUserByUsername(authentication.getName());
        SharedRide sharedRide = sharedRideService.getRideByDriverIdAndStatus(user.getId(),"pending");
//        SharedRide sharedRide1 = sharedRideService.getRideByDriverIdAndStatus(user.getId(),"in-motion");
//
//        if(sharedRide1!=null){
//            return "redirect:/ride-initiated";
//        }

        Car car = carService.findCarByUserId(user.getId());
        List<Journey> rideJourney = journeyService.getJourneyByRideIdAndStatus(sharedRide.getId());
        model.addAttribute("driver", user);
        model.addAttribute("car",car);
        model.addAttribute("ride",sharedRide);
        model.addAttribute("currentJourney",currentJourney);
        model.addAttribute("currentJourneyStatus","true");

        if(rideJourney!=null) {
            model.addAttribute("rideJourney", rideJourney);
            model.addAttribute("rideJourney", rideJourney);
        }else{
            model.addAttribute("journey","No Passengers");
        }
        return "journey-details";
    }

    /**
     * @param authentication
     * This controller routes to the accepted journey page
     */
    @GetMapping("/accept-journey/{id}")
    public String acceptJourney(@PathVariable Long id ,Authentication authentication) {
        Journey currentJourney = journeyService.getById(id);
            currentJourney.setStatus("accepted");
            currentJourney.setUpdatedAt(LocalDateTime.now());
            journeyService.saveRide(currentJourney);
            User user = service.findById(currentJourney.getUserId());
            String body = "Your request to join a ride has been accepted by the driver kindly visit your dashboard to see progress http://localhost:8080/current-ride";
//        try {
//            notificationService.sendNotification(user.getEmailAddress(),"RideShare - Ride Status",body);
//        } catch (MessagingException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return "redirect:/driver-ride";
    }

    /**
     * This controller routes to the reject journey page
     */
    @GetMapping("/reject-journey/{id}")
    public String rejectJourney(@PathVariable Long id){
        String mail = "Your request to join a ride has been declined by the driver kindly visit your dashboard to join another ride http://localhost:8080/shared-ride";
        Journey currentJourney = journeyService.getById(id);
        SharedRide sharedRide = sharedRideService.getOneRide(currentJourney.getRideId());
        User user = service.findById(currentJourney.getUserId());
        sharedRide.setAvailableSeat(sharedRide.getAvailableSeat()+1);
        sharedRide.setUpdatedAt(LocalDateTime.now());
        currentJourney.setStatus("rejected");
        sharedRideService.updateSharedRide(sharedRide);
        journeyService.saveRide(currentJourney);

//        try {
//            notificationService.sendNotification(user.getEmailAddress(),"RideShare - Ride Status",mail);
//        } catch (MessagingException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return "redirect:/driver-ride";
    }

    /**
     * @param authentication
     * This controller routes to the cancel shared-ride page
     */
    @GetMapping("/cancel-sharing")
    public String cancelJourney(Authentication authentication){
        String mail = "Your ride has been cancelled by the ride driver kindly go to dashboard to join another ride. http://localhost:8080/shared-ride";
        User currentUser =  service.findUserByUsername(authentication.getName());
        SharedRide sharedRide = sharedRideService.getRideByDriverIdAndStatus(currentUser.getId(),"pending");
        List<Journey> linkedJourney = journeyService.getJourneyByRideIdAndStatus(sharedRide.getId());
        for (Journey eachJourney : linkedJourney) {
            eachJourney.setStatus("cancelled");
            eachJourney.setUpdatedAt(LocalDateTime.now());
            journeyService.saveRide(eachJourney);
        }
        sharedRide.setStatus("cancelled");
        sharedRideService.updateSharedRideAndMail(sharedRide,mail);
        return "redirect:/driver";
    }

    /**
     *
     * @param authentication
     * @param model
     *
     * This controller routes to the driver page
     */
    @GetMapping("/driver")
    public String getDriverIndexPage(Authentication authentication, Model model){
        User user = service.findUserByUsername(authentication.getName());
        Car car = carService.findCarByUserId(user.getId());
        List<Review> review = reviewService.findReviewByDriverId(user.getId());
        model.addAttribute("car",car);
        model.addAttribute("review",review);
        model.addAttribute("username",user.getUsername());
        return "driver";
    }

    /**
     * @param authentication
     * @param model
     * This controller routes to the share ride page
     */
    @PostMapping("/share-ride")
    public String shareRide(Authentication authentication,SharedRide sharedRide,Model model){
        System.out.println(sharedRide);
        User currentUser = service.findUserByUsername(authentication.getName());
        Car car = carService.findCarByUserId(currentUser.getId());
        List<Review> review = reviewService.findReviewByDriverId(currentUser.getId());
        SharedRide pendingRide = sharedRideService.getRideByDriverIdAndStatus(currentUser.getId(),"pending");
        SharedRide activeRide = sharedRideService.getRideByDriverIdAndStatus(currentUser.getId(),"active");
        SharedRide inMotionRide = sharedRideService.getRideByDriverIdAndStatus(currentUser.getId(),"in-motion");
        model.addAttribute("username",currentUser.getUsername());

        if(pendingRide==null && activeRide==null && inMotionRide==null){
            sharedRide.setDriverId(currentUser.getId());
            sharedRideService.saveSharedRide(sharedRide,currentUser.getEmailAddress());
            model.addAttribute("status","true");
        }else {
            model.addAttribute("status","false");
        }
        model.addAttribute("car", car);
        model.addAttribute("review", review);
        return "driver";
    }

    /**
     * @param authentication
     * @param model
     * This controller routes to the end journey ride page
     */
    @GetMapping("/end-journey/{id}")
    public String endOneJourney(@PathVariable Long id, Authentication authentication,Model model)  {
        User currentUser = service.findUserByUsername(authentication.getName());
        Journey currentJourney = journeyService.getById(id);
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd,MM,yyyy,HH,mm,ss");

        if(currentJourney!=null){
            if(currentJourney.getDriverId()==currentUser.getId()){
                SharedRide sharedRide = sharedRideService.getRideByDriverIdAndStatus(currentUser.getId(),"pending");
                SharedRide sharedRide1 = sharedRideService.getRideByDriverIdAndStatus(currentUser.getId(),"in-motion");
                Car car = carService.findCarByUserId(currentUser.getId());
                model.addAttribute("car", car);
                List<Review> review = reviewService.findReviewByDriverId(currentUser.getId());
                currentJourney.setStatus("ended");
                currentJourney.setProgress("ended");
                currentJourney.setUpdatedAt(LocalDateTime.now());
                currentJourney.setEndTime(formatter.format(new Date()));
                journeyService.saveRide(currentJourney);
                Long movingJourney = journeyService.countInMotionJourney(currentJourney.getRideId());
                if(movingJourney==0){
                    SharedRide currentRide = sharedRideService.getOneRide(currentJourney.getRideId());
                    currentRide.setStatus("ended");
                    currentRide.setUpdatedAt(LocalDateTime.now());
                    sharedRideService.updateSharedRide(currentRide);
                }

                String inStartDate = StringUtils.replace(currentJourney.getStartTime(), "-", " ");
                inStartDate = StringUtils.replace(inStartDate, ":", " ");
                inStartDate = StringUtils.replace(inStartDate, " ", ",");

                String enStartDate = StringUtils.replace(currentJourney.getEndTime(), "-", " ");
                enStartDate = StringUtils.replace(enStartDate, ":", " ");
                enStartDate = StringUtils.replace(enStartDate, " ", ",");
                LocalDateTime startDate = LocalDateTime.parse(inStartDate,myFormatObj);

                    LocalDateTime endDate = LocalDateTime.parse(enStartDate,myFormatObj);

                    Long hours = Math.abs(ChronoUnit.HOURS.between(endDate,startDate));
                    Long min = Math.abs(ChronoUnit.MINUTES.between(endDate,startDate));
                    Long sec = Math.abs(ChronoUnit.SECONDS.between(endDate,startDate));
                    String time="";
                    if(hours==0&& min==0 && sec!=0) {
                        time = sec + " seconds";
                    }else if(hours==0&& min!=0 && sec!=0){
                        time = min +"minutes "+sec+" seconds";
                    }else if (hours!=0 && min!=0 && sec!=0){
                        time = hours+" hours "+ min +" minutes "+sec+" seconds";
                    }
                model.addAttribute("car",car);
                model.addAttribute("review",review);
                model.addAttribute("username",currentUser.getUsername());
                model.addAttribute("journey",currentJourney);
                model.addAttribute("status","one-journey");
                model.addAttribute("period",time);
//                System.out.println(hours+""+min+""+sec);
                return "ended-journey";

            }else {
                model.addAttribute("status","invalid");
            }
        }else{
            model.addAttribute("status","no-journey");
        }
        return "redirect:/driver-ride";
    }

    /**
     * @param authentication
     * This controller routes to the update car page
     */
    @PostMapping("/update-car")
    private String updateCar(@RequestParam("imageFile") MultipartFile file, Authentication authentication){
        User user = service.findUserByUsername(authentication.getName());
        Car car = carService.findCarByUserId(user.getId());

        try{
            Map uploadResult = cloudc.upload(file.  getBytes(), ObjectUtils.asMap("resourcetype", "auto"));
            car.setCarImgUrl(uploadResult.get("url").toString());
            carService.saveCar(car);
        }
        catch (IOException e) {
            e.printStackTrace();
            return "redirect:/driver-profile";
        }
        return "redirect:/driver";
    }


}
