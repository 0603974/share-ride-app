package rideshare.demo.configs;

public class NoAvailableRideException extends RuntimeException{
    public NoAvailableRideException(){
        super("No available Ride");
    }
    public NoAvailableRideException(String s) {
        super(s);
    }
}
