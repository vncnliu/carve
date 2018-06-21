package me.vncnliu.microservices.kafka;

import kafka.admin.AdminUtils;
import kafka.admin.RackAwareMode;
import kafka.utils.ZkUtils;
import org.apache.kafka.common.security.JaasUtils;

import java.util.Properties;

/**
 * add description<br>
 * created on 2018/2/8<br>
 *
 * @author vncnliu
 * @since default 1.0.0
 */
public class KafkaMain {

    public static void main(String[] args) {
        try {
            ZkUtils zkUtils = ZkUtils.apply("192.168.88.43:2181", 30000, 30000, JaasUtils.isZkSecurityEnabled());
            // 创建一个单分区单副本名为test1的topic
            AdminUtils.createTopic(zkUtils, "test1", 1, 1, new Properties(), RackAwareMode.Enforced$.MODULE$);
            zkUtils.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

}
