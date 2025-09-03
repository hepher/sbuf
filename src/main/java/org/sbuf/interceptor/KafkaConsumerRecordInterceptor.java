package com.enel.notification.commons.interceptor;

import com.enel.notification.commons.main.LoggingComponent;
import com.enel.notification.commons.model.KafkaMessage;
import com.enel.notification.commons.util.ApplicationContextUtils;
import com.enel.notification.commons.util.LabelUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.RecordInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Slf4j
@Component
public class KafkaConsumerRecordInterceptor extends LoggingComponent<KafkaConsumerRecordInterceptor> implements RecordInterceptor<String, KafkaMessage> {

    private final Map<String, StopWatch> recordStopWatchMap = new ConcurrentHashMap<>();
    private final Function<ConsumerRecord<String, KafkaMessage>, String> recordIdFunction = (record) -> record.topic() + "-" + record.partition() + "-" + record.offset();
    private final Function<ConsumerRecord<String, KafkaMessage>, String> transactionIdFunction = (record) -> {
        if (record.headers().lastHeader(LabelUtils.TRANSACTION_ID) != null) {
            return new String(record.headers().lastHeader(LabelUtils.TRANSACTION_ID).value(), StandardCharsets.UTF_8);
        }

        if (record.key() != null) {
            return record.key();
        }

        return UUID.randomUUID().toString();
    };

    @Override
    public ConsumerRecord<String, KafkaMessage> intercept(ConsumerRecord<String, KafkaMessage> record, Consumer<String, KafkaMessage> consumer) {

        ApplicationContextUtils.setTransactionId(transactionIdFunction.apply(record));

        self.log("Record on topic '{}' with offset '{}' is intercepted", record.topic(), record.offset());

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        recordStopWatchMap.put(recordIdFunction.apply(record), stopWatch);

        return record;
    }

    @Override
    public void failure(ConsumerRecord<String, KafkaMessage> record, Exception exception, Consumer<String, KafkaMessage> consumer) {
        logRecord(record, exception);
    }

    @Override
    public void success(ConsumerRecord<String, KafkaMessage> record, Consumer<String, KafkaMessage> consumer) {
        logRecord(record, null);
    }

    private void logRecord(ConsumerRecord<String, KafkaMessage> record, Exception exception) {
        String recordId = recordIdFunction.apply(record);
        StopWatch stopWatch = recordStopWatchMap.get(recordId);

        Map<String, String> headerMap = new HashMap<>();
        record.headers().forEach(header -> headerMap.put(header.key(), new String(header.value(), StandardCharsets.UTF_8)));

        if (stopWatch != null) {
            stopWatch.stop();

            recordStopWatchMap.remove(recordId);
        }

        if (exception != null) {
            self.logError(LabelUtils.LOG_ERROR_KAFKA_MESSAGE,
                    record.topic(),
                    record.key(),
                    record.offset(),
                    headerMap,
                    record.value().toJson(),
                    ApplicationContextUtils.getExceptionMessage(exception),
                    stopWatch != null ? stopWatch.getTotalTimeMillis() + "ms" : "N/A"
            );
            self.logError("error details {}", ApplicationContextUtils.getExceptionStackTrace(exception));
        } else {
            self.log(LabelUtils.LOG_KAFKA_MESSAGE,
                    record.topic(),
                    record.key(),
                    record.offset(),
                    headerMap,
                    record.value().toJson(),
                    stopWatch != null ? stopWatch.getTotalTimeMillis() + "ms" : "N/A");
        }
    }
}
