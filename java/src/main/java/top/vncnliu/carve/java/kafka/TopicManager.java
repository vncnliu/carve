package top.vncnliu.carve.java.kafka;

import kafka.admin.AdminUtils;
import kafka.admin.RackAwareMode;
import kafka.utils.ZkUtils;
import org.apache.kafka.common.security.JaasUtils;

import java.util.Properties;

/**
 * add description<br>
 * created on 2018/1/26<br>
 *
 * @author vncnliu
 * @since default 1.0.0
 */
public class TopicManager {

    public static void main(String[] args) {
        ZkUtils zkUtils = ZkUtils.apply("192.168.1.184:2181", 30000, 30000, JaasUtils.isZkSecurityEnabled());
// 创建一个单分区单副本名为t1的topic
        AdminUtils.createTopic(zkUtils, "test-topic-1", 1, 1, new Properties(), RackAwareMode.Enforced$.MODULE$);
        zkUtils.close();
    }
}
