package mcpserver.internal;

import mcpserver.config.PagedResponse;
import mcpserver.internal.dto.ClientMenuInfo;
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

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class InternalGetMenuByStoreId {

    private static final Logger log = LoggerFactory.getLogger(InternalGetMenuByStoreId.class);
    private final RestTemplate restTemplate;
    private final String storeServiceUrl;

    public InternalGetMenuByStoreId(RestTemplate restTemplate,
                                    @Value("${service.store.url}") String storeServiceUrl) {
        this.restTemplate = restTemplate;
        this.storeServiceUrl = storeServiceUrl;
    }

    @Tool(name = "get_menus_by_store_id",
            description = "Fetches a list of menus for a specific store, identified by its ID. "
                    + "The method requires the 'storeId' as a parameter.")
    public List<ClientMenuInfo> getMenusByStoreId(String storeId) {
        String url = storeServiceUrl + "/menu/" + storeId;
        log.info("Requesting menus for storeId: {} from URL: {}", storeId, url);
        try {
            ParameterizedTypeReference<PagedResponse<ClientMenuInfo>> responseType = new ParameterizedTypeReference<>() {};

            ResponseEntity<PagedResponse<ClientMenuInfo>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                responseType
            );
            PagedResponse<ClientMenuInfo> pagedResponse = response.getBody();
            if (pagedResponse.getContent() != null) {
                log.info("Successfully fetched {} menus for storeId: {}", pagedResponse.getContent().size(), storeId);
                return pagedResponse.getContent();
            } else {
                log.warn("Fetched a null or empty response for storeId: {}", storeId);
                return Collections.emptyList();
            }
        } catch (RestClientException e) {
            log.error("Failed to fetch menus for storeId: {}. Error: {}", storeId, e.getMessage());
            return Collections.emptyList();
        }
    }
}
