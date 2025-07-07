package com.eyecare.controller;

import com.eyecare.model.Patient;
import com.eyecare.model.User;
import com.eyecare.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/patients")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PatientController {
    
    @Autowired
    private PatientService patientService;
    
    @GetMapping
    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN')")
    public ResponseEntity<List<Map<String, Object>>> getAllPatients(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "50") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        
        List<Patient> patients;
        if (search != null && !search.trim().isEmpty()) {
            patients = patientService.searchPatients(search);
        } else {
            patients = patientService.getAllPatients();
        }
        
        // Apply pagination
        int start = Math.min(offset, patients.size());
        int end = Math.min(start + limit, patients.size());
        List<Patient> paginatedPatients = patients.subList(start, end);
        
        List<Map<String, Object>> response = paginatedPatients.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getPatient(@PathVariable Long id, Authentication authentication) {
        Patient patient = patientService.getPatientById(id).orElse(null);
        
        if (patient == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Patient not found");
            return ResponseEntity.notFound().build();
        }
        
        User currentUser = (User) authentication.getPrincipal();
        
        // Check access permissions
        if (currentUser.getRole() == User.Role.PATIENT && !patient.getUser().getId().equals(currentUser.getId())) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Access denied");
            return ResponseEntity.status(403).body(error);
        }
        
        return ResponseEntity.ok(convertToResponse(patient));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePatient(@PathVariable Long id, @RequestBody Map<String, Object> updates, Authentication authentication) {
        Patient patient = patientService.getPatientById(id).orElse(null);
        
        if (patient == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Patient not found");
            return ResponseEntity.notFound().build();
        }
        
        User currentUser = (User) authentication.getPrincipal();
        
        // Check access permissions
        if (currentUser.getRole() == User.Role.PATIENT && !patient.getUser().getId().equals(currentUser.getId())) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Access denied");
            return ResponseEntity.status(403).body(error);
        }
        
        // Update patient fields
        if (updates.containsKey("emergencyContact")) {
            patient.setEmergencyContact((String) updates.get("emergencyContact"));
        }
        if (updates.containsKey("emergencyPhone")) {
            patient.setEmergencyPhone((String) updates.get("emergencyPhone"));
        }
        if (updates.containsKey("insuranceProvider")) {
            patient.setInsuranceProvider((String) updates.get("insuranceProvider"));
        }
        if (updates.containsKey("insuranceNumber")) {
            patient.setInsuranceNumber((String) updates.get("insuranceNumber"));
        }
        if (updates.containsKey("allergies")) {
            patient.setAllergies((String) updates.get("allergies"));
        }
        if (updates.containsKey("currentMedications")) {
            patient.setCurrentMedications((String) updates.get("currentMedications"));
        }
        if (updates.containsKey("medicalHistory")) {
            patient.setMedicalHistory((String) updates.get("medicalHistory"));
        }
        
        patientService.savePatient(patient);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Patient updated successfully");
        return ResponseEntity.ok(response);
    }
    
    private Map<String, Object> convertToResponse(Patient patient) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", patient.getId());
        response.put("first_name", patient.getUser().getFirstName());
        response.put("last_name", patient.getUser().getLastName());
        response.put("email", patient.getUser().getEmail());
        response.put("phone", patient.getUser().getPhone());
        response.put("date_of_birth", patient.getUser().getDateOfBirth());
        response.put("address", patient.getUser().getAddress());
        response.put("emergency_contact", patient.getEmergencyContact());
        response.put("emergency_phone", patient.getEmergencyPhone());
        response.put("insurance_provider", patient.getInsuranceProvider());
        response.put("insurance_number", patient.getInsuranceNumber());
        response.put("allergies", patient.getAllergies());
        response.put("current_medications", patient.getCurrentMedications());
        response.put("medical_history", patient.getMedicalHistory());
        return response;
    }
}