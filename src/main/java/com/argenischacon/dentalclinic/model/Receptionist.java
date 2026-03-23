package com.argenischacon.dentalclinic.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@Table(name = "receptionists")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Receptionist extends Person implements UserOwner {
    @Column(nullable = false, unique = true, length = 20)
    private String employeeNumber;

    @Column(nullable = false)
    private LocalDate hireDate;

    @Builder.Default
    @Column(nullable = false)
    private boolean active = true;

    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, unique = true, updatable = false)
    private User user;

    @Override
    public void activate() {
        this.active = true;
        if (this.user != null) {
            this.user.setActive(true);
        }
    }

    @Override
    public void deactivate() {
        this.active = false;
        if (this.user != null) {
            this.user.setActive(false);
        }
    }
}
