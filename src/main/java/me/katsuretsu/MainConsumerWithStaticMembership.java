package me.katsuretsu;

import org.apache.kafka.clients.consumer.CooperativeStickyAssignor;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.tinylog.Logger;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

public class MainConsumerWithStaticMembership {
    public static void main(String[] args) {
        final Properties properties = new Properties();
        properties.put("bootstrap.servers", "127.0.0.1:9092");
        properties.put("group.id", "java-application");
        properties.put("auto.offset.reset", "earliest");
        properties.put("key.deserializer", StringDeserializer.class.getName());
        properties.put("value.deserializer", StringDeserializer.class.getName());
        properties.put("partition.assignment.strategy", CooperativeStickyAssignor.class.getName());
        properties.put("group.instance.id", "instance-3");
        // properties.put("session.timeout.ms", "3100"); // Kafka Broker Settings needed to be changed

        try (final KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties)) {
            final Thread mainThread = Thread.currentThread();
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                consumer.wakeup();
                try {
                    mainThread.join();
                } catch (final InterruptedException e) {
                    Logger.error(e);
                }
            }));

            consumer.subscribe(List.of("first_topic"));
            while (true) {
                consumer.poll(Duration.ofMillis(500)).forEach(record -> {
                    Logger.info("Key: " + record.key()
                            + ", Value: " + record.value()
                            + ", Partition: " + record.partition()
                            + ", Offset: " + record.offset());
                });
            }
        } catch (final WakeupException e) {
            Logger.info("Shut down the application.");
        } catch (final Exception e) {
            Logger.error(e);
        }
    }
}