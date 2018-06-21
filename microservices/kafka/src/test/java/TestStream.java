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
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * add description<br>
 * created on 2018/2/8<br>
 *
 * @author vncnliu
 * @since default 1.0.0
 */
public class TestStream {

    String producerTopic = "streams-plaintext-input-4";
    String producerTopic2 = "streams-plaintext-input-24";
    String consumerTopic = "streams-linesplit-output-4";
    String consumerCountTopic = "streams-wordcount-output-4";

    @Test
    public void main() {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-pipe");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.88.43:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        final StreamsBuilder builder = new StreamsBuilder();
        KStream<String, String> source = builder.stream(producerTopic);
        KStream<String, String> source2 = builder.stream(producerTopic2);

        KStream<String, String> joined = source.join(source2,
                (leftValue, rightValue) -> {
                    System.out.println("t");
                    return "left=" + leftValue + ", right=" + rightValue;
                },
                JoinWindows.of(TimeUnit.SECONDS.toMillis(10)),
                Joined.with(
                        Serdes.String(), /* key */
                        Serdes.String(),   /* left value */
                        Serdes.String())  /* right value */
        );

        joined.flatMapValues(value -> {
            System.out.println(value);
            return Arrays.asList(value.toLowerCase(Locale.getDefault()).split("\\W+"));
        })
                .groupBy((key, value) -> value)
                .count(Materialized.as("counts-store-2"))
                .toStream()
                .to(consumerCountTopic, Produced.with(Serdes.String(), Serdes.Long()));

        final Topology topology = builder.build();
        System.out.println(topology.describe());

        final KafkaStreams streams = new KafkaStreams(topology, props);

        final CountDownLatch latch = new CountDownLatch(1);

        // attach shutdown handler to catch control-c
        Runtime.getRuntime().addShutdownHook(new Thread("streams-shutdown-hook") {
            @Override
            public void run() {
                streams.close();
                latch.countDown();
            }
        });

        try {
            streams.start();

            Thread pro = new Thread(() -> producer(producerTopic));
            pro.start();
            Thread pro2 = new Thread(() -> producer(producerTopic2));
            pro2.start();
            Thread con = new Thread(() -> consumerCount());
            con.start();

            latch.await();
        } catch (Throwable e) {
            System.exit(1);
        }
        System.exit(0);

    }

    private void consumer() {

        ZkUtils zkUtils = ZkUtils.apply("192.168.88.43:2181", 30000, 30000, JaasUtils.isZkSecurityEnabled());
        if(!AdminUtils.topicExists(zkUtils, consumerTopic)){
            // 创建一个单分区单副本名为test1的topic
            AdminUtils.createTopic(zkUtils, consumerTopic, 1, 1, new Properties(), RackAwareMode.Enforced$.MODULE$);
        }
        zkUtils.close();

        Properties props = new Properties();
        props.put("bootstrap.servers", "192.168.88.43:9092");
        props.put("group.id", "quotes");
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);

        consumer.subscribe(Collections.singletonList(consumerTopic));

        while (true){
            ConsumerRecords<String, String> records = consumer.poll(10);
            for (ConsumerRecord<String, String> record : records) {
                System.out.println(record);
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void consumerCount() {

        ZkUtils zkUtils = ZkUtils.apply("192.168.88.43:2181", 30000, 30000, JaasUtils.isZkSecurityEnabled());
        if(!AdminUtils.topicExists(zkUtils, consumerCountTopic)){
            // 创建一个单分区单副本名为test1的topic
            AdminUtils.createTopic(zkUtils, consumerCountTopic, 1, 1, new Properties(), RackAwareMode.Enforced$.MODULE$);
        }
        zkUtils.close();

        Properties props = new Properties();
        props.put("bootstrap.servers", "192.168.88.43:9092");
        props.put("group.id", "quotes");
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.LongDeserializer");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);

        consumer.subscribe(Collections.singletonList(consumerCountTopic));

        while (true){
            ConsumerRecords<String, String> records = consumer.poll(10);
            for (ConsumerRecord<String, String> record : records) {
                System.out.println(record);
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void producer(String topic) {
        ZkUtils zkUtils = ZkUtils.apply("192.168.88.43:2181", 30000, 30000, JaasUtils.isZkSecurityEnabled());
        if(!AdminUtils.topicExists(zkUtils, topic)){
            // 创建一个单分区单副本名为test1的topic
            AdminUtils.createTopic(zkUtils, topic, 1, 1, new Properties(), RackAwareMode.Enforced$.MODULE$);
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

        while (true){
            producer.send(new ProducerRecord<>(topic
                    , "key"
                    , "a\\b\\c"));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void sendQuote(){
        String topic = "quote_2";
        ZkUtils zkUtils = ZkUtils.apply("192.168.88.43:2181", 30000, 30000, JaasUtils.isZkSecurityEnabled());
        if(!AdminUtils.topicExists(zkUtils, topic)){
            // 创建一个单分区单副本名为test1的topic
            AdminUtils.createTopic(zkUtils, topic, 1, 1, new Properties(), RackAwareMode.Enforced$.MODULE$);
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

        Random random = new Random();
        while (true){
            int randomInt = random.nextInt(1000);
            String symbolCode = "s"+randomInt;
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code",symbolCode);
            jsonObject.put("price",random.nextDouble());
            producer.send(new ProducerRecord<>(topic
                    , symbolCode
                    , jsonObject.toJSONString()));
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
