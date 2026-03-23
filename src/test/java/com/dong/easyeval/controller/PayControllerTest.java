package com.dong.easyeval.controller;

import com.dong.easyeval.common.ApiResponse;
import com.dong.easyeval.config.RocketMq5Properties;
import com.dong.easyeval.entity.TPaymentHistory;
import com.dong.easyeval.entity.TUser;
import com.dong.easyeval.request.PayRequest;
import com.dong.easyeval.service.ITPaymentHistoryService;
import com.dong.easyeval.service.ITUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.message.MessageId;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.apache.rocketmq.client.apis.producer.SendReceipt;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PayControllerTest {

    @Test
    void payShouldSendMessageAfterSavingHistory() throws ClientException {
        PayController controller = new PayController();
        ITUserService userService = mock(ITUserService.class);
        ITPaymentHistoryService paymentHistoryService = mock(ITPaymentHistoryService.class);
        Producer producer = mock(Producer.class);
        SendReceipt sendReceipt = mock(SendReceipt.class);
        MessageId messageId = mock(MessageId.class);
        RocketMq5Properties properties = new RocketMq5Properties();
        properties.setEnabled(true);
        properties.setTopic("PAY_FINISH");
        properties.setTag("PAY_FINISHED");

        ReflectionTestUtils.setField(controller, "userService", userService);
        ReflectionTestUtils.setField(controller, "paymentHistoryService", paymentHistoryService);
        ReflectionTestUtils.setField(controller, "rocketMqProducer", producer);
        ReflectionTestUtils.setField(controller, "clientServiceProvider", ClientServiceProvider.loadService());
        ReflectionTestUtils.setField(controller, "rocketMq5Properties", properties);
        ReflectionTestUtils.setField(controller, "objectMapper", new ObjectMapper());

        when(userService.getOne(any())).thenReturn(TUser.builder().userId(1L).wechatUserId("wx-open-id").build());
        doAnswer(invocation -> {
            TPaymentHistory history = invocation.getArgument(0);
            history.setPaymentId(10L);
            return true;
        }).when(paymentHistoryService).save(any(TPaymentHistory.class));
        when(messageId.toString()).thenReturn("mid-1");
        when(sendReceipt.getMessageId()).thenReturn(messageId);
        when(producer.send(any())).thenReturn(sendReceipt);

        PayRequest payRequest = new PayRequest();
        payRequest.setWechatId("wx-open-id");
        payRequest.setPaymentAmount(BigDecimal.valueOf(9.9));
        payRequest.setPaymentStatus("SUCCESS");

        ApiResponse response = controller.pay(payRequest);

        assertEquals(200, response.getStatus());
        verify(paymentHistoryService).save(any(TPaymentHistory.class));
        verify(producer).send(any());
    }

    @Test
    void payShouldFailFastWhenRocketMqIsDisabled() {
        PayController controller = new PayController();
        ITUserService userService = mock(ITUserService.class);
        ITPaymentHistoryService paymentHistoryService = mock(ITPaymentHistoryService.class);
        RocketMq5Properties properties = new RocketMq5Properties();
        properties.setEnabled(false);

        ReflectionTestUtils.setField(controller, "userService", userService);
        ReflectionTestUtils.setField(controller, "paymentHistoryService", paymentHistoryService);
        ReflectionTestUtils.setField(controller, "clientServiceProvider", ClientServiceProvider.loadService());
        ReflectionTestUtils.setField(controller, "rocketMq5Properties", properties);
        ReflectionTestUtils.setField(controller, "objectMapper", new ObjectMapper());

        when(userService.getOne(any())).thenReturn(TUser.builder().userId(1L).wechatUserId("wx-open-id").build());

        PayRequest payRequest = new PayRequest();
        payRequest.setWechatId("wx-open-id");
        payRequest.setPaymentAmount(BigDecimal.valueOf(9.9));

        ApiResponse response = controller.pay(payRequest);

        assertEquals(500, response.getStatus());
        verify(paymentHistoryService, never()).save(any(TPaymentHistory.class));
    }

    @Test
    void payShouldReturnErrorWhenSendFails() throws ClientException {
        PayController controller = new PayController();
        ITUserService userService = mock(ITUserService.class);
        ITPaymentHistoryService paymentHistoryService = mock(ITPaymentHistoryService.class);
        Producer producer = mock(Producer.class);
        RocketMq5Properties properties = new RocketMq5Properties();
        properties.setEnabled(true);
        properties.setTopic("PAY_FINISH");
        properties.setTag("PAY_FINISHED");

        ReflectionTestUtils.setField(controller, "userService", userService);
        ReflectionTestUtils.setField(controller, "paymentHistoryService", paymentHistoryService);
        ReflectionTestUtils.setField(controller, "rocketMqProducer", producer);
        ReflectionTestUtils.setField(controller, "clientServiceProvider", ClientServiceProvider.loadService());
        ReflectionTestUtils.setField(controller, "rocketMq5Properties", properties);
        ReflectionTestUtils.setField(controller, "objectMapper", new ObjectMapper());

        when(userService.getOne(any())).thenReturn(TUser.builder().userId(1L).wechatUserId("wx-open-id").build());
        doAnswer(invocation -> {
            TPaymentHistory history = invocation.getArgument(0);
            history.setPaymentId(11L);
            return true;
        }).when(paymentHistoryService).save(any(TPaymentHistory.class));
        when(producer.send(any())).thenThrow(mock(ClientException.class));

        PayRequest payRequest = new PayRequest();
        payRequest.setWechatId("wx-open-id");
        payRequest.setPaymentAmount(BigDecimal.valueOf(9.9));
        payRequest.setPaymentStatus("SUCCESS");

        ApiResponse response = controller.pay(payRequest);

        assertEquals(500, response.getStatus());
        verify(paymentHistoryService).save(any(TPaymentHistory.class));
        verify(producer).send(any());
    }
}
