package rideshare.demo.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import rideshare.demo.Entity.Journey;
import rideshare.demo.Entity.User;
import rideshare.demo.Repository.JourneyRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class JourneyService {

    @Autowired
    JourneyRepository journeyRepository;
    @Autowired
    UserService userService;

    public Journey getById(Long id){
        return journeyRepository.getOne(id);
    }

    public void saveRide(Journey journey){
            journeyRepository.save(journey);
    }

    public List<Journey> findByUserIdAndStatus(Long id,String status){
        return  journeyRepository.findByUserIdAndStatus(id,status);
    }

    public List<Journey> getJourneyHistory(String email){
        User user = userService.findUser(email);
        List<Journey> completedJourney = journeyRepository.findByUserIdAndStatus(user.getId(),"completed");
        List<Journey> failedJourney = journeyRepository.findByUserIdAndStatus(user.getId(),"failed");
        List<Journey> historyJourney = new ArrayList<Journey>();
        historyJourney.addAll(completedJourney);
        historyJourney.addAll(failedJourney);
        return historyJourney;
    }

    public List<Journey> getJourneyByRideIdAndStatus(Long id){
        List<Journey>activeJourney =  journeyRepository.findByRideIdAndStatus(id,"active");
        List<Journey>acceptedJourney =journeyRepository.findByRideIdAndStatus(id,"accepted");
        List<Journey> rideJourney = new ArrayList<Journey>();
        if(acceptedJourney!=null){
            rideJourney.addAll(activeJourney);
        }
        if(acceptedJourney!=null){
            rideJourney.addAll(acceptedJourney);
        }
        return rideJourney;
    }

    public Long countInMotionJourney(Long id){
        return journeyRepository.countByRideIdAndStatusAndProgress(id,"accepted", "in-motion");
    }






}
