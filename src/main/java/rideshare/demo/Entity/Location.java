package rideshare.demo.Entity;


import lombok.*;

import javax.persistence.Entity;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
//this entity is used to model a location.
public class Location extends AbstractEntity{
    private String city;
    private String country;
    private Double latitude;
    private Double longitude;
}
