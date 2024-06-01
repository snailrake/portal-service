package ru.intership.portalservice.dto.client.company;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyData {

    private String inn;
    private String kpp;
    private String ogrn;
    private Address address;
}
