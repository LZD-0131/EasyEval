package com.dong.easyeval.config;

import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientConfigurationBuilder;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.StaticSessionCredentialsProvider;
import org.apache.rocketmq.client.apis.consumer.FilterExpression;
import org.apache.rocketmq.client.apis.consumer.FilterExpressionType;
import org.apache.rocketmq.client.apis.consumer.MessageListener;
import org.apache.rocketmq.client.apis.consumer.PushConsumer;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
@EnableConfigurationProperties(RocketMq5Properties.class)
public class RocketMq5Config {

    @Bean
    public ClientServiceProvider clientServiceProvider() {
        return ClientServiceProvider.loadService();
    }

    @Bean(destroyMethod = "close")
    @ConditionalOnProperty(prefix = "rocketmq5", name = "enabled", havingValue = "true")
    public Producer rocketMqProducer(ClientServiceProvider provider, RocketMq5Properties properties) throws ClientException {
        if (StringUtils.isBlank(properties.getEndpoints())) {
            throw new IllegalArgumentException("rocketmq5.endpoints 未配置，无法初始化 RocketMQ Producer");
        }
        if (StringUtils.isBlank(properties.getTopic())) {
            throw new IllegalArgumentException("rocketmq5.topic 未配置，无法初始化 RocketMQ Producer");
        }
        ClientConfigurationBuilder configBuilder = ClientConfiguration.newBuilder()
                .setEndpoints(properties.getEndpoints());

        if (StringUtils.isNotBlank(properties.getAccessKey()) && StringUtils.isNotBlank(properties.getSecretKey())) {
            configBuilder.setCredentialProvider(
                    new StaticSessionCredentialsProvider(properties.getAccessKey(), properties.getSecretKey()));
        }
        if (StringUtils.isNotBlank(properties.getNamespace())) {
            configBuilder.setNamespace(properties.getNamespace());
        }

        return provider.newProducerBuilder()
                .setClientConfiguration(configBuilder.build())
                .setTopics(properties.getTopic())
                .build();
    }

    @Bean(destroyMethod = "close")
    @ConditionalOnProperty(prefix = "rocketmq5", name = "enabled", havingValue = "true")
    public PushConsumer rocketMqPushConsumer(ClientServiceProvider provider,
                                             RocketMq5Properties properties,
                                             MessageListener messageListener) throws ClientException {
        if (StringUtils.isBlank(properties.getConsumerGroup())) {
            throw new IllegalArgumentException("rocketmq5.consumer-group 未配置，无法初始化 RocketMQ Consumer");
        }
        ClientConfigurationBuilder configBuilder = ClientConfiguration.newBuilder()
                .setEndpoints(properties.getEndpoints());

        if (StringUtils.isNotBlank(properties.getAccessKey()) && StringUtils.isNotBlank(properties.getSecretKey())) {
            configBuilder.setCredentialProvider(
                    new StaticSessionCredentialsProvider(properties.getAccessKey(), properties.getSecretKey()));
        }
        if (StringUtils.isNotBlank(properties.getNamespace())) {
            configBuilder.setNamespace(properties.getNamespace());
        }

        FilterExpression filterExpression = new FilterExpression(properties.getTag(), FilterExpressionType.TAG);
        return provider.newPushConsumerBuilder()
                .setClientConfiguration(configBuilder.build())
                .setConsumerGroup(properties.getConsumerGroup())
                .setSubscriptionExpressions(Collections.singletonMap(properties.getTopic(), filterExpression))
                .setMessageListener(messageListener)
                .build();
    }
}
