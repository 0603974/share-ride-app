package rideshare.demo.Entity.Respons.geocode;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class Properties {

    private String id;
    private String gid;
    private String layer;
    private String source;
    private String source_id;
    private String name;
    private String confidence;
    private String distance;
    private String accuracy;
    private String country;
    private String country_gid;
    private String country_a;
    private String macroregion;
    private String macroregion_gid;
    private String macroregion_a;
    private String region;
    private String region_gid;
    private String region_a;
    private String localadmin;
    private String localadmin_gid;
    private String locality;
    private String locality_gid;
    private String borough;
    private String borough_gid;
    private String neighbourhood;
    private String neighbourhood_gid;
    private String continent;
    private String continent_gid;
    private String label;
}
