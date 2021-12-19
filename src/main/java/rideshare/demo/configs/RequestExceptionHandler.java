package rideshare.demo.configs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.NoSuchElementException;

/**
 * RequestExceptionHandler
 */
@ControllerAdvice
public class RequestExceptionHandler extends ResponseEntityExceptionHandler {
    Logger logger = LoggerFactory.getLogger(RequestExceptionHandler.class);

    @ExceptionHandler(MalformedURLException.class)
    public ResponseEntity<RESTResponse> handleMalformedURKException(MalformedURLException ex) {
        return handleException(ex);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RESTResponse> handleException(Exception e) {
        logger.error("**INTERNAL SERVER ERROR*** ", e);

        RESTResponse response = RESTResponse.builder()
                        .error("An unknown error has occurred, if this persist contact the admin")
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .success(false)
                        .build();

        return new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);


    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Object> handleNoSuchElementException(NoSuchElementException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());

    }

    @ExceptionHandler(NoAvailableRideException.class)
    public ResponseEntity<RESTResponse> handleNoAvailableRideException(NoAvailableRideException ex) {

        RESTResponse response = RESTResponse.builder()
                .error(ex.getMessage())
                .status(HttpStatus.BAD_REQUEST)
                .success(false)
                .build();

        return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<RESTResponse> handleConstraintViolationException(ConstraintViolationException constraintViolationException) {
        return ResponseEntity.badRequest()
                .body(
                        RESTResponse.builder().data(constraintViolationException.getConstraintViolations())
                                .build()
                );

    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<RESTResponse> handleAccessDeniedException(AccessDeniedException accessDeniedException) {
        return ResponseEntity.badRequest().body(
                RESTResponse.builder()
                        .error(accessDeniedException.getMessage())
                        .success(false)
                        .build()
        );

    }

    // @ExceptionHandler(BindException.class)
    @Override
    public ResponseEntity<Object> handleBindException(BindException bindException, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return ResponseEntity.badRequest().body(bindException.getFieldErrors());
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Object> handleUsernameNotFoundException(UsernameNotFoundException usernameNotFoundException) {
        return ResponseEntity.badRequest().body(usernameNotFoundException.getMessage());
    }


    /**
     * Handle MethodArgumentNotValidException. Triggered when an object fails @Valid validation.
     *
     * @param ex      the MethodArgumentNotValidException that is thrown when @Valid validation fails
     * @param headers HttpHeaders
     * @param status  HttpStatus
     * @param request WebRequest
     * @return the ApiError object
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return ResponseEntity.badRequest().body(Arrays.asList(ex.getBindingResult().getGlobalErrors()));

    }

    /**
     * Handle MissingServletRequestParameterException. Triggered when a 'required' request parameter is missing.
     *
     * @param ex      MissingServletRequestParameterException
     * @param headers HttpHeaders
     * @param status  HttpStatus
     * @param request WebRequest
     * @return the ApiError object
     */
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex, HttpHeaders headers,
            HttpStatus status, WebRequest request) {
        return ResponseEntity.badRequest().body(ex.getParameterName() + " parameter is missing");
    }


}
