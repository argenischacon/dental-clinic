package com.argenischacon.dentalclinic.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "patients")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Patient extends Person{
    @Column(nullable = false, unique = true, length = 10)
    private String patientCode;

    @Column(nullable = false, length = 5)
    private String bloodType;

    @Column(length = 500)
    private String allergies;

    @Column(length = 1000)
    private String medicalNotes;

    @Builder.Default
    @Column(nullable = false)
    private boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "guardian_id")
    private Guardian guardian;

    public boolean requiresGuardian(){
        return isMinor();
    }

    public boolean hasGuardian(){
        return guardian != null;
    }

    public void activate(){
        this.active = true;
    }

    public void deactivate(){
        this.active = false;
    }
}
