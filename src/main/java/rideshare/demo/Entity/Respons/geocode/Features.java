package rideshare.demo.Entity.Respons.geocode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class Features{
    private String type;
    private Geometry geometry;
    private Properties properties;
}
