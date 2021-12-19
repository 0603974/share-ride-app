package rideshare.demo.Entity.Request;


import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class PickupLocationRequest {
    private String city;
    private String country;
    private Double latitude;
    private Double longitude;
}
