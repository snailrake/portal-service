package ru.intership.portalservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import ru.intership.portalservice.dto.client.company.Query;
import ru.intership.portalservice.dto.client.company.CompanyResponse;

@FeignClient(name = "dadata-service", url = "${services.dadata.api-url}")
public interface DadataClient {

    @PostMapping(value = "${services.dadata.find-company-url}", consumes = "application/json")
    CompanyResponse getCompanyInfo(@RequestHeader("Authorization") String token,
                                   @RequestBody Query query);
}
