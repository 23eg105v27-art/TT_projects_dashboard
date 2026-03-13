package com.ttdashboard.entity;

import jakarta.persistence.*;

@Entity
@Table(name="projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String rollNumber;

    private String studentName;

    private String section;

    private String year;

    private String name;

    private String frontendIp;

    private String backendIp;

    private String githubRepo;

    @Column(length = 2000)
    private String abstractText;

    public Project(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFrontendIp() {
        return frontendIp;
    }

    public void setFrontendIp(String frontendIp) {
        this.frontendIp = frontendIp;
    }

    public String getBackendIp() {
        return backendIp;
    }

    public void setBackendIp(String backendIp) {
        this.backendIp = backendIp;
    }

    public String getGithubRepo() {
        return githubRepo;
    }

    public void setGithubRepo(String githubRepo) {
        this.githubRepo = githubRepo;
    }

    public String getAbstractText() {
        return abstractText;
    }

    public void setAbstractText(String abstractText) {
        this.abstractText = abstractText;
    }

}