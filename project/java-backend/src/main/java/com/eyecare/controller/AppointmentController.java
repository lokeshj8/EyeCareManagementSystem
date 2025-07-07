package com.eyecare.controller;

import com.eyecare.dto.AppointmentRequest;
import com.eyecare.model.Appointment;
import com.eyecare.model.Doctor;
import com.eyecare.model.Patient;
import com.eyecare.model.User;
import com.eyecare.service.AppointmentService;
import com.eyecare.service.DoctorService;
import com.eyecare.service.PatientService;
import jakarta.validation.Valid;
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
@RequestMapping("/appointments")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AppointmentController {
    
    @Autowired
    private AppointmentService appointmentService;
    
    @Autowired
    private PatientService patientService;
    
    @Autowired
    private DoctorService doctorService;
    
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllAppointments(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Long doctorId,
            @RequestParam(required = false) Long patientId,
            @RequestParam(required = false) String status,
            Authentication authentication) {
        
        User currentUser = (User) authentication.getPrincipal();
        Appointment.Status statusEnum = null;
        
        if (status != null) {
            try {
                statusEnum = Appointment.Status.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Invalid status, ignore
            }
        }
        
        List<Appointment> appointments;
        
        // Apply role-based filtering
        if (currentUser.getRole() == User.Role.PATIENT) {
            Patient patient = patientService.getPatientByUserId(currentUser.getId()).orElse(null);
            if (patient != null) {
                appointments = appointmentService.getAppointmentsByFilters(date, doctorId, patient.getId(), statusEnum);
            } else {
                appointments = List.of();
            }
        } else if (currentUser.getRole() == User.Role.DOCTOR) {
            Doctor doctor = doctorService.getDoctorByUserId(currentUser.getId()).orElse(null);
            if (doctor != null) {
                appointments = appointmentService.getAppointmentsByFilters(date, doctor.getId(), patientId, statusEnum);
            } else {
                appointments = List.of();
            }
        } else {
            appointments = appointmentService.getAppointmentsByFilters(date, doctorId, patientId, statusEnum);
        }
        
        List<Map<String, Object>> response = appointments.stream().map(this::convertToResponse).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
    
    @PostMapping
    public ResponseEntity<?> createAppointment(@Valid @RequestBody AppointmentRequest request) {
        // Check for conflicts
        if (appointmentService.hasConflictingAppointment(request.getDoctorId(), request.getAppointmentDate(), request.getAppointmentTime())) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Time slot already booked");
            return ResponseEntity.badRequest().body(error);
        }
        
        Patient patient = patientService.getPatientById(request.getPatientId()).orElse(null);
        Doctor doctor = doctorService.getDoctorById(request.getDoctorId()).orElse(null);
        
        if (patient == null || doctor == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Patient or Doctor not found");
            return ResponseEntity.badRequest().body(error);
        }
        
        Appointment appointment = new Appointment(patient, doctor, request.getAppointmentDate(), request.getAppointmentTime());
        appointment.setDuration(request.getDuration());
        appointment.setReason(request.getReason());
        appointment.setNotes(request.getNotes());
        
        Appointment savedAppointment = appointmentService.saveAppointment(appointment);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Appointment created successfully");
        response.put("appointmentId", savedAppointment.getId());
        
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateAppointment(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        Appointment appointment = appointmentService.getAppointmentById(id).orElse(null);
        
        if (appointment == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Appointment not found");
            return ResponseEntity.notFound().build();
        }
        
        // Update fields
        if (updates.containsKey("appointmentDate")) {
            appointment.setAppointmentDate(LocalDate.parse((String) updates.get("appointmentDate")));
        }
        if (updates.containsKey("appointmentTime")) {
            appointment.setAppointmentTime(java.time.LocalTime.parse((String) updates.get("appointmentTime")));
        }
        if (updates.containsKey("duration")) {
            appointment.setDuration((Integer) updates.get("duration"));
        }
        if (updates.containsKey("status")) {
            try {
                appointment.setStatus(Appointment.Status.valueOf(((String) updates.get("status")).toUpperCase()));
            } catch (IllegalArgumentException e) {
                // Invalid status, ignore
            }
        }
        if (updates.containsKey("reason")) {
            appointment.setReason((String) updates.get("reason"));
        }
        if (updates.containsKey("notes")) {
            appointment.setNotes((String) updates.get("notes"));
        }
        
        appointmentService.saveAppointment(appointment);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Appointment updated successfully");
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    public ResponseEntity<?> deleteAppointment(@PathVariable Long id) {
        if (!appointmentService.getAppointmentById(id).isPresent()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Appointment not found");
            return ResponseEntity.notFound().build();
        }
        
        appointmentService.deleteAppointment(id);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Appointment deleted successfully");
        return ResponseEntity.ok(response);
    }
    
    private Map<String, Object> convertToResponse(Appointment appointment) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", appointment.getId());
        response.put("patient_first_name", appointment.getPatient().getUser().getFirstName());
        response.put("patient_last_name", appointment.getPatient().getUser().getLastName());
        response.put("doctor_first_name", appointment.getDoctor().getUser().getFirstName());
        response.put("doctor_last_name", appointment.getDoctor().getUser().getLastName());
        response.put("appointment_date", appointment.getAppointmentDate().toString());
        response.put("appointment_time", appointment.getAppointmentTime().toString());
        response.put("duration", appointment.getDuration());
        response.put("status", appointment.getStatus().name().toLowerCase());
        response.put("reason", appointment.getReason());
        response.put("notes", appointment.getNotes());
        response.put("created_at", appointment.getCreatedAt().toString());
        response.put("updated_at", appointment.getUpdatedAt().toString());
        return response;
    }
}