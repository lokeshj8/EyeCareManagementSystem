package com.eyecare.controller;

import com.eyecare.dto.JwtResponse;
import com.eyecare.dto.LoginRequest;
import com.eyecare.dto.RegisterRequest;
import com.eyecare.model.Doctor;
import com.eyecare.model.Patient;
import com.eyecare.model.User;
import com.eyecare.security.JwtUtils;
import com.eyecare.service.DoctorService;
import com.eyecare.service.PatientService;
import com.eyecare.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {
    
    @Autowired
    AuthenticationManager authenticationManager;
    
    @Autowired
    UserService userService;
    
    @Autowired
    PatientService patientService;
    
    @Autowired
    DoctorService doctorService;
    
    @Autowired
    JwtUtils jwtUtils;
    
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateToken((User) authentication.getPrincipal());
            
            User user = (User) authentication.getPrincipal();
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login successful");
            response.put("token", jwt);
            
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getId());
            userInfo.put("username", user.getUsername());
            userInfo.put("email", user.getEmail());
            userInfo.put("role", user.getRole().name().toLowerCase());
            userInfo.put("first_name", user.getFirstName());
            userInfo.put("last_name", user.getLastName());
            
            response.put("user", userInfo);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid credentials");
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest signUpRequest) {
        if (userService.existsByUsername(signUpRequest.getUsername())) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Username is already taken!");
            return ResponseEntity.badRequest().body(error);
        }
        
        if (userService.existsByEmail(signUpRequest.getEmail())) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Email is already in use!");
            return ResponseEntity.badRequest().body(error);
        }
        
        // Create new user
        User user = new User(signUpRequest.getUsername(),
                           signUpRequest.getEmail(),
                           signUpRequest.getPassword(),
                           signUpRequest.getRole(),
                           signUpRequest.getFirstName(),
                           signUpRequest.getLastName());
        
        user.setPhone(signUpRequest.getPhone());
        user.setDateOfBirth(signUpRequest.getDateOfBirth());
        user.setAddress(signUpRequest.getAddress());
        
        User savedUser = userService.createUser(user);
        
        // Create role-specific record
        if (savedUser.getRole() == User.Role.PATIENT) {
            Patient patient = new Patient(savedUser);
            patientService.savePatient(patient);
        } else if (savedUser.getRole() == User.Role.DOCTOR) {
            Doctor doctor = new Doctor(savedUser);
            doctorService.saveDoctor(doctor);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "User registered successfully");
        response.put("userId", savedUser.getId());
        
        return ResponseEntity.ok(response);
    }
}