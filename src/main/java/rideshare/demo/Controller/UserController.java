package rideshare.demo.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import rideshare.demo.Entity.*;
import rideshare.demo.Entity.Respons.SharedRideResponse;
import rideshare.demo.Repository.SharedRideRepository;
import rideshare.demo.Repository.UserRepository;
import rideshare.demo.Service.*;
import rideshare.demo.security.NotificationService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller()
public class UserController {

    @Autowired
    UserService service;
    @Autowired
    CarService carService;
    @Autowired
    JourneyService journeyService;
    @Autowired
    SharedRideService sharedRideService;
    @Autowired
    ReviewService reviewService;
    @Autowired
    SharedRideRepository sharedRideRepository;
    @Autowired
    NotificationService notificationService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CommunityService communityService;
    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    @GetMapping("/")
    private String getRidePage1(Model model){
        return "redirect:/ride";
    }


    @GetMapping("/ride")
    private String getRidePage(Model model){
//        model.addAttribute("status","false");
        return "ride";
    }


    /**
     * This controller routes to the user controller HHTML page
     * @return user register page
     */
    @GetMapping("/register")
    private String getRegistrationPage()
    {
        return "register";
    }

    /**
     * This controller is responsible for getting  user input from html form to the back-end for manipulation and storing
     * @param user the object user is a parameter to be passed from the Html form
     * @param model
     * @return register page
     */
    @PostMapping("registration")
    private String registerUser(User user, Model model){
        user.setRole("USER");
        boolean status = service.saveUser(user);
        model.addAttribute("status",status);
        if(status==true){
            model.addAttribute("status","true");
            return "register";
        }else {
            model.addAttribute("status", "false");
        }
            return "register";
    }



    @GetMapping("/login")
    private String getLoginPage(){
        List <User> user = service.getAllByRole("ADMIN");
        if(user.isEmpty()){
            User newUser = new User();
            newUser.setFirstName("admin");
            newUser.setLastName("admin");
            newUser.setEmailAddress("admin@admin.com");
            newUser.setPassword(passwordEncoder.encode("admin"));
            newUser.setStatus("activated");
            newUser.setRole("ADMIN");
            newUser.setUsername("admin");
            newUser.setCreatedAt(LocalDateTime.now());
            newUser.setUpdatedAt(LocalDateTime.now());
            userRepository.save(newUser);
        }
        return "login";
    }

    @GetMapping("/account-activation")
    private String getActivationPage(){
        return "account-activation";
    }

    @PostMapping("/activate-account")
    private String activateAccount(User user, Model model){
        boolean status = service.activateAccount(user);
        if (status==true){
            return "redirect:/login";
        }
        model.addAttribute("status", "false");
        return "account-activation";
    }

    @GetMapping("/user-profile")
    private String getUserProfilePage(Authentication authentication, Model model){
        User user = service.findUserByUsername(authentication.getName());
        model.addAttribute("user",user);
        return "user-profile";
    }

    @PostMapping("/update-profile")
    private String submitUserProfile(UserProfileRequest profileRequest, Authentication authentication, Model model){
        User user = service.findUserByUsername(authentication.getName());
        boolean status = service.editUserProfile(profileRequest,user.getEmailAddress());
        if(status==false){
            model.addAttribute("status","Wrong current password");
        }
        return "redirect:/user-profile";
    }

    @GetMapping("/forgot-password")
    private String getForgotPasswordPage(){
        return "";
    }


    @PostMapping("/reset-password")
    private String resetPassword(User user,Model model){
        String email = user.getEmailAddress();
        User currentUser = service.findUser(email);
        boolean status = service.resetPassword(currentUser);
        String success ="";
        if(status==false){
            success="false";
        }else{
            success="true";
        }
        model.addAttribute("success",success);
        return "login";
    }


    @GetMapping("/my-journey")
    private String getJourneyPage(Authentication authentication,Model model){
        List<Journey>journeyHistory  = journeyService.getJourneyHistory(authentication.getName());
        User currentUser = service.findUserByUsername(authentication.getName());
        model.addAttribute("firstName",currentUser.getFirstName());
        if(journeyHistory.isEmpty()){
            model.addAttribute("status","No Ride History");
        }else
        model.addAttribute("journeyHistory",journeyHistory);
        return "my-journey";
    }

