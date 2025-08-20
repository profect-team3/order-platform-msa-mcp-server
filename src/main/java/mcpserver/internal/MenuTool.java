package mcpserver.internal;

import mcpserver.config.apiPayload.ApiResponse;
import mcpserver.internal.dto.MenuInfo;
import mcpserver.internal.dto.store.MenuCollection;
import mcpserver.internal.dto.store.StoreCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MenuTool {

    private static final Logger log = LoggerFactory.getLogger(MenuTool.class);
    private final RestTemplate restTemplate;
    private final String storeServiceUrl;

    public MenuTool(RestTemplate restTemplate,
                                    @Value("${service.store.url}") String storeServiceUrl) {
        this.restTemplate = restTemplate;
        this.storeServiceUrl = storeServiceUrl;
    }

    @Tool(name = "get_menus_by_store_id",
            description = "Fetches a list of menus for a specific store, identified by its ID. "
                    + "The method requires the 'storeId' as a parameter.")
    public List<MenuInfo> getMenusByStoreId(String storeId) {
        String url = UriComponentsBuilder.fromHttpUrl(storeServiceUrl)
            .path("/mongo/stores/{storeId}")
            .buildAndExpand(storeId)
            .toUriString();
        log.info("Requesting store details for storeId: {} from URL: {}", storeId, url);

        ParameterizedTypeReference<ApiResponse<StoreCollection>> responseType =
            new ParameterizedTypeReference<>() {};
        try {
            ResponseEntity<ApiResponse<StoreCollection>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                responseType
            );
            ApiResponse<StoreCollection> apiResponse = response.getBody();

            if (apiResponse != null && Boolean.TRUE.equals(apiResponse.isSuccess())) {
                StoreCollection store = apiResponse.result();

                if (store != null && store.getMenus() != null) {
                    log.info("Successfully fetched {} menus for storeId: {}", store.getMenus().size(), storeId);
                    return store.getMenus().stream()
                            .map(menu -> MenuInfo.builder()
                                    .menuId(menu.getMenuId())
                                    .menuName(menu.getName())
                                    .price(menu.getPrice())
                                    .description(menu.getDescription())
                                    .build())
                            .collect(Collectors.toList());
                }
            }
            log.warn("Fetched a null or empty response for storeId: {}", storeId);
            return Collections.emptyList();
        } catch (RestClientException e) {
            log.error("Failed to fetch menus for storeId: {}. Error: {}", storeId, e.getMessage());
            return Collections.emptyList();
        }
    }
}
