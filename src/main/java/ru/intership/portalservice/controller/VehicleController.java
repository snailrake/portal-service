package ru.intership.portalservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.intership.portalservice.dto.VehicleDto;
import ru.intership.portalservice.service.VehicleService;
import ru.intership.webcommonspringbootstarter.UserContext;

@RestController
@RequestMapping("/vehicle")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;
    private final UserContext userContext;

    @PostMapping
    public VehicleDto registerVehicle(@RequestParam String companyInn,
                                      @RequestBody VehicleDto vehicleDto) {
        return vehicleService.registerVehicle(companyInn, vehicleDto, userContext.getUserRoles());
    }
}
