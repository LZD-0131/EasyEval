package com.dong.easyeval.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CouponResp {
    private String couponId;
    private String couponCode;
    private String couponType;
    private BigDecimal couponValue;
    private LocalDate expirationDate;
    private String couponStatus;
    private String userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdBy;
    private Long updatedBy;
    private String typeId;
    private String typeName;
    private String typeDescription;
    private String typeProperties;
}
