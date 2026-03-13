package com.ttdashboard.service;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Comparator;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import com.ttdashboard.entity.Project;
import com.ttdashboard.repository.ProjectRepository;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository){
        this.projectRepository = projectRepository;
    }

    public @NonNull Project saveProject(@NonNull Project project){
        if (projectRepository.existsByRollNumber(normalize(project.getRollNumber()))) {
            throw new IllegalArgumentException("Roll number already submitted");
        }
        return Objects.requireNonNull(projectRepository.save(project));
    }

    public List<Project> getAllProjects(){
        return projectRepository.findAll(Sort.by("name"));
    }

    public List<Project> getFilteredProjects(
            String rollNumber,
            String studentName,
            String section,
            String projectName,
            String frontendIp,
            String sortBy,
            String sortDir) {
        String normalizedRollNumber = normalize(rollNumber);
        String normalizedStudentName = normalize(studentName);
        String normalizedSection = normalize(section);
        String normalizedProjectName = normalize(projectName);
        String normalizedFrontendIp = normalize(frontendIp);
        Sort.Direction direction = "desc".equalsIgnoreCase(normalize(sortDir))
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        String safeSortBy = toSortableColumn(sortBy);
        Comparator<Project> comparator = getComparator(safeSortBy, direction);

        if (normalizedRollNumber.isEmpty()
                && normalizedStudentName.isEmpty()
                && normalizedSection.isEmpty()
                && normalizedProjectName.isEmpty()
                && normalizedFrontendIp.isEmpty()) {
            return projectRepository.findAll(Sort.by(direction, safeSortBy));
        }

        List<Project> baseList;
        if (!normalizedRollNumber.isEmpty()) {
            baseList = projectRepository.findByRollNumberContainingIgnoreCase(normalizedRollNumber);
        } else if (!normalizedStudentName.isEmpty()) {
            baseList = projectRepository.findByStudentNameContainingIgnoreCase(normalizedStudentName);
        } else if (!normalizedSection.isEmpty()) {
            baseList = projectRepository.findBySection(normalizedSection);
        } else if (!normalizedProjectName.isEmpty()) {
            baseList = projectRepository.findByNameContainingIgnoreCase(normalizedProjectName);
        } else {
            baseList = projectRepository.findByFrontendIpContainingIgnoreCase(normalizedFrontendIp);
        }

        return baseList.stream()
            .filter(project -> normalizedRollNumber.isEmpty()
                || containsIgnoreCase(project.getRollNumber(), normalizedRollNumber))
                .filter(project -> normalizedStudentName.isEmpty()
                        || containsIgnoreCase(project.getStudentName(), normalizedStudentName))
                .filter(project -> normalizedSection.isEmpty()
                        || normalizedSection.equalsIgnoreCase(valueOrEmpty(project.getSection())))
                .filter(project -> normalizedProjectName.isEmpty()
                        || containsIgnoreCase(project.getName(), normalizedProjectName))
                .filter(project -> normalizedFrontendIp.isEmpty()
                        || containsIgnoreCase(project.getFrontendIp(), normalizedFrontendIp))
                .sorted(comparator)
                .toList();
    }

    private String toSortableColumn(String sortBy) {
        String normalized = normalize(sortBy);
        return switch (normalized) {
            case "id", "rollNumber", "name", "studentName", "section", "year", "frontendIp", "backendIp", "githubRepo" -> normalized;
            default -> "name";
        };
    }

    private Comparator<Project> getComparator(String sortBy, Sort.Direction direction) {
        Comparator<Project> comparator = switch (sortBy) {
            case "id" -> Comparator.comparing(project -> project.getId() == null ? Long.MIN_VALUE : project.getId());
            case "rollNumber" -> Comparator.comparing(project -> valueOrEmpty(project.getRollNumber()).toLowerCase(Locale.ROOT));
            case "studentName" -> Comparator.comparing(project -> valueOrEmpty(project.getStudentName()).toLowerCase(Locale.ROOT));
            case "section" -> Comparator.comparing(project -> valueOrEmpty(project.getSection()).toLowerCase(Locale.ROOT));
            case "year" -> Comparator.comparing(project -> valueOrEmpty(project.getYear()).toLowerCase(Locale.ROOT));
            case "frontendIp" -> Comparator.comparing(project -> valueOrEmpty(project.getFrontendIp()).toLowerCase(Locale.ROOT));
            case "backendIp" -> Comparator.comparing(project -> valueOrEmpty(project.getBackendIp()).toLowerCase(Locale.ROOT));
            case "githubRepo" -> Comparator.comparing(project -> valueOrEmpty(project.getGithubRepo()).toLowerCase(Locale.ROOT));
            default -> Comparator.comparing(project -> valueOrEmpty(project.getName()).toLowerCase(Locale.ROOT));
        };

        return direction == Sort.Direction.DESC ? comparator.reversed() : comparator;
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }

    private String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }

    private boolean containsIgnoreCase(String source, String query) {
        return valueOrEmpty(source).toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT));
    }
}
