package de.tuantu.playbacklog.service;

import de.tuantu.playbacklog.service.domain.WorkCatalogDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class WorkCatalogAPIService {

    private final RestClient restClient;

    @Value("${workcatalog.api.url:NO_API}")
    private String WorkCatalogAPIUrl;

    public WorkCatalogAPIService() {
        this.restClient = RestClient.create(WorkCatalogAPIUrl);
    }

    public WorkCatalogDto getAdditionalData(String isrcCode) {
        // Dummy wenn WorkCatalogAPIUrl nicht gesetzt ist
        if(WorkCatalogAPIUrl == null || WorkCatalogAPIUrl.isEmpty() || WorkCatalogAPIUrl.equals("NO_API")) {
            return new WorkCatalogDto(
                    "", "", ""
            );
        }
        return restClient.get()
                .uri("/data/"+isrcCode)
                .retrieve()
                .body(WorkCatalogDto.class);
    }
}
