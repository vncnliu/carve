package top.vncnliu.carve.java.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

/**
 * add description<br>
 * created on 2017/11/17<br>
 *
 * @author vncnliu
 * @since default 1.0.0
 */
public class Test2 {

    public static void main(String[] args) throws InterruptedException {
        Properties props = new Properties();
        props.put("bootstrap.servers", "192.168.88.43:9092");
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        Producer<String, String> producer = new KafkaProducer<>(props);
        int j = 0;
        long begin = System.currentTimeMillis();
        while (j<100){

            for (int i = 0; i < 100; i++){
                producer.send(new ProducerRecord<String, String>("my-topic-3", Integer.toString(i), Long.toString(System.currentTimeMillis())));
            }
            Thread.sleep(1);
            j++;
        }
        producer.close();
        System.out.printf("send end,time:%d",(System.currentTimeMillis()-begin));
        Thread.sleep(1000);
    }
}
