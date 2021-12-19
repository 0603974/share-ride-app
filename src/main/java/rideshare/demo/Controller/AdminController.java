package rideshare.demo.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import rideshare.demo.Entity.*;
import rideshare.demo.Entity.Request.CarTableData;
import rideshare.demo.Entity.Request.JourneyTableData;
import rideshare.demo.Repository.CarRepository;
import rideshare.demo.Repository.UserRepository;
import rideshare.demo.Service.CarService;
import rideshare.demo.Service.ReviewService;
import rideshare.demo.Service.SharedRideService;
import rideshare.demo.Service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
public class AdminController {

    @Autowired
    SharedRideService sharedRideService;
    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CarService carService;
    @Autowired
    CarRepository carRepository;
    @Autowired
    ReviewService reviewService;

    /**
     *
     * @param authentication
     * @param model
     *
     * This controller routes to the admin dashboard
     */
    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model){
        List<JourneyTableData> allJourneyData =  new ArrayList<>();
        List<SharedRide> rides = sharedRideService.getAllInMotionRides();
//        List<Review>reviews = reviewService.findFirst5Reviews();
        for (SharedRide sharedRide : rides){
            JourneyTableData journeyTableData = new JourneyTableData();
            journeyTableData.setMeetUp(sharedRide.getMeetUp());
            journeyTableData.setDestination(sharedRide.getDestination());
            journeyTableData.setOrigin(sharedRide.getOrigin());
            journeyTableData.setDriverUsername(userService.findById(sharedRide.getDriverId()).getUsername());
            journeyTableData.setDate(sharedRide.getDate().toString());

            allJourneyData.add(journeyTableData) ;
        }
//        model.addAttribute("review",reviews);
        model.addAttribute("journey",allJourneyData);
        model.addAttribute("allUser",userService.countUser());
        model.addAttribute("driver",userService.countUserByRole("DRIVER"));
        model.addAttribute("rider",userService.countUserByRole("USER"));
        model.addAttribute("allRide",sharedRideService.countRide());
        model.addAttribute("activeRide",sharedRideService.countByStatus("pending"));
        model.addAttribute("closedRide",sharedRideService.countByStatus("ended"));
        return "admin-dashboard";
    }


    /**
     *
     * @param model
     *
     * This controller routes to the admin data-table page
     */
    @GetMapping("/data-table")
    public String adminDataTable(Model model){
        model.addAttribute("driver",userService.getAllByRole("DRIVER"));
        model.addAttribute("rider",userService.getAllByRole("USER"));
        model.addAttribute("status","");
        List<Car> cars = carService.getAllCar();
        List<CarTableData> carTableData = new ArrayList<>();
        for(Car userCar : cars){
            CarTableData  newCarTableData = new CarTableData();
            newCarTableData.setUsername(userService.findById(userCar.getUserId()).getUsername());
            newCarTableData.setMake(userCar.getMake());
            newCarTableData.setModel(userCar.getModel());
            newCarTableData.setCategory(userCar.getCategory());
            newCarTableData.setColor(userCar.getColor());
            newCarTableData.setPlateNumber(userCar.getPlateNumber());
            newCarTableData.setNoSeat(userCar.getNoSeat());
            newCarTableData.setSpecialFeature(userCar.getSpecialFeature());
            newCarTableData.setCarImgUrl(userCar.getCarImgUrl());
            carTableData.add(newCarTableData);
        }
        model.addAttribute("carData",carTableData);

        List<SharedRide> rides = sharedRideService.getAllRides();
        System.out.print(rides);
        List<JourneyTableData> allJourneyData =  new ArrayList<>();
        for (SharedRide sharedRide : rides){
            JourneyTableData journeyTableData = new JourneyTableData();
            journeyTableData.setMeetUp(sharedRide.getMeetUp());
            journeyTableData.setDestination(sharedRide.getDestination());
            journeyTableData.setOrigin(sharedRide.getOrigin());
            journeyTableData.setDriverUsername(userService.findById(sharedRide.getDriverId()).getUsername());
            journeyTableData.setDate(sharedRide.getDate().toString());
            journeyTableData.setStatus(sharedRide.getStatus());
            allJourneyData.add(journeyTableData) ;
        }
        System.out.println("yo"+allJourneyData.get(0).getDestination());
        model.addAttribute("ride",allJourneyData);
        return "admin-user-table";
    }

    /**
     *
     * @param id
     * @param model
     *
     * This controller routes is to delete a user based on the id on admin page
     */
    @GetMapping("/delete-user/{id}")
    public String deleteUser(@PathVariable Long id, Model model){
        User user = userService.findById(id);
        Car car = carService.findCarByUserId(user.getId());
        model.addAttribute("driver",userService.getAllByRole("DRIVER"));
        model.addAttribute("rider",userService.getAllByRole("USER"));
        String status = "";
        if(user!=null){
            userRepository.delete(user);
            carRepository.delete(car);
            status="success";
        }else{
            status="failed";
        }
        model.addAttribute("status",status);
        return "admin-user-table";
    }

    /**
     *
     * @param id
     * @param model
     *
     * This controller routes to the block or unblock a user on admin dashboard
     */
    @GetMapping("/review-user/{id}")
    public String blockUnblockUser(@PathVariable Long id, Model model){
        User user = userService.findById(id);
        model.addAttribute("driver",userService.getAllByRole("DRIVER"));
        model.addAttribute("rider",userService.getAllByRole("USER"));
        String status = "";
        if(user!=null){
            if(user.getStatus().equals("pending")){
                status="pendingVerified";
                user.setStatus("verified");
            }else if(user.getStatus().equals("activated")){
                status="activatedBlocked";
                user.setStatus("blocked");
            }else if(user.getStatus().equals("blocked")){
                status="blockedActivated";
                user.setStatus("activated");
            }
            userService.updateUser(user);
        }else{
            status="failed";
        }
        model.addAttribute("status",status);
        return "admin-user-table";
    }

    /**
     *
     * @param authentication
     * @param model
     *
     * This controller routes to the admin prefile page
     */
    @GetMapping("/admin-profile")
    public String adminProfile(Model model, Authentication authentication){

        User user = userService.findUserByUsername(authentication.getName());
        model.addAttribute("user",user);
        return "admin-profile";
    }

    /**
     *
     * @param authentication
     * @param model
     *
     * This controller routes to the updated admin profile page of admin user
     */
    @PostMapping("/update-admin-profile")
    private String submitAdminProfile(UserProfileRequest profileRequest, Authentication authentication, Model model){
        User user = userService.findUserByUsername(authentication.getName());
        boolean state = userService.editUserProfile(profileRequest,user.getEmailAddress());

        String status = "";
        if(state==false){
            status="failed";
        }
        else{
            status="true";
        }
        model.addAttribute("user",user);
        model.addAttribute("status",status);
        return "admin-profile";
    }

    /**
     *
     * This controller routes to the add admin page
     */
    @GetMapping("/add-admin")
    public String adminRegisterPage(){
        return "admin-register";
    }

    @PostMapping("/save-admin")
    public String registerAdmin(User user, Model model){
        user.setRole("ADMIN");
        boolean state = userService.saveUser(user);
        String status = "";
        if(state==true){
            status ="true";

        }else {
            status = "false";
        }
        model.addAttribute("status",status);
        return "admin-register";
    }

}
