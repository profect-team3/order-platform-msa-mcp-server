package app.internal;

import app.config.apiPayload.ApiResponse;
import app.internal.dto.cart.CartItemRequest;
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
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class CartTool {

    private static final Logger log = LoggerFactory.getLogger(CartTool.class);
    private final RestTemplate restTemplate;
    private final String itemServiceUrl;

    public CartTool(RestTemplate restTemplate,
                    @Value("${service.order.url}") String itemServiceUrl) {
        this.restTemplate = restTemplate;
        this.itemServiceUrl = itemServiceUrl;
    }

    @Tool(name = "add_item_to_cart",
            description = "Adds an item to the shopping cart. Requires the 'menuId' (the unique identifier for a menu item, not its name), the 'storeId' (the unique identifier for the store, which is a long alphanumeric string, not the store's name), the 'quantity' (as an integer), and the 'userId'.")
    public String addItemToCart(String menuId, String storeId, int quantity, String userId) {
        String url = UriComponentsBuilder.fromHttpUrl(itemServiceUrl + "/mcp/cart")
                .queryParam("userId", userId)
                .toUriString();
        log.info("Requesting to add item to cart: menuId={}, storeId={}, quantity={}, userid={} to URL: {}",
            menuId, storeId, quantity, userId, url);

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

    @Tool(name = "delete_cart",
            description = "Deletes all items from the user's shopping cart. Requires 'userId'.")
    public String deleteCart(String userId) {
        String url = UriComponentsBuilder.fromHttpUrl(itemServiceUrl + "/mcp/cart")
                .queryParam("userId", userId)
                .toUriString();
        log.info("Requesting to delete cart for userId: {} to URL: {}", userId, url);

        HttpEntity<Void> requestEntity = new HttpEntity<>(new HttpHeaders());

        ParameterizedTypeReference<ApiResponse<String>> responseType =
                new ParameterizedTypeReference<>() {};

        try {
            ResponseEntity<ApiResponse<String>> response = restTemplate.exchange(
                    url,
                    HttpMethod.DELETE,
                    requestEntity,
                    responseType
            );

            ApiResponse<String> apiResponse = response.getBody();

            if (apiResponse != null && Boolean.TRUE.equals(apiResponse.isSuccess())) {
                log.info("Successfully deleted cart for userId: {}. Response: {}", userId, apiResponse.message());
                return "Cart for user " + userId + " has been successfully deleted.";
            } else {
                String errorMessage = (apiResponse != null) ? apiResponse.message() : "No response body";
                log.warn("Failed to delete cart for userId: {}. Message: {}", userId, errorMessage);
                return "Failed to delete cart: " + errorMessage;
            }
        } catch (RestClientException e) {
            log.error("Error while deleting cart for userId: {}: {}", userId, e.getMessage());
            return "Error while deleting cart: " + e.getMessage();
        }
    }
}