    //This endpoint returns the shared ride page and all pending rides for display
    @GetMapping("/shared-ride")
    private String getSharedRidesPage(Authentication authentication, Model model){
        List<SharedRide>pendingRide = sharedRideService.getAllPendingRides();
        List<SharedRideResponse>pendingRides = new ArrayList<>();

        for(SharedRide eachRide : pendingRide){
            SharedRideResponse sharedRideResponse = new SharedRideResponse();
            sharedRideResponse.setId(eachRide.getId());
            sharedRideResponse.setDate(eachRide.getDate().toString());
            sharedRideResponse.setDestination(eachRide.getDestination());
            sharedRideResponse.setOrigin(eachRide.getOrigin());
            sharedRideResponse.setTime(eachRide.getTime().toString());
            sharedRideResponse.setPrice(eachRide.getPrice());
            User driver = service.findById(eachRide.getDriverId());
            sharedRideResponse.setDriverName(driver.getUsername());
            pendingRides.add(sharedRideResponse);
        }

//        model.addAttribute("pendingRides",pendingRides);
        model.addAttribute("status","");
        model.addAttribute("nosearch","");

        return "shared-rides";
    }



    @GetMapping("/accept-ride/{id}")
    private String getAcceptDriverPage(@PathVariable Long id, Model model){
        SharedRide sharedRide = sharedRideService.getOneRide(id);
            User driver = service.findById(sharedRide.getDriverId());
            Car car = carService.findCarByUserId(driver.getId());

            List<Review> reviews = reviewService.findReviewByDriverId(driver.getId());

            model.addAttribute("driver", driver);
            model.addAttribute("car",car);
            model.addAttribute("ride",sharedRide);
            model.addAttribute("review",reviews);
            model.addAttribute("current","no");

        return "accept-ride";
    }

    @PostMapping("/accept-ride/{id}")
    private String acceptRide(@PathVariable Long id, Journey journey,Authentication authentication,Model model){
        SharedRide sharedRide = sharedRideService.getOneRide(id);
        String email = authentication.getName();
        User currentUser = service.findUserByUsername(email);
        List<Journey> activeJourney = journeyService.findByUserIdAndStatus(currentUser.getId(),"active");
        List<Journey> acceptedJourney = journeyService.findByUserIdAndStatus(currentUser.getId(),"accepted");
        List<Journey>currentJourney = new ArrayList<>();
        currentJourney.addAll(acceptedJourney);
        currentJourney.addAll(activeJourney);
          if(sharedRide!=null){
            if(currentJourney.isEmpty()) {
                if(sharedRide.getAvailableSeat()>0) {
                    journey.setUserId(currentUser.getId());
                    journey.setDriverId(sharedRide.getDriverId());
                    journey.setUsername(currentUser.getUsername());
                    journey.setRideId(id);
                    journey.setStatus("active");
                    journey.setProgress("pending");
                    journeyService.saveRide(journey);
                    sharedRide.setAvailableSeat(sharedRide.getAvailableSeat() - 1);
                     sharedRideRepository.save(sharedRide);
                    model.addAttribute("status", "success");
                }else{
                    model.addAttribute("status","invalid");
                }
            }else {
                model.addAttribute("status","failed");
            }
            Car car = carService.findCarByUserId(sharedRide.getDriverId());
            List<Review> reviews = reviewService.findReviewByDriverId(sharedRide.getDriverId());
            User driver = service.findById(sharedRide.getDriverId());
            model.addAttribute("driver", driver);
            model.addAttribute("car",car);
            model.addAttribute("ride",sharedRide);
            model.addAttribute("review",reviews);
            model.addAttribute("current","no");

            return "accept-ride";
        }else
            return "redirect:/shared-ride";
    }

