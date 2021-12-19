package rideshare.demo.configs;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import rideshare.demo.Entity.Car;
import rideshare.demo.Entity.User;
import rideshare.demo.Repository.CarRepository;
import rideshare.demo.Repository.UserRepository;
import rideshare.demo.security.NotificationService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;


@Configuration
@RequiredArgsConstructor
@Slf4j
public class DatabaseSeeder {

    private final CarRepository carRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final PasswordEncoder passwordEncoder;


    @EventListener
    public void seedDataBase(ContextRefreshedEvent event) {
        log.info("seeding database");
//        seedUser();

    }



}
