package com.author.crm.model;

public class Trainer extends User {
    private String specialization;

    public Trainer() {
    }

    public Trainer(Long id, String firstName, String lastName, String username, String password, boolean active, String specialization) {
        super(id, firstName, lastName, username, password, active);

        this.specialization = specialization;
    }

    public Long getUserId() {
        return getId();
    }

    public void setUserId(Long userId) {
        setId(userId);
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }
}
