package ru.intership.portalservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.intership.portalservice.model.Vehicle;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, String> {
}
