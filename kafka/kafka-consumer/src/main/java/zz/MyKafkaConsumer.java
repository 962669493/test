package zz;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author zhangzheng
 * @date 2020/8/20
 */
public class MyKafkaConsumer {
    public static final String brokerList="192.168.178.138:9092,192.168.178.146:9092,192.168.178.145:9092";
    public static final String topic="test1";
    public static final String groupId="group.demo";
    public static final AtomicBoolean isRunning=new AtomicBoolean(true);

    public static Properties initConfig(){
        Properties properties = new Properties();
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerList);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.put(ConsumerConfig.CLIENT_ID_CONFIG, "client.id.demo");
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        return properties;
    }

    public static void main(String[] args) {
        offsetForTime();
    }
    public static void general(){
        Properties properties = initConfig();
        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(properties);
        consumer.subscribe(Arrays.asList(topic));
        for(;true;){
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
            for (ConsumerRecord<String, String> record : records) {
                Date date = new Date(record.timestamp());
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                System.out.println("topic = "+record.topic()+", partition = "+record.partition()+", offset = "+record.offset()+", time = "+ simpleDateFormat.format(date));
                System.out.println("key = "+record.key()+", value = "+record.value());
            }
            consumer.commitAsync();
        }
        //consumer.close();
    }

    public static void seek(){
        Properties properties = initConfig();
        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(properties);
        consumer.subscribe(Arrays.asList(topic));
        Set<TopicPartition> assignment = new HashSet<>();
        for(;assignment.size() == 0;){
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
            assignment = consumer.assignment();
        }
        for (TopicPartition topicPartition : assignment) {
            consumer.seek(topicPartition, 0);
        }
        for (;true;){
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
            for (ConsumerRecord<String, String> record : records) {
                Date date = new Date(record.timestamp());
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                System.out.println("topic = "+record.topic()+", partition = "+record.partition()+", offset = "+record.offset()+", time = "+ simpleDateFormat.format(date));
                System.out.println("key = "+record.key()+", value = "+record.value());
            }
            consumer.commitSync();
        }
    }

    public static void offsetForTime(){
        Properties properties = initConfig();
        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(properties);
        consumer.subscribe(Arrays.asList(topic));
        Set<TopicPartition> assignment = new HashSet<>();
        for(;assignment.size() == 0;){
            consumer.poll(Duration.ofMillis(1000));
            assignment = consumer.assignment();
        }
        Map<TopicPartition, Long> timestampTosSearch = new HashMap<>(16);
        for (TopicPartition topicPartition : assignment) {
            timestampTosSearch.put(topicPartition, LocalDateTime.now().minusDays(2L).toInstant(ZoneOffset.of("+8")).toEpochMilli());
        }
        Map<TopicPartition, OffsetAndTimestamp> offsets = consumer.offsetsForTimes(timestampTosSearch);
        for (TopicPartition topicPartition : assignment) {
            OffsetAndTimestamp offsetAndTimestamp = offsets.get(topicPartition);
            if(offsetAndTimestamp != null){
                consumer.seek(topicPartition, offsetAndTimestamp.offset());
            }
        }
        for (;true;){
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
            for (ConsumerRecord<String, String> record : records) {
                Date date = new Date(record.timestamp());
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                System.out.println("topic = "+record.topic()+", partition = "+record.partition()+", offset = "+record.offset()+", time = "+ simpleDateFormat.format(date));
                System.out.println("key = "+record.key()+", value = "+record.value());
            }
            consumer.commitSync();
        }
    }
}
