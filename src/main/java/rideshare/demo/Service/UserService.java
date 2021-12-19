package rideshare.demo.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import rideshare.demo.Entity.Car;
import rideshare.demo.Entity.Request.DriverProfileRequest;
import rideshare.demo.Entity.User;
import rideshare.demo.Entity.UserPrincipal;
import rideshare.demo.Entity.UserProfileRequest;
import rideshare.demo.Repository.UserRepository;
import rideshare.demo.security.NotificationService;

import javax.mail.MessagingException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service("userDetailsService")
public class UserService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    NotificationService notificationService;
    @Autowired
    CarService carService;
    LocalDateTime localDate = LocalDateTime.now();



    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = findUser(email);
        if(user.getStatus().equalsIgnoreCase("activated")){
            return new UserPrincipal(user);
        }else
            throw new UsernameNotFoundException("invalid account");
    }

    public User findById(Long id){
        return userRepository.getOneById(id);
    }

    public User findUser(String email){
        User user= userRepository.findByEmailAddress(email);
        return user;
    }

    public User findUserByUsername(String username){
        User user= userRepository.findByUsername(username);
        return user;
    }

    public boolean saveUser(User user) {
        User existingUser = userRepository.findByEmailAddress(user.getEmailAddress()) ;
        User existingUser1 = userRepository.findByUsername(user.getUsername());
        if(existingUser==null && existingUser1==null){
            user.setActCode(notificationService.generateCode());
            user.setStatus("activated");
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);
//            String mail = "Welcome to RideShare, Kindly activate your account by clicking the link localhost:8080/account-activation using the activation code:"+ user.getActCode();
//
//            try {
//                notificationService.sendNotification(user.getEmailAddress(),"RideShare- Account Activation",mail);
//            } catch (MessagingException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            return true;
        }
        return false;
    }
    public boolean activateAccount(User user){
        User existingUser = findUser(user.getEmailAddress());
        if(existingUser!=null){
            if(user.getActCode().equals(existingUser.getActCode())){
                existingUser.setStatus("activated");
                existingUser.setUpdatedAt(localDate);
                userRepository.save(existingUser);
                return true;
            }
        }

            return false;
    }

    public boolean editUserProfile(UserProfileRequest profileRequest, String email){
        User user = findUser(email);
        String oldPassword = profileRequest.getOldPassword();
        String newPassword = profileRequest.getNewPassword();
        user.setFirstName(profileRequest.getFirstName());
        user.setLastName(profileRequest.getLastName());
        user.setPhoneNumber(profileRequest.getPhoneNumber());
        user.setUpdatedAt(localDate);
        if(oldPassword!=""&&newPassword!=""){
            if(passwordEncoder.matches(oldPassword,user.getPassword())){
                user.setPassword(passwordEncoder.encode(newPassword));
            }else
                return false;
        }
        userRepository.save(user);
        return true;
    }

    public boolean resetPassword(User user){
        User existingUser = findUser(user.getEmailAddress());
        String newPassword = notificationService.generateNewPassword();
        String mailSubject="RideShare- Password Reset";
        String mailBody="Your RideShare account password has been reset, your new password is: "+newPassword+" Use this password to login then go to your profile to update your password";
        if(existingUser!=null) {
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setUpdatedAt(localDate);
            userRepository.save(user);

//            try {
//            notificationService.sendNotification(user.getEmailAddress(),mailSubject,mailBody);
//            } catch (MessagingException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            return true;
        }else

        return false;
    }

    public Long countUser(){
        return userRepository.count();
    }

    public Long countUserByRole(String role){
        return userRepository.countAllByRole(role);
    }

    public List<User>getAllByRole(String role){
        return userRepository.getAllByRole(role);
    }

    public void updateUser(User user){
        user.setUpdatedAt(localDate);
        userRepository.save(user);
    }

    public boolean editDriverProfile(DriverProfileRequest profileRequest, String username){
        User user = findUserByUsername(username);
        Car car = carService.findCarByUserId(user.getId());
        String oldPassword = profileRequest.getOldPassword();
        String newPassword = profileRequest.getNewPassword();
        user.setFirstName(profileRequest.getFirstName());
        user.setLastName(profileRequest.getLastName());
        user.setPhoneNumber(profileRequest.getPhoneNumber());
        user.setUpdatedAt(localDate);
        car.setMake(profileRequest.getMake());
        car.setModel(profileRequest.getModel());
        car.setCategory(profileRequest.getCategory());
        car.setColor(profileRequest.getColor());
        car.setPlateNumber(profileRequest.getPlateNumber());
        car.setNoSeat(profileRequest.getNoSeat());
        car.setSpecialFeature(profileRequest.getSpecialFeature());

        if(oldPassword!=""&&newPassword!=""){
            if(passwordEncoder.matches(oldPassword,user.getPassword())){
                user.setPassword(passwordEncoder.encode(newPassword));
            }else
                return false;
        }
        userRepository.save(user);
        return true;
    }
}
