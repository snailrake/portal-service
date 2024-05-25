package ru.intership.portalservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.intership.portalservice.dto.VehicleDto;
import ru.intership.portalservice.mapper.VehicleMapper;
import ru.intership.portalservice.model.Vehicle;
import ru.intership.portalservice.repository.VehicleRepository;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final CompanyService companyService;
    private final UserService userService;
    private final VehicleMapper vehicleMapper;

    public VehicleDto registerVehicle(String companyInn, VehicleDto vehicleDto, Set<String> roles) {
        userService.validateUserIsCompanyLogistOrAdmin(companyInn, roles);
        Vehicle vehicle = vehicleMapper.toEntity(vehicleDto);
        vehicle.setCompany(companyService.getCompanyById(companyInn));
        return vehicleMapper.toDto(vehicleRepository.save(vehicle));
    }
}
