package com.ttdashboard.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ttdashboard.entity.Project;
import com.ttdashboard.service.PdfService;
import com.ttdashboard.service.ProjectService;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Objects;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import org.springframework.web.bind.annotation.*;

@Controller
public class ProjectController {

    private final ProjectService projectService;
    private final PdfService pdfService;
    private final ObjectMapper objectMapper;

    public ProjectController(
            ProjectService projectService,
            PdfService pdfService,
            ObjectMapper objectMapper) {

        this.projectService = projectService;
        this.pdfService = pdfService;
        this.objectMapper = objectMapper;
    }

    // HEALTH CHECK FIX (IMPORTANT FOR RAILWAY)
    @GetMapping("/health")
    @ResponseBody
    public String health() {
        return "OK";
    }

    // ROOT PAGE
    @GetMapping("/")
    public String home(Model model) {

        model.addAttribute("project", new Project());

        return "form";
    }

    // SAVE PROJECT
    @PostMapping("/save")
    public String saveProject(

            @Valid @ModelAttribute("project") @NonNull Project project,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            return "form";
        }

        try {

            projectService.saveProject(project);

        } catch (IllegalArgumentException ex) {

            bindingResult.rejectValue(
                    "rollNumber",
                    "duplicate",
                    "Roll number already submitted");

            return "form";
        }

        return "redirect:/dashboard";
    }

    // DASHBOARD
    @GetMapping("/dashboard")
    public String showDashboard(

            @RequestParam(required=false) String rollNumber,
            @RequestParam(required=false) String studentName,
            @RequestParam(required=false) String section,
            @RequestParam(required=false) String projectName,
            @RequestParam(required=false) String frontendIp,

            @RequestParam(defaultValue="name") String sortBy,
            @RequestParam(defaultValue="asc") String sortDir,

            Model model){

        List<Project> projects =
                projectService.getFilteredProjects(
                        rollNumber,
                        studentName,
                        section,
                        projectName,
                        frontendIp,
                        sortBy,
                        sortDir);

        long totalSections =
                projects.stream()
                        .map(Project::getSection)
                        .filter(s -> s!=null && !s.isBlank())
                        .map(String::trim)
                        .map(String::toUpperCase)
                        .distinct()
                        .count();

        model.addAttribute("projects",projects);

        model.addAttribute("totalProjects",projects.size());

        model.addAttribute("totalSections",totalSections);

        model.addAttribute("sortBy",sortBy);

        model.addAttribute("sortDir",sortDir);

        model.addAttribute(
                "toggleSortDir",
                "asc".equalsIgnoreCase(sortDir) ? "desc" : "asc"
        );

        return "dashboard";
    }

    // DOWNLOAD JSON
    @GetMapping("/download/json")
    public ResponseEntity<byte[]> downloadJson() throws Exception {

        List<Project> projects =
                projectService.getAllProjects();

        byte[] payload =
                objectMapper
                        .writerWithDefaultPrettyPrinter()
                        .writeValueAsBytes(projects);

        return ResponseEntity.ok()

                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=projects.json")

                .contentType(
                        Objects.requireNonNull(
                                MediaType.APPLICATION_JSON))

                .body(payload);
    }

    // DOWNLOAD PDF
    @GetMapping("/download/pdf")
    public ResponseEntity<byte[]> downloadPdf(){

        List<Project> projects =
                projectService.getAllProjects();

        byte[] payload =
                pdfService.generatePdf(projects);

        return ResponseEntity.ok()

                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=projects.pdf")

                .contentType(
                        Objects.requireNonNull(
                                MediaType.APPLICATION_PDF))

                .body(payload);
    }

}