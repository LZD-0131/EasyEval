package com.dong.easyeval.consumer;

import com.dong.easyeval.dto.PayFinishedMessage;
import com.dong.easyeval.entity.TCoupon;
import com.dong.easyeval.service.ITCouponService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.rocketmq.client.apis.consumer.ConsumeResult;
import org.apache.rocketmq.client.apis.consumer.MessageListener;
import org.apache.rocketmq.client.apis.message.MessageView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

@Component
public class MessageConsumer implements MessageListener {

    private static final Logger log = LoggerFactory.getLogger(MessageConsumer.class);

    @Autowired
    private ITCouponService couponService;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public ConsumeResult consume(MessageView messageView) {
        try {
            String messageBody = StandardCharsets.UTF_8.decode(messageView.getBody()).toString();
            PayFinishedMessage payFinishedMessage = objectMapper.readValue(messageBody, PayFinishedMessage.class);
            log.info("Receive pay finished message, messageId={}, body={}",
                    messageView.getMessageId(), messageBody);
            if (payFinishedMessage.getPaymentId() == null || payFinishedMessage.getUserId() == null) {
                log.warn("Ignore invalid pay finished message, missing paymentId or userId, body={}", messageBody);
                return ConsumeResult.FAILURE;
            }
            if (!"SUCCESS".equalsIgnoreCase(payFinishedMessage.getPaymentStatus())) {
                log.warn("Ignore pay finished message with non-success status, paymentId={}, paymentStatus={}",
                        payFinishedMessage.getPaymentId(), payFinishedMessage.getPaymentStatus());
                return ConsumeResult.SUCCESS;
            }

            String couponCode = "PAY-" + payFinishedMessage.getPaymentId();
            try {
                boolean saved = couponService.save(TCoupon.builder()
                        .userId(payFinishedMessage.getUserId())
                        .couponType(1L)
                        .expirationDate(LocalDate.now().plusMonths(6))
                        .couponCode(couponCode)
                        .couponStatus("UNUSED")
                        .couponValue(new BigDecimal(20))
                        .build());
                if (!saved) {
                    log.error("Coupon issue failed to persist, paymentId={}, userId={}, couponCode={}",
                            payFinishedMessage.getPaymentId(), payFinishedMessage.getUserId(), couponCode);
                    return ConsumeResult.FAILURE;
                }
            } catch (DuplicateKeyException duplicateKeyException) {
                log.info("Skip duplicate coupon issue, paymentId={}, couponCode={}",
                        payFinishedMessage.getPaymentId(), couponCode);
                return ConsumeResult.SUCCESS;
            }

            log.info("Coupon issued successfully, paymentId={}, userId={}, couponCode={}",
                    payFinishedMessage.getPaymentId(), payFinishedMessage.getUserId(), couponCode);
            return ConsumeResult.SUCCESS;
        } catch (Exception exception) {
            log.error("Consume pay finished message failed", exception);
            return ConsumeResult.FAILURE;
        }
    }
}
