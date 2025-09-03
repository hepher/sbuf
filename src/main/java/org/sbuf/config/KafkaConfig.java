package com.enel.notification.commons.config;

import com.enel.notification.commons.interceptor.KafkaConsumerRecordInterceptor;
import com.enel.notification.commons.main.LoggingComponent;
import com.enel.notification.commons.model.KafkaMessage;
import com.enel.notification.commons.property.KafkaProperties;
import com.enel.notification.commons.util.LabelUtils;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.support.ExponentialBackOffWithMaxRetries;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.time.Duration;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConfig extends LoggingComponent<KafkaConfig> {

    @Autowired
    private KafkaProperties kafkaProperties;

    @Bean
    @ConditionalOnExpression("${commons.kafka.consumer.enabled:false}")
    public ConsumerFactory<String, KafkaMessage> consumerFactory() {
        Map<String, Object> props = new HashMap<>();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.getConsumer().getGroupId());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, StringUtils.defaultIfBlank(kafkaProperties.getConsumer().getAutoOffsetReset(), "earliest"));
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        JsonDeserializer<KafkaMessage> jsonDeserializer = new JsonDeserializer<>(KafkaMessage.class, objectMapper);
        jsonDeserializer.addTrustedPackages("*");
        jsonDeserializer.setTypeFunction((bytes, headers) -> {
            try {
                return TypeFactory.defaultInstance().constructFromCanonical(new String(headers.lastHeader(LabelUtils.KAFKA_MESSAGE_CLASS).value()));
            } catch (Exception e) {
                return TypeFactory.defaultInstance().constructFromCanonical(KafkaMessage.class.getCanonicalName());
            }
        });

        props.put("security.protocol", kafkaProperties.getProperties().getSecurityProtocol());

        try {
            self.log("Configuring SSL properties for consumer factory:");
            self.log("  security.protocol: {}", kafkaProperties.getProperties().getSecurityProtocol());

            File truststoreFile = loadTruststoreFile();
            props.put("ssl.truststore.location", truststoreFile.getAbsolutePath());
            props.put("ssl.truststore.password", kafkaProperties.getProperties().getSslTruststorePwd());
            self.log("ssl.truststore.location: {}", truststoreFile.getAbsolutePath());

            File keystoreFile = decodeBase64ToTempFile(kafkaProperties.getProperties().getSslKeystore(), "kafka-keystore-consumer");
            props.put("ssl.keystore.location", keystoreFile.getAbsolutePath());
            props.put("ssl.keystore.password", kafkaProperties.getProperties().getSslKeystorePwd());
            props.put("ssl.key.password", kafkaProperties.getProperties().getSslKeyPwd());
            self.log("ssl.keystore.location (temp file): {}", keystoreFile.getAbsolutePath());

            self.log("Consumer SSL configured: truststore={}, keystore={}", props.get("ssl.truststore.location"), keystoreFile.getAbsolutePath());

        } catch (Exception e) {
            self.log("Error in Kafka SSL configuration (Consumer): {}", e.getMessage());
            throw new RuntimeException("Error during Kafka SSL configuration (Consumer)", e);
        }

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), jsonDeserializer);
    }

    @Bean
    @ConditionalOnExpression("${commons.kafka.producer.enabled:false}")
    public ProducerFactory<String, KafkaMessage> producerFactory() {
        Map<String, Object> props = new HashMap<>();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class.getName());
        props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);

        props.put("security.protocol", kafkaProperties.getProperties().getSecurityProtocol());

        try {
            File truststoreFile = loadTruststoreFile();
            props.put("ssl.truststore.location", truststoreFile.getAbsolutePath());
            props.put("ssl.truststore.password", kafkaProperties.getProperties().getSslTruststorePwd());

            File keystoreFile = decodeBase64ToTempFile(kafkaProperties.getProperties().getSslKeystore(), "kafka-keystore-producer");
            props.put("ssl.keystore.location", keystoreFile.getAbsolutePath());
            props.put("ssl.keystore.password", kafkaProperties.getProperties().getSslKeystorePwd());
            props.put("ssl.key.password", kafkaProperties.getProperties().getSslKeyPwd());
        } catch (Exception e) {
            throw new RuntimeException("Error during Kafka SSL configuration (Producer)", e);
        }

        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    @ConditionalOnExpression("${commons.kafka.producer.enabled:false}")
    public KafkaTemplate<String, KafkaMessage> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    @ConditionalOnExpression("${commons.kafka.consumer.enabled:false}")
    public ConcurrentKafkaListenerContainerFactory<String, KafkaMessage> kafkaListenerContainerFactory(KafkaConsumerRecordInterceptor kafkaConsumerRecordInterceptor) {
        ConcurrentKafkaListenerContainerFactory<String, KafkaMessage> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setRecordInterceptor(kafkaConsumerRecordInterceptor);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.valueOf(kafkaProperties.getConsumer().getListener().getAckMode()));
        ExponentialBackOffWithMaxRetries bo = new ExponentialBackOffWithMaxRetries(kafkaProperties.getConsumer().getListener().getMaxRetries());
        bo.setInitialInterval(kafkaProperties.getConsumer().getListener().getInitialInterval());
        bo.setMultiplier(kafkaProperties.getConsumer().getListener().getMultiplier());
        bo. setMaxInterval(kafkaProperties.getConsumer().getListener().getMaxInterval());
        factory. setCommonErrorHandler(new DefaultErrorHandler(bo));
        factory.getContainerProperties().setAuthExceptionRetryInterval(Duration.ofSeconds(kafkaProperties.getConsumer().getListener().getAuthExceptionRetryInterval()));
        factory.getContainerProperties().setDeliveryAttemptHeader(true);
        return factory;
    }

    /**
     * Load truststore from classpath or absolute path
     */
    private File loadTruststoreFile() throws Exception {
        String truststoreLocation = kafkaProperties.getProperties().getSslTruststoreLocation();

        if (truststoreLocation == null || truststoreLocation.isEmpty()) {
            throw new IllegalArgumentException("The sslTruststoreLocation property cannot be null or empty.");
        }

        if (truststoreLocation.startsWith("classpath:")) {
            String resourcePath = truststoreLocation.substring("classpath:".length());
            File truststoreFile = File.createTempFile("kafka-truststore-", ".jks");
            truststoreFile.deleteOnExit();

            try (InputStream is = new ClassPathResource(resourcePath).getInputStream();
                    FileOutputStream fos = new FileOutputStream(truststoreFile)) {
                is.transferTo(fos);
            }
            self.log("Truststore loaded from classpath to temporary file: {}", truststoreFile.getAbsolutePath());
            return truststoreFile;
        } else {
            File file = new File(truststoreLocation);
            if (!file.exists()) {
                throw new IllegalArgumentException("Truststore file not found: " + truststoreLocation);
            }
            self.log("Truststore loaded from file system: {}", truststoreLocation);
            return file;
        }
    }

    /**
     * Decodes a Base64 string and saves it to a temporary file.
     */
    private File decodeBase64ToTempFile(String base64String, String prefix) throws Exception {
        if (base64String == null || base64String.isEmpty()) {
            throw new IllegalArgumentException("The Base64 string for the keystore cannot be null or empty.");
        }

        byte[] decodedBytes = Base64.getDecoder().decode(base64String);
        File tempFile = File.createTempFile(prefix, ".jks");
        tempFile.deleteOnExit();

        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(decodedBytes);
        }

        self.log("Temporary keystore file created: {}", tempFile.getAbsolutePath());
        return tempFile;
    }
}
