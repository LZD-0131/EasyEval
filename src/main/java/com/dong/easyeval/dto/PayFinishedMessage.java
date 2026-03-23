package com.dong.easyeval.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayFinishedMessage {
    private Long paymentId;
    private Long userId;
    private String paymentStatus;
}
