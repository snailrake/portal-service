package ru.intership.portalservice.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.intership.portalservice.dto.CompanyCardDto;
import ru.intership.portalservice.dto.CompanyShortDto;
import ru.intership.portalservice.dto.client.company.CompanyInfo;
import ru.intership.portalservice.exception.CompanyRegistrationException;
import ru.intership.portalservice.mapper.CompanyMapper;
import ru.intership.portalservice.model.Company;
import ru.intership.portalservice.model.UserRole;
import ru.intership.portalservice.repository.CompanyRepository;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final KeycloakService keycloakService;
    private final DadataService dadataService;
    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;
    private final UserService userService;

    @Transactional
    public String registerCompany(String username, String companyInn) {
        if (keycloakService.isGroupExists(companyInn)) {
            throw new CompanyRegistrationException(String.format("Company %s already in registry", companyInn));
        }
        String companyId = keycloakService.registerGroup(companyInn);
        if (!keycloakService.isRoleExists(companyInn)) {
            keycloakService.registerRole(companyInn);
        }
        keycloakService.joinToGroup(companyId, username);
        keycloakService.assignRoleToGroup(companyId, companyInn);
        if (!keycloakService.isRoleExists(companyInn + UserRole.ADMIN.name())) {
            keycloakService.registerRole(companyInn + UserRole.ADMIN.name());
        }
        keycloakService.assignRoleToUser(username, UserRole.ADMIN.name());
        keycloakService.assignRoleToUser(username, companyInn + UserRole.ADMIN.name());
        CompanyInfo companyInfo = dadataService.getCompanyInfo(companyInn);
        companyRepository.save(companyMapper.toCompany(companyInfo));
        return companyId;
    }

    @Transactional(readOnly = true)
    public Company getCompanyById(String companyInn) {
        return companyRepository.findById(companyInn)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Company %s not found", companyInn)));
    }

    @Transactional(readOnly = true)
    public CompanyCardDto getCompanyCard(String companyInn, Set<String> roles) {
        userService.validateUserIsAdmin(roles);
        Company company = getCompanyById(companyInn);
        long logistCount = keycloakService.findUsersByRole(companyInn + UserRole.LOGIST.name()).size();
        long driverCount = keycloakService.findUsersByRole(companyInn + UserRole.DRIVER.name()).size();
        return new CompanyCardDto(companyMapper.toDto(company), logistCount, driverCount);
    }

    @Transactional(readOnly = true)
    public List<CompanyShortDto> getAllCompaniesShort(int page, int size) {
        List<Company> companies = companyRepository.findAll(PageRequest.of(page, size)).getContent();
        return companyMapper.toShortDto(companies);
    }
}
