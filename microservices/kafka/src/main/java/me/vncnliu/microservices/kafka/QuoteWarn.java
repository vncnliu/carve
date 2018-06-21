package me.vncnliu.microservices.kafka;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
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
import org.apache.kafka.streams.kstream.KStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

/**
 * add description<br>
 * created on 2018/2/10<br>
 *
 * @author vncnliu
 * @since default 1.0.0
 */
public class QuoteWarn extends BaseKafka {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuoteWarn.class);
    private static final String APPLICATION_ID = "quote_warn_4";
    private static final String WARN_ADD_TOPIC = APPLICATION_ID+"_warn_add_4";
    private static final String WARN_DELETE_TOPIC = APPLICATION_ID+"_warn_delete_4";
    private static final String QUOTE_TOPIC = APPLICATION_ID+"_quote_4";
    private static final String WARN_JOIN_TOPIC = APPLICATION_ID+"_join_4";
    private static final String WARN_STORE = APPLICATION_ID+"_warn_store_4";
    private static final String WARN_GROUP_TPOIC = APPLICATION_ID+"_quote_group_warn_4";

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, APPLICATION_ID);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, SERVERS_CONFIG);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        final StreamsBuilder builder = new StreamsBuilder();

        KStream<String,String> warnAdd = builder.stream(WARN_ADD_TOPIC);
        KStream<String,String> warnDelete = builder.stream(WARN_DELETE_TOPIC);
        KStream<String,String> quote = builder.stream(QUOTE_TOPIC);

        warnDelete.groupBy((key, value) -> key)
            .aggregate(() -> "[]",(key, value, aggregate) -> {
                JSONArray jsonArray = JSON.parseArray(aggregate);
                JSONObject jsonValue = JSON.parseObject(value);
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    if(jsonObject.getString("warnId").equals(jsonValue.getString("warnId"))){
                        jsonArray.remove(i);
                    }
                }
                return jsonArray.toJSONString();
            });

        quote.join(warnAdd.groupBy((key, value) -> key)
                .aggregate(() -> "[]",(key, value, aggregate) -> {
                    JSONArray jsonArray = JSON.parseArray(aggregate);
                    JSONObject jsonValue = JSON.parseObject(value);
                    boolean contines = false;
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                        if(jsonObject.getString("warnId").equals(jsonValue.getString("warnId"))){
                            contines = true;
                            break;
                        }
                    }
                    if(!contines){
                        jsonArray.add(JSON.parse(value));
                    }
                    return jsonArray.toJSONString();
                }),(quoteS,warnS) -> quoteS+warnS ).to(WARN_JOIN_TOPIC);

        final Topology topology = builder.build();

        final KafkaStreams streams = new KafkaStreams(topology, props);

        final CountDownLatch latch = new CountDownLatch(1);

        Runtime.getRuntime().addShutdownHook(new Thread("streams-shutdown-hook") {
            @Override
            public void run() {
                streams.close();
                latch.countDown();
            }
        });

        try {
            streams.start();

            Thread warnAddPro = new Thread(() -> producerAddPro(WARN_ADD_TOPIC));
            warnAddPro.start();
            Thread warnDeletePro = new Thread(() -> producerDeletePro(WARN_DELETE_TOPIC));
            warnDeletePro.start();
            Thread quotePro = new Thread(() -> producerQuote(QUOTE_TOPIC));
            quotePro.start();
            Thread con = new Thread(() -> consumer(WARN_JOIN_TOPIC));
            con.start();

            latch.await();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(),e);
            System.exit(1);
        }
        System.exit(0);
    }

    static void consumer(String topic) {

        ZkUtils zkUtils = ZkUtils.apply("192.168.88.43:2181", 30000, 30000, JaasUtils.isZkSecurityEnabled());
        if(!AdminUtils.topicExists(zkUtils, topic)){
            // 创建一个单分区单副本名为test1的topic
            AdminUtils.createTopic(zkUtils, topic, 1, 1, new Properties(), RackAwareMode.Enforced$.MODULE$);
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

        consumer.subscribe(Collections.singletonList(topic));

        while (true){
            ConsumerRecords<String, String> records = consumer.poll(1);
            System.out.println("===============begin=================");
            for (ConsumerRecord<String, String> record : records) {
                System.out.println(record.value());
            }
            System.out.println("===============end=================");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    static void producerAddPro(String topic) {
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
            jsonObject.put("symbolCode",symbolCode);
            jsonObject.put("price",random.nextInt(1000));
            jsonObject.put("cngoldId",random.nextInt(10000));
            jsonObject.put("warnId",random.nextInt(100000));
            producer.send(new ProducerRecord<>(topic
                    , symbolCode
                    , jsonObject.toJSONString()));
            System.out.println("add:"+jsonObject.toJSONString());
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    static void producerDeletePro(String topic) {
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
            jsonObject.put("symbolCode",symbolCode);
            jsonObject.put("price",random.nextInt(1000));
            jsonObject.put("cngoldId",random.nextInt(10000));
            jsonObject.put("warnId",random.nextInt(100000));
            producer.send(new ProducerRecord<>(topic
                    , symbolCode
                    , jsonObject.toJSONString()));
            System.out.println("delete:"+jsonObject.toJSONString());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    static void producerQuote(String topic) {
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
            jsonObject.put("symbolCode",symbolCode);
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
