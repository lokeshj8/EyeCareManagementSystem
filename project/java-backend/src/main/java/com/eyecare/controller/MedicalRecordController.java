package com.eyecare.controller;

import com.eyecare.model.Doctor;
import com.eyecare.model.MedicalRecord;
import com.eyecare.model.Patient;
import com.eyecare.model.User;
import com.eyecare.service.DoctorService;
import com.eyecare.service.MedicalRecordService;
import com.eyecare.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/medical-records")
@CrossOrigin(origins = "*", maxAge = 3600)
public class MedicalRecordController {
    
    @Autowired
    private MedicalRecordService medicalRecordService;
    
    @Autowired
    private PatientService patientService;
    
    @Autowired
    private DoctorService doctorService;
    
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllMedicalRecords(
            @RequestParam(required = false) Long patientId,
            @RequestParam(required = false) Long doctorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Authentication authentication) {
        
        User currentUser = (User) authentication.getPrincipal();
        List<MedicalRecord> records;
        
        // Apply role-based filtering
        if (currentUser.getRole() == User.Role.PATIENT) {
            Patient patient = patientService.getPatientByUserId(currentUser.getId()).orElse(null);
            if (patient != null) {
                records = medicalRecordService.getMedicalRecordsByFilters(patient.getId(), doctorId, startDate, endDate);
            } else {
                records = List.of();
            }
        } else if (currentUser.getRole() == User.Role.DOCTOR) {
            Doctor doctor = doctorService.getDoctorByUserId(currentUser.getId()).orElse(null);
            if (doctor != null) {
                records = medicalRecordService.getMedicalRecordsByFilters(patientId, doctor.getId(), startDate, endDate);
            } else {
                records = List.of();
            }
        } else {
            records = medicalRecordService.getMedicalRecordsByFilters(patientId, doctorId, startDate, endDate);
        }
        
        List<Map<String, Object>> response = records.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN')")
    public ResponseEntity<?> createMedicalRecord(@RequestBody Map<String, Object> request) {
        Long patientId = Long.valueOf(request.get("patientId").toString());
        Long doctorId = Long.valueOf(request.get("doctorId").toString());
        LocalDate visitDate = LocalDate.parse((String) request.get("visitDate"));
        
        Patient patient = patientService.getPatientById(patientId).orElse(null);
        Doctor doctor = doctorService.getDoctorById(doctorId).orElse(null);
        
        if (patient == null || doctor == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Patient or Doctor not found");
            return ResponseEntity.badRequest().body(error);
        }
        
        MedicalRecord record = new MedicalRecord(patient, doctor, visitDate);
        
        // Set optional fields
        if (request.containsKey("appointmentId")) {
            // Handle appointment ID if needed
        }
        if (request.containsKey("chiefComplaint")) {
            record.setChiefComplaint((String) request.get("chiefComplaint"));
        }
        if (request.containsKey("diagnosis")) {
            record.setDiagnosis((String) request.get("diagnosis"));
        }
        if (request.containsKey("treatmentPlan")) {
            record.setTreatmentPlan((String) request.get("treatmentPlan"));
        }
        if (request.containsKey("prescription")) {
            record.setPrescription((String) request.get("prescription"));
        }
        if (request.containsKey("followUpDate")) {
            record.setFollowUpDate(LocalDate.parse((String) request.get("followUpDate")));
        }
        if (request.containsKey("visualAcuityRight")) {
            record.setVisualAcuityRight((String) request.get("visualAcuityRight"));
        }
        if (request.containsKey("visualAcuityLeft")) {
            record.setVisualAcuityLeft((String) request.get("visualAcuityLeft"));
        }
        if (request.containsKey("eyePressureRight")) {
            record.setEyePressureRight((String) request.get("eyePressureRight"));
        }
        if (request.containsKey("eyePressureLeft")) {
            record.setEyePressureLeft((String) request.get("eyePressureLeft"));
        }
        if (request.containsKey("notes")) {
            record.setNotes((String) request.get("notes"));
        }
        
        MedicalRecord savedRecord = medicalRecordService.saveMedicalRecord(record);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Medical record created successfully");
        response.put("recordId", savedRecord.getId());
        
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN')")
    public ResponseEntity<?> updateMedicalRecord(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        MedicalRecord record = medicalRecordService.getMedicalRecordById(id).orElse(null);
        
        if (record == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Medical record not found");
            return ResponseEntity.notFound().build();
        }
        
        // Update fields
        if (updates.containsKey("chiefComplaint")) {
            record.setChiefComplaint((String) updates.get("chiefComplaint"));
        }
        if (updates.containsKey("diagnosis")) {
            record.setDiagnosis((String) updates.get("diagnosis"));
        }
        if (updates.containsKey("treatmentPlan")) {
            record.setTreatmentPlan((String) updates.get("treatmentPlan"));
        }
        if (updates.containsKey("prescription")) {
            record.setPrescription((String) updates.get("prescription"));
        }
        if (updates.containsKey("followUpDate")) {
            record.setFollowUpDate(LocalDate.parse((String) updates.get("followUpDate")));
        }
        if (updates.containsKey("visualAcuityRight")) {
            record.setVisualAcuityRight((String) updates.get("visualAcuityRight"));
        }
        if (updates.containsKey("visualAcuityLeft")) {
            record.setVisualAcuityLeft((String) updates.get("visualAcuityLeft"));
        }
        if (updates.containsKey("eyePressureRight")) {
            record.setEyePressureRight((String) updates.get("eyePressureRight"));
        }
        if (updates.containsKey("eyePressureLeft")) {
            record.setEyePressureLeft((String) updates.get("eyePressureLeft"));
        }
        if (updates.containsKey("notes")) {
            record.setNotes((String) updates.get("notes"));
        }
        
        medicalRecordService.saveMedicalRecord(record);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Medical record updated successfully");
        return ResponseEntity.ok(response);
    }
    
    private Map<String, Object> convertToResponse(MedicalRecord record) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", record.getId());
        response.put("patient_first_name", record.getPatient().getUser().getFirstName());
        response.put("patient_last_name", record.getPatient().getUser().getLastName());
        response.put("doctor_first_name", record.getDoctor().getUser().getFirstName());
        response.put("doctor_last_name", record.getDoctor().getUser().getLastName());
        response.put("visit_date", record.getVisitDate().toString());
        response.put("chief_complaint", record.getChiefComplaint());
        response.put("diagnosis", record.getDiagnosis());
        response.put("treatment_plan", record.getTreatmentPlan());
        response.put("prescription", record.getPrescription());
        response.put("follow_up_date", record.getFollowUpDate());
        response.put("visual_acuity_right", record.getVisualAcuityRight());
        response.put("visual_acuity_left", record.getVisualAcuityLeft());
        response.put("eye_pressure_right", record.getEyePressureRight());
        response.put("eye_pressure_left", record.getEyePressureLeft());
        response.put("notes", record.getNotes());
        response.put("created_at", record.getCreatedAt().toString());
        return response;
    }
}