package rideshare.demo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Logger;

@Service
public class NotificationService {

    @Autowired
    JavaMailSender javaMailSender;

    Logger logger;

    public void sendNotification( String emailTo,String mailSubject, String mailText)throws MessagingException, IOException {

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(emailTo);
        simpleMailMessage.setSubject(mailSubject);
        simpleMailMessage.setText(mailText);
        javaMailSender.send(simpleMailMessage);
    }

    /**
     * This method generates random numbers for the activation code
     * @return
     */
    public String generateCode() {
        String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";

        StringBuilder ref = new StringBuilder();
        Random rnd = new Random();
        while (ref.length() < 8) { // length of the random string.
            int index = (int) (rnd.nextFloat() * CHARS.length());
            ref.append(CHARS.charAt(index));
        }
        String reference = ref.toString();

        return reference;
    }

    /**
     * This method generates random password for the reset password
     * @return
     */
    public String generateNewPassword() {
        String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890_-";

        StringBuilder ref1 = new StringBuilder();
        Random rnd = new Random();
        while (ref1.length() < 12) { // length of the random string.
            int index = (int) (rnd.nextFloat() * CHARS.length());
            ref1.append(CHARS.charAt(index));
        }
        String rndPassword = ref1.toString();
        return rndPassword;
    }
}
