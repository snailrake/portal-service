package ru.intership.portalservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserShortDto {

    private String firstName;
    private String lastName;
    private String username;
    private List<String> roles;
}
