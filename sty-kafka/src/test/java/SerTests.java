import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.kafka.common.serialization.Serializer;
import org.junit.Test;

import java.util.Map;

/**
 * 序列化测试工具类
 * Created by zhijian.zhang@chelaile.net.cn on 2019-07-03.
 */
public class SerTests {
    @Test
     public void test1() {

     }
}

class CustomerSerializer implements Serializer<Customer>  {
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public byte[] serialize(String topic, Customer data) {
        return new byte[0];
    }

    @Override
    public void close() {

    }
}

@Data
@AllArgsConstructor
class Customer {
    private int id;
    private String name;
}
