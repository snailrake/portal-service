package ru.intership.portalservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyCardDto {

    private CompanyDto company;
    private long logistCount;
    private long driverCount;
}
