package rideshare.demo.Entity.Respons.geocode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class ParsedText{
    private String city;
    private String country;
}
