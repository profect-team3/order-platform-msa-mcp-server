package mcpserver.internal;

import mcpserver.config.apiPayload.ApiResponse;
import mcpserver.internal.dto.order.enums.OrderChannel;
import mcpserver.internal.dto.order.OrderRequest;
import mcpserver.internal.dto.order.enums.PaymentMethod;
import mcpserver.internal.dto.order.enums.ReceiptMethod;
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
public class OrderTool {

    private static final Logger log = LoggerFactory.getLogger(OrderTool.class);
    private final RestTemplate restTemplate;
    private final String orderServiceUrl;

    public OrderTool(RestTemplate restTemplate,
                     @Value("${service.item.url}") String orderServiceUrl) {
        this.restTemplate = restTemplate;
        this.orderServiceUrl = orderServiceUrl;
    }

    @Tool(name = "place_order",
            description = "Places an order based on the user's cart. "
                    + "Requires paymentMethod, orderChannel, receiptMethod, totalPrice, and deliveryAddress.")
    public String placeOrder(
            PaymentMethod paymentMethod,
            OrderChannel orderChannel,
            ReceiptMethod receiptMethod,
            String requestMessage,
            int totalPrice,
            String deliveryAddress) {

        String url = orderServiceUrl + "/";
        log.info("Requesting to place an order to URL: {}", url);

        OrderRequest requestPayload = OrderRequest.builder()
                .paymentMethod(paymentMethod)
                .orderChannel(orderChannel)
                .receiptMethod(receiptMethod)
                .requestMessage(requestMessage)
                .totalPrice(totalPrice)
                .deliveryAddress(deliveryAddress)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<OrderRequest> requestEntity = new HttpEntity<>(requestPayload, headers);

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
                log.info("Successfully placed order. Response: {}", apiResponse.result());
                return "Order placed successfully. Order ID: " + apiResponse.result();
            } else {
                String errorMessage = (apiResponse != null) ? apiResponse.message() : "No response body";
                log.warn("Failed to place order. Message: {}", errorMessage);
                return "Failed to place order: " + errorMessage;
            }
        } catch (RestClientException e) {
            log.error("Error while placing order: {}", e.getMessage());
            return "Error while placing order: " + e.getMessage();
        }
    }
}
