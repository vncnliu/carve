package top.vncnliu.carve.java.kafka;

import com.alibaba.fastjson.JSON;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * add description<br>
 * created on 2017/11/17<br>
 *
 * @author vncnliu
 * @since default 1.0.0
 */
public class QuotesProducer {

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
        long begin = System.currentTimeMillis();

        for (int i = 100; i < 1000000; i++){
            Quotes quotes = getQuotes();
            quotes.setPriceTime(i);
            producer.send(new ProducerRecord<>("quotes-1"
                    ,0
                    ,quotes.getPriceTime()
                    ,quotes.getSymbolUid()+":"+quotes.getPriceTime()
                    , JSON.toJSONString(quotes)));
        }

        producer.close();
        System.out.printf("send end,time:%d",(System.currentTimeMillis()-begin));
        Thread.sleep(1000);
    }

    public static Quotes getQuotes() {
        Quotes quotes = new Quotes();
        quotes.setPriceTime(System.currentTimeMillis());
        quotes.setSymbolUid(110);
        Map<Short,Double> quoteItems = new HashMap<>();
        quoteItems.put((short) 1,1.11D);
        quoteItems.put((short) 2,2.22D);
        quoteItems.put((short) 3,3.33D);
        quoteItems.put((short) 4,4.44D);
        quoteItems.put((short) 5,5.55D);
        quoteItems.put((short) 6,6.66D);
        quoteItems.put((short) 7,7.77D);
        quoteItems.put((short) 8,8.88D);
        quoteItems.put((short) 9,9.99D);
        quoteItems.put((short) 10,10.1D);
        quoteItems.put((short) 11,11.11D);
        quoteItems.put((short) 12,12.12D);
        quotes.setItemList(quoteItems);
        return quotes;
    }
}
