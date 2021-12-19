package rideshare.demo.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SharedRide extends AbstractEntity {

    private Long driverId;
    private String origin;
    private String destination;
    private String date;
    private String time;
    private String meetUp;
    private Long availableSeat;
    private Long price;
    private String status;

    private Double latitude;
    private Double longitude;

    //this relationship entails that a sharedride can have multiple pickup locations assigned to it.
    @OneToMany(targetEntity = Location.class)
    private List<Location> pickupLocations;
}
