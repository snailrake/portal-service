package ru.intership.portalservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.intership.portalservice.dto.CompanyDto;
import ru.intership.portalservice.dto.CompanyShortDto;
import ru.intership.portalservice.dto.client.company.CompanyInfo;
import ru.intership.portalservice.model.Company;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CompanyMapper {

    @Mapping(target = "inn", source = "data.inn")
    @Mapping(target = "name", source = "value")
    @Mapping(target = "address", source = "data.address.value")
    @Mapping(target = "kpp", source = "data.kpp")
    @Mapping(target = "ogrn", source = "data.ogrn")
    Company toCompany(CompanyInfo companyResponse);

    CompanyDto toDto(Company company);

    CompanyShortDto toShortDto(Company company);

    List<CompanyShortDto> toShortDto(List<Company> companyList);
}
