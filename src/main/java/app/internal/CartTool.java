package app.internal;

import java.util.UUID;

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

    @Tool(name = "add_item_to_cart_by_menuId_and_storeKey_from_context",
            description = "Adds an item to the shopping cart. Requires the 'menuId' (the unique identifier for a menu item, not its name), "
                + "the 'storeKey' (the store's **UUID string**, e.g., '02a5f21e-2a11-4eb8-b7d9-40e507f5cde4', not the store's name or any other '_id' field), "
                + "the 'quantity' (as an integer), and the 'userId'.")
    // @Tool(name = "add_item_to_cart_by_menuId_and_storeId_from_context",
    //     description = "장바구니에 상품을 추가합니다. 'menuId'(메뉴 항목의 고유 식별자, 이름 아님), 'storeId'(상점의 고유 식별자, 상점 이름이 아닌 긴 영숫자 문자열), 'quantity'(정수), 'userId'가 필요합니다.")
    public String addItemToCart(String menuId, String storeId, String quantity, String userId) {
        String url = UriComponentsBuilder.fromHttpUrl(itemServiceUrl + "/mcp/cart")
                .queryParam("userId", userId)
                .toUriString();
        log.info("Requesting to add item to cart: menuId={}, storeId={}, quantity={}, userid={} to URL: {}",
            menuId, storeId, quantity, userId, url);

        CartItemRequest requestPayload = CartItemRequest.builder()
                .menuId(UUID.fromString(menuId))
                .storeId(UUID.fromString(storeId))
                .quantity(Long.parseLong(quantity))
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
