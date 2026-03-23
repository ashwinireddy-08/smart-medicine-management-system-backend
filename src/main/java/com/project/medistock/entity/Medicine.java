package com.project.medistock.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "medicine")
public class Medicine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String company;
    private int quantity;
    private double price;

    private LocalDate mfgDate;
    private LocalDate expiryDate;

    private String storeName;
    private String location;

    // Getters & Setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public LocalDate getMfgDate() { return mfgDate; }
    public void setMfgDate(LocalDate mfgDate) { this.mfgDate = mfgDate; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    // 🔥 Extra UI methods

    public long getDaysToExpiry() {
        if (expiryDate == null) return 0;
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), expiryDate);
    }

    public String getStockStatus() {
        if (quantity == 0) return "OUT_OF_STOCK";
        else if (quantity < 10) return "LOW_STOCK";
        else return "AVAILABLE";
    }
}