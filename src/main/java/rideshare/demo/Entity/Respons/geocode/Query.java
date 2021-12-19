package rideshare.demo.Entity.Respons.geocode;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class Query {
    private String text;
    private int size;
    private List<String> layers;

    @JsonProperty("private")
    private boolean isPrivate;

    @JsonProperty("parsed_text")
    private ParsedText parsedText;

}
