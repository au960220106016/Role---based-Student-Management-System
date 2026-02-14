package com.example.ScopeIndiaProject.Repository;

import com.example.ScopeIndiaProject.Model.CourseModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<CourseModel, Integer> {
}
