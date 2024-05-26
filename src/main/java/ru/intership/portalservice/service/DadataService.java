package ru.intership.portalservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import ru.intership.portalservice.client.DadataClient;
import ru.intership.portalservice.dto.client.company.CompanyInfo;
import ru.intership.portalservice.dto.client.company.Query;
import ru.intership.portalservice.exception.CompanyRegistrationException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DadataService {

    private final DadataClient dadataClient;

    @Value("${services.dadata.api-key}")
    private String token;

    @Retryable(maxAttempts = 5, backoff = @Backoff(delay = 5000))
    public CompanyInfo getCompanyInfo(String inn) {
        List<CompanyInfo> companyInfo = dadataClient.getCompanyInfo(token, new Query(inn)).getSuggestions();
        if (companyInfo.isEmpty()) {
            throw new CompanyRegistrationException(String.format("Company %s not found in global registry", inn));
        }
        log.info("Company info fetched: {}", companyInfo);
        return companyInfo.get(0);
    }
}
