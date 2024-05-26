package ru.intership.portalservice.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.intership.portalservice.dto.CompanyCardDto;
import ru.intership.portalservice.dto.CompanyDto;
import ru.intership.portalservice.dto.CompanyShortDto;
import ru.intership.portalservice.dto.client.company.CompanyInfo;
import ru.intership.portalservice.mapper.CompanyMapperImpl;
import ru.intership.portalservice.model.Company;
import ru.intership.portalservice.repository.CompanyRepository;
import ru.intership.portalservice.validator.CompanyValidator;
import ru.intership.portalservice.validator.UserValidator;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CompanyServiceTest {

    @Mock
    private KeycloakService keycloakService;

    @Mock
    private DadataService dadataService;

    @Mock
    private CompanyRepository companyRepository;

    @Spy
    private CompanyMapperImpl companyMapper;

    @Mock
    private UserValidator userValidator;

    @Mock
    private CompanyValidator companyValidator;

    @InjectMocks
    private CompanyService companyService;

    @Test
    public void registerCompany_ValidArgs() {
        String companyInn = getCompanyInn();
        String username = "oaerg@mail.ru";
        String expectedCompanyId = UUID.randomUUID().toString();
        when(dadataService.getCompanyInfo(anyString())).thenReturn(new CompanyInfo());
        when(keycloakService.registerGroup(anyString())).thenReturn(expectedCompanyId);

        String actualCompanyId = companyService.registerCompany(username, companyInn);

        assertEquals(expectedCompanyId, actualCompanyId);
        verify(companyValidator, times(1)).validateGroupNotExists(anyString());
        verify(dadataService, times(1)).getCompanyInfo(anyString());
        verify(keycloakService, times(1)).registerGroup(anyString());
        verify(keycloakService, times(1)).joinToGroup(anyString(), anyString());
        verify(keycloakService, times(1)).assignRoleToGroup(anyString(), anyString());
        verify(keycloakService, times(2)).isRoleExists(anyString());
        verify(keycloakService, times(2)).assignRoleToUser(anyString(), anyString());
        verify(companyMapper, times(1)).toCompany(any(CompanyInfo.class));
        verify(companyRepository, times(1)).save(any(Company.class));
    }

    @Test
    public void getCompanyById_ValidArgs() {
        Company expectedCompany = new Company();
        when(companyRepository.findById(anyString())).thenReturn(Optional.of(expectedCompany));

        Company actualCompany = companyService.getCompanyById(anyString());

        assertEquals(expectedCompany, actualCompany);
        verify(companyRepository, times(1)).findById(anyString());
    }

    @Test
    public void getCompanyById_NotExistsCompany_ThrowsEntityNotFoundException() {
        when(companyRepository.findById(anyString())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> companyService.getCompanyById(anyString()));
    }

    @Test
    public void getCompanyCard_ValidArgs() {
        CompanyCardDto expectedCard = getCompanyCardDto();
        when(keycloakService.findUsersByRole(anyString())).thenReturn(getEmployeesList());
        when(companyRepository.findById(anyString())).thenReturn(Optional.of(new Company()));

        CompanyCardDto actualCard = companyService.getCompanyCard(getCompanyInn(), new HashSet<>());

        assertEquals(expectedCard, actualCard);
        verify(userValidator, times(1)).validateUserIsAdmin(anySet());
        verify(companyRepository, times(1)).findById(anyString());
        verify(keycloakService, times(2)).findUsersByRole(anyString());
        verify(companyMapper, times(1)).toDto(any(Company.class));
    }

    @Test
    public void getCompanyCard_NotExistsCompany_ThrowsEntityNotFoundException() {
        when(companyRepository.findById(anyString())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> companyService.getCompanyCard(getCompanyInn(), new HashSet<>()));
    }

    @Test
    public void getAllCompaniesShort_ValidArgs() {
        List<CompanyShortDto> expectedCompanyDtos = getCompaniesShortDtos();
        when(companyRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(getCompanies()));

        List<CompanyShortDto> actualCompanyDtos = companyService.getAllCompaniesShort(0, 25);

        assertEquals(expectedCompanyDtos, actualCompanyDtos);
        verify(companyRepository, times(1)).findAll(any(Pageable.class));
        verify(companyMapper, times(1)).toShortDto(anyList());
    }

    private CompanyCardDto getCompanyCardDto() {
        return CompanyCardDto.builder()
                .company(new CompanyDto())
                .logistCount(3)
                .driverCount(3)
                .build();
    }

    private List<UserRepresentation> getEmployeesList() {
        return List.of(new UserRepresentation(), new UserRepresentation(), new UserRepresentation());
    }

    private String getCompanyInn() {
        return "123456789101";
    }

    private List<CompanyShortDto> getCompaniesShortDtos() {
        return List.of(
                CompanyShortDto.builder()
                        .inn(getCompanyInn())
                        .build(),
                CompanyShortDto.builder()
                        .inn(getCompanyInn())
                        .build()
        );
    }

    private List<Company> getCompanies() {
        return List.of(
                Company.builder()
                        .inn(getCompanyInn())
                        .build(),
                Company.builder()
                        .inn(getCompanyInn())
                        .build()
        );
    }
}
