package com.example.ScopeIndiaProject.Service;

import com.example.ScopeIndiaProject.Model.RegisterModel;
import com.example.ScopeIndiaProject.Repository.ResetPasswordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ResetPasswordService {

    @Autowired
    private ResetPasswordRepository resetPasswordRepository;

    public Boolean SetPassword(String email,String password){
        Optional<RegisterModel> useremail=resetPasswordRepository.findByEmail(email);
        if(useremail.isPresent()){
            RegisterModel user = useremail.get();
            user.setPassword(password);
            resetPasswordRepository.save(user);
            return true;
        }
        return false;
    }
}
