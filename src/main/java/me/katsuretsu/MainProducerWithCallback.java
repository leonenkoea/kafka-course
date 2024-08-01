package me.katsuretsu;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RoundRobinPartitioner;
import org.apache.kafka.common.serialization.StringSerializer;
import org.tinylog.Logger;

import java.util.Properties;

public class MainProducerWithCallback {
    public static void main(String[] args) {
        final Properties properties = new Properties();
        properties.put("bootstrap.servers", "127.0.0.1:9092");
        properties.put("key.serializer", StringSerializer.class.getName());
        properties.put("value.serializer", StringSerializer.class.getName());

//        properties.put("partitioner.class", RoundRobinPartitioner.class.getName());

        try (final KafkaProducer<String, String> producer = new KafkaProducer<>(properties);) {
            for (int i = 0; i < 10; i++) {
                final ProducerRecord<String, String> record = new ProducerRecord<>("first_topic", "hello world! " + i + " time(s)");
                Logger.info("Sending message...");
                producer.send(record, (metadata, exception) -> {
                    if (exception == null) {
                        Logger.info("Message sent successfully"
                                + " Topic = " + metadata.topic()
                                + ", Partition = " + metadata.partition()
                                + ", Offset = " + metadata.offset()
                                + ", Timestamp = " + metadata.timestamp());
                    } else {
                        Logger.error("Error sending message", exception);
                    }
                });
            }
            producer.flush();
        }
    }
}