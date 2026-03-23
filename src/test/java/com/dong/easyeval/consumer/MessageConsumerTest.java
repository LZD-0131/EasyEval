package com.dong.easyeval.consumer;

import com.dong.easyeval.entity.TCoupon;
import com.dong.easyeval.service.ITCouponService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.rocketmq.client.apis.consumer.ConsumeResult;
import org.apache.rocketmq.client.apis.message.MessageId;
import org.apache.rocketmq.client.apis.message.MessageView;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MessageConsumerTest {

    @Test
    void consumeShouldBeIdempotentForSamePayment() {
        MessageConsumer consumer = new MessageConsumer();
        ITCouponService couponService = mock(ITCouponService.class);
        ReflectionTestUtils.setField(consumer, "couponService", couponService);
        ReflectionTestUtils.setField(consumer, "objectMapper", new ObjectMapper());

        when(couponService.save(any(TCoupon.class)))
                .thenReturn(true)
                .thenThrow(new DuplicateKeyException("duplicate coupon"));

        MessageView messageView = new StubMessageView("{\"paymentId\":1001,\"userId\":1,\"paymentStatus\":\"SUCCESS\"}");

        ConsumeResult firstConsume = consumer.consume(messageView);
        ConsumeResult secondConsume = consumer.consume(messageView);

        assertEquals(ConsumeResult.SUCCESS, firstConsume);
        assertEquals(ConsumeResult.SUCCESS, secondConsume);
        verify(couponService, times(2)).save(any(TCoupon.class));
    }

    @Test
    void consumeShouldSkipNonSuccessPaymentStatus() {
        MessageConsumer consumer = new MessageConsumer();
        ITCouponService couponService = mock(ITCouponService.class);
        ReflectionTestUtils.setField(consumer, "couponService", couponService);
        ReflectionTestUtils.setField(consumer, "objectMapper", new ObjectMapper());

        MessageView messageView = new StubMessageView("{\"paymentId\":1002,\"userId\":1,\"paymentStatus\":\"FAILED\"}");

        ConsumeResult consumeResult = consumer.consume(messageView);

        assertEquals(ConsumeResult.SUCCESS, consumeResult);
        verify(couponService, times(0)).save(any(TCoupon.class));
    }

    private static class StubMessageView implements MessageView {
        private final ByteBuffer body;

        private StubMessageView(String messageBody) {
            this.body = ByteBuffer.wrap(messageBody.getBytes(StandardCharsets.UTF_8));
        }

        @Override
        public MessageId getMessageId() {
            return null;
        }

        @Override
        public String getTopic() {
            return "PAY_FINISH";
        }

        @Override
        public ByteBuffer getBody() {
            return body.asReadOnlyBuffer();
        }

        @Override
        public Map<String, String> getProperties() {
            return Collections.emptyMap();
        }

        @Override
        public Optional<String> getTag() {
            return Optional.of("PAY_FINISHED");
        }

        @Override
        public Collection<String> getKeys() {
            return Collections.singletonList("pay-1001");
        }

        @Override
        public Optional<String> getMessageGroup() {
            return Optional.empty();
        }

        @Override
        public Optional<Long> getDeliveryTimestamp() {
            return Optional.empty();
        }

        @Override
        public String getBornHost() {
            return "127.0.0.1";
        }

        @Override
        public long getBornTimestamp() {
            return System.currentTimeMillis();
        }

        @Override
        public int getDeliveryAttempt() {
            return 1;
        }
    }
}
