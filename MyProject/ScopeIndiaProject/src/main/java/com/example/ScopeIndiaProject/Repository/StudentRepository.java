package com.example.ScopeIndiaProject.Repository;

import com.example.ScopeIndiaProject.Model.RegisterModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<RegisterModel,Integer> {
    Optional<RegisterModel> findByEmail(@Param("email") String email);
}
