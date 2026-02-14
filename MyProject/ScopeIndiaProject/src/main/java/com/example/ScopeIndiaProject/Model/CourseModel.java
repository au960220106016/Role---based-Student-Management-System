package com.example.ScopeIndiaProject.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class CourseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String coursename;
    private String coursedetails;
    private String courseduration;
    private float coursefee;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCoursename() {
        return coursename;
    }

    public void setCoursename(String coursename) {
        this.coursename = coursename;
    }

    public String getCoursedetails() {
        return coursedetails;
    }

    public void setCoursedetails(String coursedetails) {
        this.coursedetails = coursedetails;
    }

    public String getCourseduration() {
        return courseduration;
    }

    public void setCourseduration(String courseduration) {
        this.courseduration = courseduration;
    }

    public float getCoursefee() {
        return coursefee;
    }

    public void setCoursefee(float coursefee) {
        this.coursefee = coursefee;
    }
}
