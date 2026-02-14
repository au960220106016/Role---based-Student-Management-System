package com.example.ScopeIndiaProject.Repository;

import com.example.ScopeIndiaProject.Model.RegisterModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FormRepository extends JpaRepository<RegisterModel,Long> {
}
