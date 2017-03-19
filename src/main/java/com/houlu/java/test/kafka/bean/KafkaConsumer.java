package com.houlu.java.test.kafka.bean;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.serializer.StringDecoder;
import kafka.utils.VerifiableProperties;

/**
 * 类名称: KafkaConsumer <br>
 * 类描述: <br>
 *
 * @author lu.hou
 * @version 1.0.0
 * @since 17/2/8 上午12:20
 */
public class KafkaConsumer {
    private final ConsumerConnector consumer;

    public final static String TOPIC = "YPT_KAFKA_TASK_DATA_TOPIC";


    private KafkaConsumer() {
        Properties props = new Properties();
        //zookeeper 配置
//        props.put("zookeeper.connect", "172.17.102.171:12181");
        props.put("zookeeper.connect", "172.17.103.186:2181,172.17.103.187:2181,172.17.103.188:2181");
        props.put("zookeeper.session.timeout.ms", "4000");
        props.put("zookeeper.sync.time.ms", "200");

        //group 代表一个消费组
        props.put("group.id", "jd-group");


        //消费者属性
        props.put("auto.commit.enable", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("auto.offset.reset", "smallest");
        props.put("serializer.class", "kafka.serializer.StringEncoder");

        ConsumerConfig config = new ConsumerConfig(props);

        consumer = kafka.consumer.Consumer.createJavaConsumerConnector(config);
    }

    void consume() {
        Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
        topicCountMap.put(KafkaConsumer.TOPIC, new Integer(1));

        StringDecoder keyDecoder = new StringDecoder(new VerifiableProperties());
        StringDecoder valueDecoder = new StringDecoder(new VerifiableProperties());

        Map<String, List<KafkaStream<String, String>>> consumerMap =
                consumer.createMessageStreams(topicCountMap, keyDecoder, valueDecoder);
        KafkaStream<String, String> stream = consumerMap.get(KafkaConsumer.TOPIC).get(0);
        ConsumerIterator<String, String> it = stream.iterator();
        while (it.hasNext()) {
            System.out.println(Thread.currentThread().getName() +"---"+it.next().message());
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread thread1 = new Thread(new Runnable() {
            public void run() {
                new KafkaConsumer().consume();
            }
        });
        Thread thread2 = new Thread(new Runnable() {
            public void run() {
                new KafkaConsumer().consume();
            }
        });
        thread1.start();
        thread2.start();
        Thread.sleep(10000000);
    }

}
