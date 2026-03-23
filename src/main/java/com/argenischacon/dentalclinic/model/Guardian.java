package com.argenischacon.dentalclinic.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "guardians")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Guardian extends Person{
    @Column(nullable = false, length = 50)
    private String relationship;

    @Builder.Default
    @Column(nullable = false)
    private boolean active = true;

    @Builder.Default
    @OneToMany(mappedBy = "guardian", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Patient> patients = new ArrayList<>();

    public boolean hasMultiplePatients(){
        return patients.size() > 1;
    }

    public int getPatientsCount(){
        return patients.size();
    }

    public void activate(){
        this.active = true;
    }

    public void deactivate(){
        this.active = false;
    }

    public void addPatient(Patient patient) {
        patients.add(patient);
        patient.setGuardian(this);
    }

    public void removePatient(Patient patient) {
        patients.remove(patient);
        patient.setGuardian(null);
    }
}
