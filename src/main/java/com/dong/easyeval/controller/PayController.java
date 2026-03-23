package com.dong.easyeval.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dong.easyeval.common.ApiResponse;
import com.dong.easyeval.config.RocketMq5Properties;
import com.dong.easyeval.dto.PayFinishedMessage;
import com.dong.easyeval.entity.TPaymentHistory;
import com.dong.easyeval.entity.TUser;
import com.dong.easyeval.request.PayRequest;
import com.dong.easyeval.service.ITPaymentHistoryService;
import com.dong.easyeval.service.ITUserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.message.Message;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.apache.rocketmq.client.apis.producer.SendReceipt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.NoTransactionException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@RestController
@RequestMapping("api/admin")
public class PayController {

    @Autowired
    private ITUserService userService;
    @Autowired
    private ITPaymentHistoryService paymentHistoryService;
    @Autowired(required = false)
    private Producer rocketMqProducer;
    @Autowired
    private ClientServiceProvider clientServiceProvider;
    @Autowired
    private RocketMq5Properties rocketMq5Properties;
    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping("/pay")
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse pay(@RequestBody PayRequest payRequest) {
        if (Objects.isNull(payRequest)
                || StringUtils.isBlank(payRequest.getWechatId())
                || Objects.isNull(payRequest.getPaymentAmount())
                || payRequest.getPaymentAmount().compareTo(BigDecimal.valueOf(9.9)) != 0) {
            return ApiResponse.error("鍙傛暟閿欒");
        }

        TUser user = userService.getOne(new QueryWrapper<>(TUser.class).eq("wechat_user_id", payRequest.getWechatId()));
        if (Objects.isNull(user)) {
            return ApiResponse.error("鐢ㄦ埛涓嶅瓨鍦?");
        }

        if (!rocketMq5Properties.isEnabled()) {
            return ApiResponse.error("鏀粯鎴愬姛锛屼絾娑堟伅鍙戦€佹湭鍚敤锛岃鍏堥厤缃?rocketmq5.enabled=true");
        }
        if (Objects.isNull(rocketMqProducer)) {
            return ApiResponse.error("鏀粯鎴愬姛锛屼絾娑堟伅鍙戦€佸け璐ワ細RocketMQ Producer 鏈垵濮嬪寲锛岃妫€鏌?endpoints/topic 閰嶇疆");
        }

        String paymentStatus = StringUtils.defaultIfBlank(payRequest.getPaymentStatus(), "SUCCESS");
        TPaymentHistory paymentHistory = TPaymentHistory.builder()
                .paymentAmount(payRequest.getPaymentAmount())
                .userId(user.getUserId())
                .paymentStatus(paymentStatus)
                .build();
        if (!paymentHistoryService.save(paymentHistory)) {
            return rollbackWithError("鏀粯璁板綍淇濆瓨澶辫触");
        }

        String messageBody;
        try {
            messageBody = objectMapper.writeValueAsString(PayFinishedMessage.builder()
                    .paymentId(paymentHistory.getPaymentId())
                    .userId(user.getUserId())
                    .paymentStatus(paymentStatus)
                    .build());
        } catch (JsonProcessingException e) {
            return rollbackWithError("鏀粯鎴愬姛锛屼絾娑堟伅搴忓垪鍖栧け璐? " + e.getMessage());
        }

        Message message = clientServiceProvider.newMessageBuilder()
                .setTopic(rocketMq5Properties.getTopic())
                .setTag(rocketMq5Properties.getTag())
                .setKeys("pay-" + paymentHistory.getPaymentId())
                .setBody(messageBody.getBytes(StandardCharsets.UTF_8))
                .build();
        try {
            SendReceipt sendReceipt = rocketMqProducer.send(message);
            System.out.println("RocketMQ send success, messageId=" + sendReceipt.getMessageId());
        } catch (ClientException e) {
            return rollbackWithError("鏀粯鎴愬姛锛屼絾娑堟伅鍙戦€佸け璐? " + e.getMessage());
        }

        return ApiResponse.success("鏀粯鎴愬姛");
    }

    private ApiResponse rollbackWithError(String message) {
        try {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        } catch (NoTransactionException ignored) {
        }
        return ApiResponse.error(message);
    }
}
