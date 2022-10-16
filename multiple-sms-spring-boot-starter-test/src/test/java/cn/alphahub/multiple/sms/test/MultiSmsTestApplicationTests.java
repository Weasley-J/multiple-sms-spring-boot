package cn.alphahub.multiple.sms.test;

import cn.alphahub.multiple.sms.config.SmsProperties;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MultiSmsTestApplicationTests {

    @Test
    void contextLoads() {
        System.err.println(JSONUtil.toJsonPrettyStr(SpringUtil.getBean(SmsProperties.class)));
    }

}
