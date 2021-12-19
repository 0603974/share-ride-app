package rideshare.demo.Controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import rideshare.demo.Entity.User;
import rideshare.demo.Service.UserService;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
@Slf4j
@Controller
public class myErrorController implements ErrorController {

    @Autowired
    UserService service;


    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Exception e) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

//        log.error("oops error ", e);

        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());

            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                return "redirect:/login";
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                return "redirect:/login";
            }else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                return "redirect:/login";
            }
        }
        return "redirect:/login";

    }

    @GetMapping("/router")
    public String errorRouter(Authentication authentication){
        User currentUser = service.findUserByUsername(authentication.getName());
        if(currentUser.getRole()=="USER"){
            return "redirect:/";
        }else if(currentUser.getRole()=="DRIVER"){
            return "redirect:/driver";
        }
        return null;
    }

    @Override
    public String getErrorPath() {
        return null;
    }
}
