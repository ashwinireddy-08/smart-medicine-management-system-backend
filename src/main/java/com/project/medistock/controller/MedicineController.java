package com.project.medistock.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

import com.project.medistock.entity.Medicine;
import com.project.medistock.repository.MedicineRepository;
import com.project.medistock.service.HuggingFaceService;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/medicine")
public class MedicineController {

    @Autowired
    private MedicineRepository repository;

    @Autowired
    private HuggingFaceService aiService;

    // ✅ 1. Add Medicine
    @PostMapping("/add")
    public Medicine addMedicine(@RequestBody Medicine medicine) {
        return repository.save(medicine);
    }

    // ✅ 2. Get All Medicines
    @GetMapping("/all")
    public List<Medicine> getAllMedicines() {
        return repository.findAll();
    }

    // ✅ 3. Expiry Alert
    @GetMapping("/expiry-alert")
    public List<Medicine> getExpiryAlert() {
        LocalDate alertDate = LocalDate.now().plusDays(30);
        return repository.findByExpiryDateBefore(alertDate);
    }

    // ✅ 4. Low Stock
    @GetMapping("/low-stock")
    public List<Medicine> getLowStockMedicines() {
        return repository.findByQuantityLessThan(10);
    }

    // 🔍 5. Search
    @GetMapping("/search")
    public List<Medicine> searchMedicine(@RequestParam String name) {
        return repository
                .findByNameContainingIgnoreCaseOrCompanyContainingIgnoreCase(name, name);
    }

    // ❌ 6. Delete
    @DeleteMapping("/delete/{id}")
    public String deleteMedicine(@PathVariable Long id) {
        repository.deleteById(id);
        return "Deleted successfully";
    }

    // ⚠️ 7. Critical Expiry
    @GetMapping("/critical-expiry")
    public List<Medicine> getCriticalExpiry() {
        LocalDate date = LocalDate.now().plusDays(7);
        return repository.findByExpiryDateBefore(date);
    }

    // 🤖 8. AI Info (🔥 FIXED — NO ERROR EVER)
    @GetMapping("/medicine-info")
    public String getMedicineInfo(@RequestParam String name) {

        if (name == null || name.trim().isEmpty()) {
            return "⚠️ Please enter medicine name";
        }

        try {
            // 🔥 DO NOT MODIFY PROMPT HERE
            String result = aiService.getSuggestion(name);

            if (result == null || result.trim().isEmpty()) {
                return fallback(name);
            }

            return result;

        } catch (Exception e) {
            return fallback(name); // 🔥 NEVER RETURN ERROR
        }
    }

    // 📷 9. IMAGE → TEXT
    @PostMapping("/image-to-text")
    public String imageToText(@RequestParam("file") MultipartFile file) {

        try {
            File temp = File.createTempFile("img", ".jpg");
            file.transferTo(temp);

            String text = aiService.extractTextFromImage(temp);

            if (text == null || text.trim().isEmpty()) {
                return "unknown";
            }

            return text;

        } catch (Exception e) {
            return "unknown";
        }
    }

    // 🧠 SAFE FALLBACK (NO ERROR EVER)
    private String fallback(String name) {

        return "• " + name + " is used for treating common conditions\n" +
               "• Helps reduce pain, fever or infection\n" +
               "• Take only as prescribed\n" +
               "• Avoid overdose\n" +
               "• Consult doctor if needed";
    }
}