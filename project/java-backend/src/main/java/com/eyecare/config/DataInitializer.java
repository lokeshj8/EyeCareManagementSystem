package com.eyecare.config;

import com.eyecare.model.Doctor;
import com.eyecare.model.User;
import com.eyecare.service.DoctorService;
import com.eyecare.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private DoctorService doctorService;
    
    @Override
    public void run(String... args) throws Exception {
        // Create default admin user
        if (!userService.existsByUsername("admin")) {
            User admin = new User("admin", "admin@eyecare.com", "admin123", User.Role.ADMIN, "System", "Administrator");
            admin.setPhone("+1234567890");
            userService.createUser(admin);
        }
        
        // Create default doctor
        if (!userService.existsByUsername("dr.smith")) {
            User doctorUser = new User("dr.smith", "dr.smith@eyecare.com", "doctor123", User.Role.DOCTOR, "John", "Smith");
            doctorUser.setPhone("+1234567891");
            User savedDoctorUser = userService.createUser(doctorUser);
            
            Doctor doctor = new Doctor(savedDoctorUser);
            doctor.setSpecialization("Ophthalmology");
            doctor.setLicenseNumber("MD12345");
            doctor.setYearsExperience(15);
            doctor.setConsultationFee(new BigDecimal("150.00"));
            doctor.setBio("Experienced ophthalmologist specializing in retinal diseases and cataract surgery.");
            doctorService.saveDoctor(doctor);
        }
    }
}