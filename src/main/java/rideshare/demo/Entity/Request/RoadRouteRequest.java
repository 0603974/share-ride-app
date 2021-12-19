package rideshare.demo.Entity.Request;

import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class RoadRouteRequest implements Serializable {
    private String city;
    private String country;
}
