package ru.intership.portalservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.intership.portalservice.model.Company;

@Repository
public interface CompanyRepository extends JpaRepository<Company, String> {
}
