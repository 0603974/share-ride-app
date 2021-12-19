package rideshare.demo.configs;

import lombok.*;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class RESTResponse <T>{
    private T data;
    private String error;
    private HttpStatus status;
    private String message;
    private boolean success;

    public RESTResponse(String error, HttpStatus status){
        this.error = error;
        this.status = status;
    }
    public RESTResponse(T data){
        this.data = data;
    }
}
