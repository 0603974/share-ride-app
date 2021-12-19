package rideshare.demo.Entity.Respons.geocode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class Geometry{
    private String type;
    private List<Double> coordinates;
}