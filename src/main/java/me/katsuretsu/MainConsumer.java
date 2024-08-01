package me.katsuretsu;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.tinylog.Logger;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

public class MainConsumer {
    public static void main(String[] args) {
        final Properties properties = new Properties();
        properties.put("bootstrap.servers", "127.0.0.1:9092");
        properties.put("group.id", "java-application");
        properties.put("auto.offset.reset", "earliest");
        properties.put("key.deserializer", StringDeserializer.class.getName());
        properties.put("value.deserializer", StringDeserializer.class.getName());

        try (final KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties)) {
            consumer.subscribe(List.of("first_topic"));
            while (true) {
                Logger.info("Polling for records...");
                consumer.poll(Duration.ofMillis(500)).forEach(record -> {
                    Logger.info("Key: " + record.key()
                            + ", Value: " + record.value()
                            + ", Partition: " + record.partition()
                            + ", Offset: " + record.offset());
                });
            }
        }
    }
}