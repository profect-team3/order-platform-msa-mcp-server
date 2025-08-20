package mcpserver.internal;

import mcpserver.config.apiPayload.ApiResponse;
import mcpserver.internal.dto.cart.CartItemRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class CartTool {

    private static final Logger log = LoggerFactory.getLogger(CartTool.class);
    private final RestTemplate restTemplate;
    private final String itemServiceUrl;

    public CartTool(RestTemplate restTemplate,
                    @Value("${service.item.url}") String itemServiceUrl) {
        this.restTemplate = restTemplate;
        this.itemServiceUrl = itemServiceUrl;
    }

    @Tool(name = "add_item_to_cart",
            description = "Adds an item to the shopping cart. Requires 'menuId', 'storeId', and 'quantity'.")
    public String addItemToCart(String menuId, String storeId, int quantity) {
        String url = itemServiceUrl + "/item";
        log.info("Requesting to add item to cart: menuId={}, storeId={}, quantity={} to URL: {}",
                menuId, storeId, quantity, url);

        CartItemRequest requestPayload = CartItemRequest.builder()
                .menuId(menuId)
                .storeId(storeId)
                .quantity(quantity)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CartItemRequest> requestEntity = new HttpEntity<>(requestPayload, headers);

        ParameterizedTypeReference<ApiResponse<String>> responseType =
                new ParameterizedTypeReference<>() {};

        try {
            ResponseEntity<ApiResponse<String>> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    responseType
            );

            ApiResponse<String> apiResponse = response.getBody();

            if (apiResponse != null && Boolean.TRUE.equals(apiResponse.isSuccess())) {
                log.info("Successfully added item to cart. Response: {}", apiResponse.message());
                return apiResponse.result();
            } else {
                String errorMessage = (apiResponse != null) ? apiResponse.message() : "No response body";
                log.warn("Failed to add item to cart. Message: {}", errorMessage);
                return "Failed to add item to cart: " + errorMessage;
            }
        } catch (RestClientException e) {
            log.error("Error while adding item to cart: {}", e.getMessage());
            return "Error while adding item to cart: " + e.getMessage();
        }
    }
}
