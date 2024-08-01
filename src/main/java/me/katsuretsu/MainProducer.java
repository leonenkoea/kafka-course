package me.katsuretsu;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.tinylog.Logger;

import java.util.Properties;

public class MainProducer {
    public static void main(String[] args) {
        final Properties properties = new Properties();
        properties.put("bootstrap.servers", "127.0.0.1:9092");
        properties.put("key.serializer", StringSerializer.class.getName());
        properties.put("value.serializer", StringSerializer.class.getName());

        try (final KafkaProducer<String, String> producer = new KafkaProducer<>(properties);) {
            final ProducerRecord<String, String> record = new ProducerRecord<>("first_topic", "hello world!");
            Logger.info("Sending message...");
            producer.send(record);
            producer.flush();
        }
    }
}