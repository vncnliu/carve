package me.vncnliu.microservices.kafka;

import kafka.admin.AdminUtils;
import kafka.admin.RackAwareMode;
import kafka.utils.ZkUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.security.JaasUtils;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.ValueMapper;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;

import java.util.Arrays;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

/**
 * add description<br>
 * created on 2018/2/9<br>
 *
 * @author vncnliu
 * @since default 1.0.0
 */
public class KafkaJoin {

    public static void main(String[] args) {

        String producerTopic = "stream-join-1";
        String tableName = "test_ktable-1";
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-join");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.88.43:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        final StreamsBuilder builder = new StreamsBuilder();
        final KTable<String,String> kTable =  builder.table(producerTopic, Materialized.as(tableName));

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

            Thread pro = new Thread(() -> producer(producerTopic));
            pro.start();
            Thread pro2 = new Thread( new QueryStore(streams,tableName));
            pro2.start();

            latch.await();
        } catch (Throwable e) {
            System.exit(1);
        }
        System.exit(0);
    }

    static class QueryStore implements Runnable {

        String name;
        KafkaStreams streams;

        public QueryStore(KafkaStreams streams, String name) {
            this.name = name;
            this.streams = streams;
        }

        @Override
        public void run() {

            while (true){
                try {
                    ReadOnlyKeyValueStore view = streams.store(name, QueryableStoreTypes.keyValueStore());
                    while (view.all().hasNext()){
                        System.out.println(view.all().next());
                    }
                    System.out.println(view);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e){
                    System.out.println(streams.state());
                    e.printStackTrace();
                }
            }
        }
    }


    private static void producer(String topic) {
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
            String key = "key"+random.nextInt(10);
            System.out.println(key);
            producer.send(new ProducerRecord<>(topic
                    , key
                    , "a\\b\\c"));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
