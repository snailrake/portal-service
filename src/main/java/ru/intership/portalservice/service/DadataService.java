package ru.intership.portalservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import ru.intership.portalservice.client.DadataClient;
import ru.intership.portalservice.dto.client.company.Query;
import ru.intership.portalservice.dto.client.company.CompanyInfo;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DadataService {

    private final DadataClient dadataClient;

    @Value("${services.dadata.api-key}")
    private String token;

    @Retryable(maxAttempts = 5, backoff = @Backoff(delay = 5000))
    public CompanyInfo getCompanyInfo(String inn) {
        return Optional.ofNullable(dadataClient.getCompanyInfo(token, new Query(inn)).getSuggestions().get(0))
                .orElseThrow(RuntimeException::new);
    }
}
