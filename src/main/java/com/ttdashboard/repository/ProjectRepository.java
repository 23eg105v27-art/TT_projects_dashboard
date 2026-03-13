package com.ttdashboard.repository;

import com.ttdashboard.entity.Project;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for Project entities.
 * Inherits standard CRUD and pagination operations from JpaRepository.
 */
@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    boolean existsByRollNumber(String rollNumber);

    List<Project> findByRollNumberContainingIgnoreCase(String rollNumber);

    List<Project> findByStudentNameContainingIgnoreCase(String studentName);

    List<Project> findBySection(String section);

    List<Project> findByNameContainingIgnoreCase(String name);

    List<Project> findByFrontendIpContainingIgnoreCase(String frontendIp);
}
