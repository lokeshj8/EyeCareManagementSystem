package com.eyecare.controller;

import com.eyecare.model.Doctor;
import com.eyecare.model.User;
import com.eyecare.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/doctors")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DoctorController {
    
    @Autowired
    private DoctorService doctorService;
    
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllDoctors(
            @RequestParam(required = false) String specialization) {
        
        List<Doctor> doctors;
        if (specialization != null && !specialization.trim().isEmpty()) {
            doctors = doctorService.getDoctorsBySpecialization(specialization);
        } else {
            doctors = doctorService.getAllDoctors();
        }
        
        List<Map<String, Object>> response = doctors.stream()
                .filter(doctor -> doctor.getUser().getIsActive())
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getDoctor(@PathVariable Long id) {
        Doctor doctor = doctorService.getDoctorById(id).orElse(null);
        
        if (doctor == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Doctor not found");
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(convertToResponse(doctor));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN')")
    public ResponseEntity<?> updateDoctor(@PathVariable Long id, @RequestBody Map<String, Object> updates, Authentication authentication) {
        Doctor doctor = doctorService.getDoctorById(id).orElse(null);
        
        if (doctor == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Doctor not found");
            return ResponseEntity.notFound().build();
        }
        
        User currentUser = (User) authentication.getPrincipal();
        
        // Check access permissions
        if (currentUser.getRole() == User.Role.DOCTOR && !doctor.getUser().getId().equals(currentUser.getId())) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Access denied");
            return ResponseEntity.status(403).body(error);
        }
        
        // Update doctor fields
        if (updates.containsKey("specialization")) {
            doctor.setSpecialization((String) updates.get("specialization"));
        }
        if (updates.containsKey("licenseNumber")) {
            doctor.setLicenseNumber((String) updates.get("licenseNumber"));
        }
        if (updates.containsKey("yearsExperience")) {
            doctor.setYearsExperience((Integer) updates.get("yearsExperience"));
        }
        if (updates.containsKey("consultationFee")) {
            Object fee = updates.get("consultationFee");
            if (fee instanceof Number) {
                doctor.setConsultationFee(BigDecimal.valueOf(((Number) fee).doubleValue()));
            }
        }
        if (updates.containsKey("bio")) {
            doctor.setBio((String) updates.get("bio"));
        }
        if (updates.containsKey("availableDays")) {
            doctor.setAvailableDays((String) updates.get("availableDays"));
        }
        if (updates.containsKey("availableHours")) {
            doctor.setAvailableHours((String) updates.get("availableHours"));
        }
        
        doctorService.saveDoctor(doctor);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Doctor profile updated successfully");
        return ResponseEntity.ok(response);
    }
    
    private Map<String, Object> convertToResponse(Doctor doctor) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", doctor.getId());
        response.put("first_name", doctor.getUser().getFirstName());
        response.put("last_name", doctor.getUser().getLastName());
        response.put("email", doctor.getUser().getEmail());
        response.put("phone", doctor.getUser().getPhone());
        response.put("specialization", doctor.getSpecialization());
        response.put("license_number", doctor.getLicenseNumber());
        response.put("years_experience", doctor.getYearsExperience());
        response.put("consultation_fee", doctor.getConsultationFee());
        response.put("bio", doctor.getBio());
        response.put("available_days", doctor.getAvailableDays());
        response.put("available_hours", doctor.getAvailableHours());
        return response;
    }
}