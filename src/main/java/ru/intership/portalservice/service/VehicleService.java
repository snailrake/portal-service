package ru.intership.portalservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.intership.portalservice.dto.VehicleDto;
import ru.intership.portalservice.mapper.VehicleMapper;
import ru.intership.portalservice.model.Vehicle;
import ru.intership.portalservice.repository.VehicleRepository;
import ru.intership.portalservice.validator.UserValidator;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final CompanyService companyService;
    private final VehicleMapper vehicleMapper;
    private final UserValidator userValidator;

    public VehicleDto registerVehicle(String companyInn, VehicleDto vehicleDto, Set<String> roles) {
        userValidator.validateUserIsCompanyLogistOrAdmin(companyInn, roles);
        Vehicle vehicle = vehicleMapper.toEntity(vehicleDto);
        vehicle.setCompany(companyService.getCompanyById(companyInn));
        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        log.info("Saved vehicle: {}", savedVehicle);
        return vehicleMapper.toDto(savedVehicle);
    }
}
