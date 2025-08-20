package app.internal.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import app.internal.dto.order.enums.OrderChannel;
import app.internal.dto.order.enums.PaymentMethod;
import app.internal.dto.order.enums.ReceiptMethod;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    private PaymentMethod paymentMethod;
    private OrderChannel orderChannel;
    private ReceiptMethod receiptMethod;
    private String requestMessage;
    private int totalPrice;
    private String deliveryAddress;
}
