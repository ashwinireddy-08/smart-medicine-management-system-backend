package com.project.medistock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.project.medistock.entity.Medicine;

import java.time.LocalDate;
import java.util.List;

public interface MedicineRepository extends JpaRepository<Medicine, Long> {

    // ✅ 1. Expiry alert
    List<Medicine> findByExpiryDateBefore(LocalDate date);

    // ✅ 2. Low stock
    List<Medicine> findByQuantityLessThan(int quantity);

    // 🔥 3. Smart search (NAME + COMPANY)
    List<Medicine> findByNameContainingIgnoreCaseOrCompanyContainingIgnoreCase(String name, String company);

    // 🔥 4. Extra (optional advanced search)
    List<Medicine> findByNameContainingIgnoreCase(String name);
}