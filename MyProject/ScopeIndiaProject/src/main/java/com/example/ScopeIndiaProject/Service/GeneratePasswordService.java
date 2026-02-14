package com.example.ScopeIndiaProject.Service;

import com.example.ScopeIndiaProject.Model.RegisterModel;
import com.example.ScopeIndiaProject.Repository.FormRepository;
import com.example.ScopeIndiaProject.Repository.GeneratePasswordRepository;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
public class GeneratePasswordService {
    @Autowired
    private GeneratePasswordRepository generatePasswordRepository;

    @Autowired
    FormRepository formRepository;

    @Autowired
    RegisterEmailService registerEmailService;

    public String generateRandomPassword(){
        return String.valueOf(new Random().nextInt(900000)+100000);
    }

    public boolean generatePassword(String email) throws MessagingException {
        Optional<RegisterModel> useremail=generatePasswordRepository.findByEmail(email);

        if(useremail.isPresent()){
            RegisterModel user=useremail.get();
            String tempPassword=generateRandomPassword();
            user.setPassword(tempPassword);
            formRepository.save(user);

            String subject = "Temporary Password for Scope India";
            String message = "<html><body>" +
                    "<h3>Dear " + user.getFirstname() + ",</h3>" +
                    "<p>Your temporary password is: <b>" + tempPassword + "</b></p>" +
                    "<p>Please login and change your password immediately.</p>" +
                    "<p>Best Regards,<br>Scope India Team</p>" +
                    "</body></html>";

            registerEmailService.SendRegisterEmail(email, subject, message);
            return true;
        }
        return false;
    }
}
