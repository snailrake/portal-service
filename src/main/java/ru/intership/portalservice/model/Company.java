package ru.intership.portalservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="company")
public class Company {

    @Id
    @Column(name = "inn", nullable = false)
    private String inn;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "kpp", nullable = false)
    private String kpp;

    @Column(name = "ogrn", nullable = false)
    private String ogrn;

    @OneToMany(mappedBy = "company", cascade = CascadeType.REMOVE)
    private List<Vehicle> vehicles;
}