    @GetMapping("/current-ride")
    private String getCurrentRide(Authentication authentication, Model model){
        User currentUser = service.findUserByUsername(authentication.getName());
        List<Journey> activeJourney = journeyService.findByUserIdAndStatus(currentUser.getId(),"active");
        List<Journey> acceptedJourney = journeyService.findByUserIdAndStatus(currentUser.getId(),"accepted");
        List<Journey>currentJourney = new ArrayList<>();
        currentJourney.addAll(acceptedJourney);
        currentJourney.addAll(activeJourney);


        List<SharedRideResponse>pendingRides = new ArrayList<>();

        if(currentJourney.isEmpty()) {
            List<SharedRide>pendingRide = sharedRideService.getAllPendingRides();
            for(SharedRide eachRide : pendingRide){
                SharedRideResponse sharedRideResponse = new SharedRideResponse();
                sharedRideResponse.setId(eachRide.getId());
                sharedRideResponse.setDate(eachRide.getDate());
                sharedRideResponse.setDestination(eachRide.getDestination());
                sharedRideResponse.setOrigin(eachRide.getOrigin());
                sharedRideResponse.setPrice(eachRide.getPrice());
                sharedRideResponse.setTime(eachRide.getTime());
                User driver = service.findById(eachRide.getDriverId());
                sharedRideResponse.setDriverName(driver.getUsername());
                pendingRides.add(sharedRideResponse);
            }

            model.addAttribute("pendingRides", pendingRides);
            model.addAttribute("status", "no-ride");
            model.addAttribute("nosearch", "");

            return "shared-rides";
        }
        else {
            Journey journey = currentJourney.get(0);
            if (journey.getProgress().equals("in-motion")) {
                return "redirect:/journey-progress";
            } else {
                SharedRide sharedRide = sharedRideService.getOneRide(journey.getRideId());
                Car car = carService.findCarByUserId(sharedRide.getDriverId());
                User driver = service.findById(sharedRide.getDriverId());
                List<Review> reviews = reviewService.findReviewByDriverId(sharedRide.getDriverId());
                model.addAttribute("driver", driver);
                model.addAttribute("car", car);
                model.addAttribute("ride", sharedRide);
                model.addAttribute("review", reviews);
                model.addAttribute("current", "yes");
                System.out.println("cheee"+journey.getProgress());
                return "accept-ride";
            }
        }
    }

    @GetMapping("/journey-progress")
    private String journeyProgress(Authentication authentication, Model model){
        User currentUser = service.findUserByUsername(authentication.getName());
        List<Journey> activeJourney = journeyService.findByUserIdAndStatus(currentUser.getId(),"active");
        List<Journey> acceptedJourney = journeyService.findByUserIdAndStatus(currentUser.getId(),"accepted");
        List<Journey>currentJourney = new ArrayList<>();
        currentJourney.addAll(acceptedJourney);
        currentJourney.addAll(activeJourney);
        if(currentJourney.isEmpty()) {
            return "redirect:/current-ride";
        }else {
            if(currentJourney.get(0).getProgress().equals("in-motion")){
                Journey journey = currentJourney.get(0);
                SharedRide sharedRide = sharedRideService.getOneRide(journey.getRideId());
                Car car = carService.findCarByUserId(sharedRide.getDriverId());
                User driver = service.findById(sharedRide.getDriverId());
                List<Review> reviews = reviewService.findReviewByDriverId(sharedRide.getDriverId());
                model.addAttribute("driver", driver);
                model.addAttribute("car",car);
                model.addAttribute("ride",sharedRide);
                model.addAttribute("journey",journey);
                model.addAttribute("review",reviews);
                model.addAttribute("current","yes");
                return "journey-initiated";
            }else{
                return "redirect:/current-ride";
            }
        }
    }

    @GetMapping("/cancel-ride")
    private String cancelRide(Authentication authentication, Model model){
        User currentUser = service.findUserByUsername(authentication.getName());
        List<Journey>journey = journeyService.findByUserIdAndStatus(currentUser.getId(),"active");
        List<Journey>acceptedJourney = journeyService.findByUserIdAndStatus(currentUser.getId(),"accepted");
        journey.addAll(acceptedJourney);

        Journey activeJourney = journey.get(0);
        if(journey.isEmpty()&&acceptedJourney.isEmpty()){
            return "redirect:/current-ride";
        }
        activeJourney.setStatus("canceled");
        SharedRide sharedRide = sharedRideService.getOneRide(activeJourney.getRideId());
        sharedRide.setAvailableSeat(sharedRide.getAvailableSeat()+1);
        journeyService.saveRide(activeJourney);
        return "redirect:/shared-rides";
    }

