package com.example.ScopeIndiaProject.Service;

import com.example.ScopeIndiaProject.Model.RegisterModel;
import com.example.ScopeIndiaProject.Repository.FormRepository;
import com.example.ScopeIndiaProject.Repository.GenerateOtpRepository;
import com.example.ScopeIndiaProject.Repository.GeneratePasswordRepository;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
public class GenerateOtpService {
    @Autowired
    private GenerateOtpRepository generateOtpRepository;

    @Autowired
    FormRepository formRepository;

    @Autowired
    RegisterEmailService registerEmailService;

    public String generateRandomOtp(){
        return String.valueOf(new Random().nextInt(9000)+1000);
    }
    public boolean generateOtp(String email) throws MessagingException{
        Optional<RegisterModel> useremail=generateOtpRepository.findByEmail(email);

        if(useremail.isPresent()){
            RegisterModel user=useremail.get();
            String otp=generateRandomOtp();
            user.setOtp(otp);
            formRepository.save(user);
            String subject = "Otp for Login - Scope India";
            String message = "<html><body>" +
                    "<h3>Dear " + user.getFirstname() + ",</h3>" +
                    "<p>Your OTP changing your login password is: <b>" + otp + "</b></p>" +
                    "<p>Please enter your otp and change your password immediately.</p>" +
                    "<p>Best Regards,<br>Scope India Team</p>" +
                    "</body></html>";

            registerEmailService.SendRegisterEmail(email, subject, message);
            return true;
        }
        return false;
    }
}
