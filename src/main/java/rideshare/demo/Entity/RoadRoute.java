package rideshare.demo.Entity;


import lombok.*;

import javax.persistence.Entity;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class RoadRoute extends AbstractEntity{
    private String city;
    private String country;
    private Double latitude;
    private Double longitude;
}