    @GetMapping("/shared-rides")
    private String getSharedRidePage(Authentication authentication, Model model){
        List<SharedRide>pendingRide = sharedRideService.getAllPendingRides();
        List<SharedRideResponse>pendingRides = new ArrayList<>();
        for(SharedRide eachRide : pendingRide){
            SharedRideResponse sharedRideResponse = new SharedRideResponse();
            sharedRideResponse.setDate(eachRide.getDate().toString());
            sharedRideResponse.setId(eachRide.getId());
            sharedRideResponse.setDestination(eachRide.getDestination());
            sharedRideResponse.setOrigin(eachRide.getOrigin());
            sharedRideResponse.setTime(eachRide.getTime().toString());
            sharedRideResponse.setPrice(eachRide.getPrice());
            User driver = service.findById(eachRide.getDriverId());
            sharedRideResponse.setDriverName(driver.getUsername());
            pendingRides.add(sharedRideResponse);
        }
//        model.addAttribute("pendingRides",pendingRides);
        model.addAttribute("status","canceled");
        model.addAttribute("nosearch","");

        return "shared-rides";
    }

    // This method uses the mathcing algorithm to search for pending rides and returns found rides to the shared rides page
    @PostMapping("/search")
    public String searchSharedRide(SharedRide sharedRide, Model model) {
        System.out.println(sharedRide);
//        List<SharedRide> foundRides = new ArrayList<>();
        List<SharedRide> foundRides = sharedRideService.matchUserWithRide(sharedRide);
        model.addAttribute("status","");

        if (sharedRide == null){
            model.addAttribute("nosearch","Nothing Found");
        }
            if (!foundRides.isEmpty()) {
                List<SharedRideResponse>pendingRides = new ArrayList<>();
//                List<SharedRide>pendingRide = sharedRideService.getAllPendingRides();
                for(SharedRide eachRide : foundRides){
                    SharedRideResponse sharedRideResponse = new SharedRideResponse();
                    sharedRideResponse.setId(eachRide.getId());
                    sharedRideResponse.setDate(eachRide.getDate());
                    sharedRideResponse.setDestination(eachRide.getDestination());
                    sharedRideResponse.setOrigin(eachRide.getOrigin());
                    sharedRideResponse.setTime(eachRide.getTime());
                    User driver = service.findById(eachRide.getDriverId());
                    sharedRideResponse.setDriverName(driver.getUsername());
                    pendingRides.add(sharedRideResponse);
                }

                model.addAttribute("pendingRides",pendingRides);
            }else {
                model.addAttribute("nosearch", "Nothing Found");
            }
        return "shared-rides";
    }

    //method to get logout page
    @GetMapping("/logout")
    public String Logout() {
        return "redirect:/logout-success";
    }


    //method to get logout page
    @GetMapping("/logout-success")
    public String logoutSuccess() {
        return "redirect:/";
    }

    @PostMapping("/save-review/{id}")
    public String saveReview( Review review, @PathVariable Long id, Authentication authentication){
        Review newReview = new Review();
        User currentUser = service.findUserByUsername(authentication.getName());
        newReview.setUserId(currentUser.getId());
        newReview.setUsername(currentUser.getUsername());
        newReview.setDriverId(id);
        newReview.setRating(review.getRating());
        newReview.setDescription(review.getDescription());
        reviewService.saveReview(newReview);
        return "redirect:/journey-progress";
    }

    @GetMapping("/community-page")
    public String communityPage(Model model){
        List<Community> allComment = communityService.getallComment();

        model.addAttribute("comment",allComment);
        return "community-page";
    }

    @PostMapping("/save-comment")
    public String saveComment(Community community){
        communityService.saveComment(community);
        return "redirect:/community-page";
    }


    @GetMapping("/review-page")
    public String reviewPage(Model model){
        List<Review> allReview = reviewService.getAllReviews();

        model.addAttribute("comment",allReview);
        return "review";
    }

}


