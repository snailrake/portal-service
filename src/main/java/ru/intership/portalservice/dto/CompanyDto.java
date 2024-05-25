package ru.intership.portalservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDto {

    private String inn;
    private String name;
    private String address;
    private String kpp;
    private String ogrn;
}
