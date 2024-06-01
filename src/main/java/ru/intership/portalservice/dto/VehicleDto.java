package ru.intership.portalservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleDto {

    @NotNull
    @Size(min = 17, max = 17)
    private String vin;

    @Positive
    private int releaseYear;

    @Positive
    private String companyInn;
}
