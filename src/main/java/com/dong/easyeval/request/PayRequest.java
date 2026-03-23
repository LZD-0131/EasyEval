package com.dong.easyeval.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;
@Data
public class PayRequest {
    @JsonProperty("wechat_id")
    private String wechatId;
    @JsonProperty("payment_date")
    private Date paymentDate;
    @JsonProperty("payment_amount")
    private BigDecimal paymentAmount;
    @JsonProperty("payment_status")
    private String paymentStatus;
}
