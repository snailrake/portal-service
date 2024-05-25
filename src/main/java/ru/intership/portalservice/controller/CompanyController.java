package ru.intership.portalservice.controller;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.intership.portalservice.config.context.UserContext;
import ru.intership.portalservice.dto.CompanyCardDto;
import ru.intership.portalservice.dto.CompanyShortDto;
import ru.intership.portalservice.service.CompanyService;

import java.util.List;

@Validated
@RestController
@RequestMapping("/company")
@RequiredArgsConstructor
public class CompanyController {

    private final UserContext userContext;
    private final CompanyService companyService;

    @PostMapping
    public String registerCompany(@RequestParam String companyInn) {
        return companyService.registerCompany(userContext.getUserName(), companyInn);
    }

    @GetMapping("/{companyInn}")
    public CompanyCardDto getAllCompany(@PathVariable String companyInn) {
        return companyService.getCompanyCard(companyInn, userContext.getUserRoles());
    }

    @GetMapping
    public List<CompanyShortDto> getAllCompanyShort(@RequestParam(defaultValue = "0") @Min(0) int page,
                                                    @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        return companyService.getAllCompaniesShort(page, size);
    }
}
