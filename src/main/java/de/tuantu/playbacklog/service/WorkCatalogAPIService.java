package de.tuantu.playbacklog.service;

import de.tuantu.playbacklog.service.domain.WorkCatalogDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Optional;

@Service
public class WorkCatalogAPIService {

    private final RestClient restClient;
    private final boolean isApiEnabled;

    public WorkCatalogAPIService(
            RestClient restClient,
            @Value("${workcatalog.api.url:NO_API}") String workCatalogAPIUrl) {

        this.restClient = restClient;
        this.isApiEnabled = !workCatalogAPIUrl.equals("NO_API") && !workCatalogAPIUrl.isEmpty();
    }

    public Optional<WorkCatalogDto> getAdditionalData(String isrcCode) {
        if(!isApiEnabled) {
            return Optional.empty();
        }
        try {
            WorkCatalogDto result = restClient.get()
                    .uri("/data/"+isrcCode)
                    .retrieve()
                    // .onstatus -> Errorhandling
                    .body(WorkCatalogDto.class);

            return Optional.ofNullable(result);
        } catch (Exception e) {
            return Optional.empty();
        }

    }
}
