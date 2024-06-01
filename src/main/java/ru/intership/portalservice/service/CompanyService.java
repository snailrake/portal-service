package ru.intership.portalservice.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.intership.portalservice.dto.CompanyCardDto;
import ru.intership.portalservice.dto.CompanyShortDto;
import ru.intership.portalservice.dto.client.company.CompanyInfo;
import ru.intership.portalservice.mapper.CompanyMapper;
import ru.intership.portalservice.model.Company;
import ru.intership.portalservice.model.UserRole;
import ru.intership.portalservice.repository.CompanyRepository;
import ru.intership.portalservice.validator.CompanyValidator;
import ru.intership.portalservice.validator.UserValidator;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyService {

    private final KeycloakService keycloakService;
    private final DadataService dadataService;
    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;
    private final UserValidator userValidator;
    private final CompanyValidator companyValidator;

    @Transactional
    public String registerCompany(String userId, String username, String companyInn) {
        try {
            companyValidator.validateGroupNotExists(companyInn);
            CompanyInfo companyInfo = dadataService.getCompanyInfo(companyInn);
            String companyId = keycloakService.registerGroup(companyInn);
            log.info("Registered company with id: {}", companyId);
            addUserToCompany(companyInn, companyId, username);
            assignRolesToUser(username, companyInn, UserRole.ADMIN.name());
            companyRepository.save(companyMapper.toCompany(companyInfo));
            log.info("Company info saved: {}", companyInfo);
            return companyId;
        } catch (RuntimeException e) {
            companyRegisterKeycloakRollback(userId, companyInn);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public Company getCompanyById(String companyInn) {
        return companyRepository.findById(companyInn)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Company %s not found", companyInn)));
    }

    @Transactional(readOnly = true)
    public CompanyCardDto getCompanyCard(String companyInn, Set<String> roles) {
        userValidator.validateUserIsAdmin(roles);
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

    private void addUserToCompany(String companyInn, String companyId, String username) {
        if (!keycloakService.isRoleExists(companyInn)) keycloakService.registerRole(companyInn);
        keycloakService.joinToGroup(companyId, username);
        keycloakService.assignRoleToGroup(companyId, companyInn);
    }

    private void assignRolesToUser(String username, String companyInn, String role) {
        if (!keycloakService.isRoleExists(companyInn + role)) {
            keycloakService.registerRole(companyInn + role);
        }
        keycloakService.assignRoleToUser(username, role);
        keycloakService.assignRoleToUser(username, companyInn + role);
    }

    private void companyRegisterKeycloakRollback(String userId, String companyInn) {
        keycloakService.unregisterGroup(companyInn);
        keycloakService.deleteRole(companyInn);
        keycloakService.deleteRole(companyInn + UserRole.ADMIN.name());
        keycloakService.unassignUserRole(userId, companyInn);
        keycloakService.unassignUserRole(userId, companyInn + UserRole.ADMIN.name());
    }
}
