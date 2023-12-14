package dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class OrderDetailsDto {
    private String orderId;
    private String code;
    private int qty;
    private double unitPrice;
}
