package ru.intership.portalservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="vehicle")
public class Vehicle {

    @Id
    @Column(name = "vin", nullable = false)
    private String vin;

    @Column(name = "release_year", nullable = false)
    private int releaseYear;

    @ManyToOne
    @JoinColumn(name = "company_inn")
    private Company company;
}
