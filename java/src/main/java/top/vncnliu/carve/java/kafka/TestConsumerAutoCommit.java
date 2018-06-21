package top.vncnliu.carve.java.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;

import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;
import java.util.Random;

/**
 * add description<br>
 * created on 2017/11/17<br>
 *
 * @author vncnliu
 * @since default 1.0.0
 */
public class TestConsumerAutoCommit {

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put("bootstrap.servers", "192.168.1.184:9092");
        props.put("group.id", "test-group");
        props.put("enable.auto.commit", "true");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        TopicPartition topicPartition = new TopicPartition("test-topic",0);
        //consumer.subscribe(Arrays.asList("test-topic"));
        consumer.assign(Collections.singletonList(topicPartition));
        System.out.printf("beginningOffsets:%s\n",consumer.beginningOffsets(Collections.singleton(topicPartition)));
        System.out.printf("endOffsets:%s\n",consumer.endOffsets(Collections.singleton(topicPartition)));
        consumer.seek(topicPartition,0);

        boolean running = true;
        while (running) {
            ConsumerRecords<String, String> records = consumer.poll(10);
            Random random = new Random();
            int randomRecord = random.nextInt(10);
            System.out.printf("randomRecord:%s\n",randomRecord);
            if(records.count()!=0){
                for (ConsumerRecord<String, String> record : records) {
                    System.out.printf("key:%s\n",record.key());
                    if(randomRecord==Integer.parseInt(record.key())){
                        running = false;
                        break;
                    }
                }
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
