package rideshare.demo.Entity.Respons.geocode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class GeoCodeResponse implements Serializable {

    private Geocoding geocoding;


    private List<Features> features;


}