/**
 *  if (sharedRide.getOrigin() != "" && sharedRide.getDestination().isEmpty() && sharedRide.getDate().isEmpty() && sharedRide.getTime().isEmpty()) {
 *             sharedRide1 = sharedRideRepository.findByOriginAndStatus(sharedRide.getOrigin(), "pending");
 *             System.out.println("heyyyyy");
 *         } else if (sharedRide.getOrigin().isEmpty() && sharedRide.getDestination() != "" && sharedRide.getDate().isEmpty() && sharedRide.getTime().isEmpty()) {
 *             sharedRide1 = sharedRideRepository.findByDestinationAndStatus(sharedRide.getDestination(), "pending");
 *         } else if (sharedRide.getOrigin().isEmpty() && sharedRide.getDestination().isEmpty() && sharedRide.getDate() != "" && sharedRide.getTime().isEmpty()) {
 *             sharedRide1 = sharedRideRepository.findByDateAndStatus(sharedRide.getDate(), "pending");
 *         } else if (sharedRide.getOrigin().isEmpty() && sharedRide.getDestination().isEmpty() && sharedRide.getDate().isEmpty() && sharedRide.getTime() != "") {
 *             sharedRide1 = sharedRideRepository.findByTimeAndStatus(sharedRide.getTime(), "pending");
 *         } else if (sharedRide.getOrigin() != "" && sharedRide.getDestination() != "" && sharedRide.getDate().isEmpty() && sharedRide.getTime().isEmpty()) {
 *             sharedRide1 = sharedRideRepository.findByOriginAndDestinationAndStatus(sharedRide.getOrigin(), sharedRide.getDestination(), "pending");
 *         } else if (sharedRide.getOrigin() != "" && sharedRide.getDestination().isEmpty() && sharedRide.getDate() != "" && sharedRide.getTime().isEmpty()) {
 *             sharedRide1 = sharedRideRepository.findByOriginAndDateAndStatus(sharedRide.getOrigin(), sharedRide.getDate(), "pending");
 *         } else if (sharedRide.getOrigin() != "" && sharedRide.getDestination().isEmpty() && sharedRide.getDate().isEmpty() && sharedRide.getTime() != "") {
 *             sharedRide1 = sharedRideRepository.findByOriginAndTimeAndStatus(sharedRide.getOrigin(), sharedRide.getTime(), "pending");
 *         } else if (sharedRide.getOrigin().isEmpty() && sharedRide.getDestination() != "" && sharedRide.getDate() != "" && sharedRide.getTime().isEmpty()) {
 *             sharedRide1 = sharedRideRepository.findByDestinationAndDateAndStatus(sharedRide.getDestination(), sharedRide.getDate(), "pending");
 *         } else if (sharedRide.getOrigin().isEmpty() && sharedRide.getDestination() != "" && sharedRide.getDate().isEmpty() && sharedRide.getTime() !="") {
 *             sharedRide1 = sharedRideRepository.findByDestinationAndTimeAndStatus(sharedRide.getDestination(), sharedRide.getTime(), "pending");
 *         } else if (sharedRide.getOrigin().isEmpty() && sharedRide.getDestination().isEmpty() && sharedRide.getDate() != "" && sharedRide.getTime() != "") {
 *             sharedRide1 = sharedRideRepository.findByDateAndTimeAndStatus(sharedRide.getDate(), sharedRide.getTime(), "pending");
 *         } else if (sharedRide.getOrigin() != "" && sharedRide.getDestination() != "" && sharedRide.getDate() != "" && sharedRide.getTime().isEmpty()) {
 *             sharedRide1 = sharedRideRepository.findByOriginAndDestinationAndDateAndStatus(sharedRide.getOrigin(), sharedRide.getDestination(), sharedRide.getDate(), "pending");
 *         } else if (sharedRide.getOrigin() != "" && sharedRide.getDestination().isEmpty() && sharedRide.getDate() != "" && sharedRide.getTime() != "") {
 *             sharedRide1 = sharedRideRepository.findByOriginAndDateAndTimeAndStatus(sharedRide.getOrigin(), sharedRide.getDate(), sharedRide.getTime(), "pending");
 *         } else if (sharedRide.getOrigin() != "" && sharedRide.getDestination() != "" && sharedRide.getDate().isEmpty() && sharedRide.getTime() != "") {
 *             sharedRide1 = sharedRideRepository.findByOriginAndDestinationAndTimeAndStatus(sharedRide.getOrigin(), sharedRide.getDestination(), sharedRide.getTime(), "pending");
 *         } else if (sharedRide.getOrigin().isEmpty() && sharedRide.getDestination() != "" && sharedRide.getDate() != "" && sharedRide.getTime() != "") {
 *             sharedRide1 = sharedRideRepository.findByDestinationAndDateAndTimeAndStatus(sharedRide.getDestination(), sharedRide.getDate(), sharedRide.getTime(), "pending");
 *         } else if (sharedRide.getOrigin() != "" && sharedRide.getDestination() != "" && sharedRide.getDate() != "" && sharedRide.getTime() != "") {
 *             sharedRide1 = sharedRideRepository.findByOriginAndDestinationAndDateAndTimeAndStatus(sharedRide.getOrigin(), sharedRide.getDestination(), sharedRide.getDate(), sharedRide.getTime(), "pending");
 *         }
 */