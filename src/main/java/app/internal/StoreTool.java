package app.internal;

import app.config.apiPayload.ApiResponse;
import app.internal.dto.StoreInfo;
import app.internal.dto.store.StoreCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StoreTool {

    private static final Logger log = LoggerFactory.getLogger(StoreTool.class);
    private final RestTemplate restTemplate;
    private final String storeServiceUrl;

    public StoreTool(RestTemplate restTemplate,
                               @Value("${service.store.url}") String storeServiceUrl) {
        this.restTemplate = restTemplate;
        this.storeServiceUrl = storeServiceUrl;
    }

    @Tool(name = "search_stores",
            description = "Searches for stores by a keyword. The method requires the 'keyword' as a parameter.")
    public List<StoreInfo> searchStores(String keyword) {
        String url = storeServiceUrl + "/mongo/stores/search?keyword=" + keyword;
        log.info("Requesting stores with keyword: {} from URL: {}", keyword, url);

        ParameterizedTypeReference<ApiResponse<List<StoreCollection>>> responseType =
                new ParameterizedTypeReference<>() {};
        try {
            ResponseEntity<ApiResponse<List<StoreCollection>>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    responseType
            );
            ApiResponse<List<StoreCollection>> apiResponse = response.getBody();

            if (apiResponse != null && Boolean.TRUE.equals(apiResponse.isSuccess())) {
                List<StoreCollection> stores = apiResponse.result();
                if (stores != null) {
                    System.out.println(stores.toString());
                    log.info("Successfully fetched {} stores for keyword: {}", stores.size(), keyword);
                    stores.forEach(store -> log.info("  - Store Info: storeKey={}, storeName='{}'", store.getStoreKey(), store.getStoreName()));
                    return stores.stream()
                            .map(store -> StoreInfo.builder()
                                    .storeKey(store.getStoreKey())
                                    .storeName(store.getStoreName())
                                    .description(store.getDescription())
                                    .address(store.getAddress())
                                    .build())
                            .collect(Collectors.toList());
                }
            }
            log.warn("Fetched a null or empty response for keyword: {}", keyword);
            return Collections.emptyList();
        } catch (RestClientException e) {
            log.error("Failed to fetch stores for keyword: {}. Error: {}", keyword, e.getMessage());
            return Collections.emptyList();
        }
    }
}
