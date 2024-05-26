package ru.intership.portalservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.intership.portalservice.dto.VehicleDto;
import ru.intership.portalservice.mapper.VehicleMapperImpl;
import ru.intership.portalservice.model.Company;
import ru.intership.portalservice.model.Vehicle;
import ru.intership.portalservice.repository.VehicleRepository;
import ru.intership.portalservice.validator.UserValidator;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private CompanyService companyService;

    @Spy
    private VehicleMapperImpl vehicleMapper;

    @Mock
    private UserValidator userValidator;

    @InjectMocks
    private VehicleService vehicleService;

    @Test
    public void registerVehicle_ValidArgs() {
        VehicleDto expectedVehicleDto = getVehicleDto();
        when(companyService.getCompanyById(anyString())).thenReturn(getCompany());
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(getVehicle());

        VehicleDto actualVehicleDto = vehicleService.registerVehicle(getCompanyInn(), expectedVehicleDto, new HashSet<>());

        assertEquals(expectedVehicleDto, actualVehicleDto);
        verify(userValidator, times(1)).validateUserIsCompanyLogistOrAdmin(anyString(), anySet());
        verify(vehicleMapper, times(1)).toEntity(any(VehicleDto.class));
        verify(companyService, times(1)).getCompanyById(anyString());
        verify(vehicleRepository, times(1)).save(any(Vehicle.class));
        verify(vehicleMapper, times(1)).toDto(any(Vehicle.class));
    }

    private String getCompanyInn() {
        return "123456789101";
    }

    private VehicleDto getVehicleDto() {
        return VehicleDto.builder()
                .vin("123A5B7I9101112")
                .companyInn(getCompanyInn())
                .build();
    }

    private Vehicle getVehicle() {
        return Vehicle.builder()
                .vin("123A5B7I9101112")
                .build();
    }

    private Company getCompany() {
        return Company.builder()
                .inn(getCompanyInn())
                .build();
    }
}
