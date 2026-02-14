package com.example.ScopeIndiaProject.Service;

import com.example.ScopeIndiaProject.Model.RegisterModel;
import com.example.ScopeIndiaProject.Repository.FormRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FormService {
    @Autowired
    private FormRepository formRepository;

    public FormRepository getFormRepository() {
        return formRepository;
    }
    public void insertData(RegisterModel registerModel) {
        formRepository.save(registerModel);
    }
}