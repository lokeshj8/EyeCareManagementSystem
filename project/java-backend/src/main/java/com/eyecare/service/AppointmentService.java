package com.eyecare.service;

import com.eyecare.model.Appointment;
import com.eyecare.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentService {
    
    @Autowired
    private AppointmentRepository appointmentRepository;
    
    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }
    
    public Optional<Appointment> getAppointmentById(Long id) {
        return appointmentRepository.findById(id);
    }
    
    public List<Appointment> getAppointmentsByPatient(Long patientId) {
        return appointmentRepository.findByPatientId(patientId);
    }
    
    public List<Appointment> getAppointmentsByDoctor(Long doctorId) {
        return appointmentRepository.findByDoctorId(doctorId);
    }
    
    public List<Appointment> getAppointmentsByDate(LocalDate date) {
        return appointmentRepository.findByAppointmentDate(date);
    }
    
    public List<Appointment> getAppointmentsByFilters(LocalDate date, Long doctorId, Long patientId, Appointment.Status status) {
        return appointmentRepository.findByFilters(date, doctorId, patientId, status);
    }
    
    public boolean hasConflictingAppointment(Long doctorId, LocalDate date, LocalTime time) {
        List<Appointment> conflicts = appointmentRepository.findConflictingAppointments(doctorId, date, time);
        return !conflicts.isEmpty();
    }
    
    public Appointment saveAppointment(Appointment appointment) {
        return appointmentRepository.save(appointment);
    }
    
    public void deleteAppointment(Long id) {
        appointmentRepository.deleteById(id);
    }
}