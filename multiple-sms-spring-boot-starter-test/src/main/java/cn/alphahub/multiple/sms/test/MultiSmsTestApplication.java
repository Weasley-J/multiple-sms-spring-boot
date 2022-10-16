package cn.alphahub.multiple.sms.test;

import cn.alphahub.multiple.sms.annotation.EnableMultipleSms;
import cn.alphahub.multiple.sms.config.MetadataProperties;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.util.Arrays;

/**
 * multiple-sms-spring-boot-starter示例验证
 * <ul>
 *    <li>项目中引入'multiple-sms-spring-boot-starter'如果不使用注解{@code @EnableMultipleSms}开启多模板短信,项目也不会报错</li>
 * </ul>
 *
 * @author weasley
 */
@EnableMultipleSms
@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
public class MultiSmsTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(MultiSmsTestApplication.class, args);
        System.err.println(JSONUtil.toJsonStr(Arrays.asList("1", "人民公园")));
        System.err.println(JSONUtil.toJsonPrettyStr(SpringUtil.getBean(MetadataProperties.class)));
    }

}
