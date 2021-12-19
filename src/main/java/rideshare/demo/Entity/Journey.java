package rideshare.demo.Entity;

import lombok.*;

import javax.persistence.Entity;

@Entity
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Journey extends AbstractEntity {
    private Long userId;
    private Long driverId;
    private String username;
    private String disabilities;
    private String note;
    private Long rideId;
    private String startTime;
    private String endTime;
    private String status;
    private String progress;
}
