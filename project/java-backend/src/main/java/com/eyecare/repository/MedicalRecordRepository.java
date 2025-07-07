package com.eyecare.repository;

import com.eyecare.model.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    List<MedicalRecord> findByPatientId(Long patientId);
    List<MedicalRecord> findByDoctorId(Long doctorId);
    List<MedicalRecord> findByPatientIdOrderByVisitDateDesc(Long patientId);
    
    @Query("SELECT mr FROM MedicalRecord mr WHERE " +
           "(:patientId IS NULL OR mr.patient.id = :patientId) AND " +
           "(:doctorId IS NULL OR mr.doctor.id = :doctorId) AND " +
           "(:startDate IS NULL OR mr.visitDate >= :startDate) AND " +
           "(:endDate IS NULL OR mr.visitDate <= :endDate)")
    List<MedicalRecord> findByFilters(
        @Param("patientId") Long patientId,
        @Param("doctorId") Long doctorId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
}