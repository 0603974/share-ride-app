package rideshare.demo.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rideshare.demo.Entity.Car;
import rideshare.demo.Repository.CarRepository;

import java.util.List;

@Service
public class CarService {
    @Autowired
    CarRepository carRepository;

    public void saveCar(Car car){
        carRepository.save(car);
    }

    public Car findCarByUserId(Long id){
        return carRepository.findByUserId(id);
    }

    public List<Car> getAllCar(){
        return carRepository.findAll();
    }
}
