package top.vncnliu.carve.java.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndTimestamp;
import org.apache.kafka.common.TopicPartition;

import java.util.*;

/**
 * add description<br>
 * created on 2017/11/17<br>
 *
 * @author vncnliu
 * @since default 1.0.0
 */
public class QuotesConsumer {

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put("bootstrap.servers", "192.168.1.184:9092");
        props.put("group.id", "quotes");
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
consumer.commitAsync();

        TopicPartition topicPartition = new TopicPartition("test-topic",0);
        //consumer.subscribe(Arrays.asList("quotes-1"));
        consumer.assign(Collections.singletonList(topicPartition));

        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            long begin = System.currentTimeMillis();
            Map<TopicPartition, Long> timestampsToSearch = new HashMap<>();
            long from  = random.nextInt(990000);
            timestampsToSearch.put(topicPartition, from);
            Map<TopicPartition, OffsetAndTimestamp> offsetsForTimes = consumer.offsetsForTimes(timestampsToSearch);
            OffsetAndTimestamp offsetAndTimestamp = offsetsForTimes.get(topicPartition);
            consumer.seek(topicPartition,offsetAndTimestamp.offset());
            System.err.printf("set seek use time:%d\n",(System.currentTimeMillis()-begin));

            int flag=0;
            while (flag<1000){
                long pollBegin = System.currentTimeMillis();
                ConsumerRecords<String, String> records = consumer.poll(1000);
                /*for (ConsumerRecord<String, String> record : records) {
                    System.out.println(record);
                }*/
                System.out.printf("poll time:%d,count:%d\n",(System.currentTimeMillis()-pollBegin),records.count());
                flag+=records.count();
            }
            System.err.printf("use time:%d\n",(System.currentTimeMillis()-begin));
        }

    }
}
