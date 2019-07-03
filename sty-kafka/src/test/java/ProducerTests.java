import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.Test;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * Created by zhijian.zhang@chelaile.net.cn on 2019-07-03.
 */
public class ProducerTests {

    @Test
    public void teset1() throws ExecutionException, InterruptedException {
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "bk1:9092,bk2:9093,bk3:9094");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);
        for (int i = 0; i < 1000; i++) {
            ProducerRecord<String, String> record = new ProducerRecord<>("test1", "1", "zhangzhijian1");
            RecordMetadata recordMetadata = producer.send(record).get();
            System.out.println(recordMetadata.partition() + " - " + recordMetadata.offset());
        }
    }

    @Test
    public void test2() {
        System.out.println(StringSerializer.class.getName());
    }
}
