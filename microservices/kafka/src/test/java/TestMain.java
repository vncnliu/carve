import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import kafka.admin.AdminUtils;
import kafka.admin.RackAwareMode;
import kafka.utils.ZkUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.security.JaasUtils;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Properties;

/**
 * add description<br>
 * created on 2018/2/8<br>
 *
 * @author vncnliu
 * @since default 1.0.0
 */
public class TestMain {
    @Test
    public void main() throws InterruptedException {

        String topicName = "my-replicated-topic";

        ZkUtils zkUtils = ZkUtils.apply("192.168.88.43:2181", 30000, 30000, JaasUtils.isZkSecurityEnabled());
        if(!AdminUtils.topicExists(zkUtils, topicName)){
            // 创建一个单分区单副本名为test1的topic
            AdminUtils.createTopic(zkUtils, topicName, 1, 1, new Properties(), RackAwareMode.Enforced$.MODULE$);
        }
        zkUtils.close();

        Properties producerProps = new Properties();
        producerProps.put("acks", "all");
        producerProps.put("bootstrap.servers", "192.168.88.43:9092");
        producerProps.put("retries", 0);
        producerProps.put("batch.size", 16384);
        producerProps.put("linger.ms", 1);
        producerProps.put("buffer.memory", 33554432);
        producerProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producerProps.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        Producer<String, String> producer = new KafkaProducer<>(producerProps);

        for (int i = 0; i < 100; i++){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("key",i);
            producer.send(new ProducerRecord<>(topicName
                    ,""+i
                    , JSON.toJSONString(jsonObject)));
        }


        //consumerTopic(topicName);

    }

    private void consumerTopic(String topicName) throws InterruptedException {
        Properties props = new Properties();
        props.put("bootstrap.servers", "192.168.88.43:9092");
        props.put("group.id", "quotes");
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);

        /*TopicPartition topicPartition = new TopicPartition("quotes-1",0);
        consumer.assign(Collections.singletonList(topicPartition));*/
        consumer.subscribe(Collections.singletonList(topicName));

        while (true){
            ConsumerRecords<String, String> records = consumer.poll(10);
            for (ConsumerRecord<String, String> record : records) {
                System.out.println(record);
            }
            Thread.sleep(1000);
        }
    }
}
