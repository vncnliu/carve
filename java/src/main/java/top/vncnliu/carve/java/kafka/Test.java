package top.vncnliu.carve.java.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Arrays;
import java.util.Properties;

/**
 * add description<br>
 * created on 2017/11/17<br>
 *
 * @author vncnliu
 * @since default 1.0.0
 */
public class Test {

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put("bootstrap.servers", "192.168.88.43:9092,192.168.88.43:9093");
        props.put("group.id", "test");
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList("quotes-1"));

        /*TopicPartition topicPartition = new TopicPartition("my-partitions-topic",0);
        consumer.assign(Collections.singletonList(topicPartition));*/
        int sum=0;
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(10);
            if(records.count()!=0){
                sum+=records.count();
                System.out.println(sum);
                System.out.println(consumer.assignment());
                System.out.printf("execute time:%d, size:%d\n",System.currentTimeMillis(),records.count());
            }
            /*for (ConsumerRecord<String, String> record : records){
                System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), (System.currentTimeMillis()-Long.parseLong(record.value())));
            }*/
        }
    }
}
