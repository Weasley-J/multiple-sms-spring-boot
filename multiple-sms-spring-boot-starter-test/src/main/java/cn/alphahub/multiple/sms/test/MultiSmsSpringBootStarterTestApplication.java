package cn.alphahub.multiple.sms.test;

import cn.alphahub.multiple.sms.annotation.EnableMultipleSmsSupport;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * multiple-sms-spring-boot-starter示例验证
 * <ul>
 *    <li>项目中引入'multiple-sms-spring-boot-starter'如果不使用注解{@code @EnableMultipleSmsSupport}开启多模板短信,项目也不会报错</li>
 * </ul>
 *
 * @author weasley
 */
@SpringBootApplication
@EnableMultipleSmsSupport
public class MultiSmsSpringBootStarterTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(MultiSmsSpringBootStarterTestApplication.class, args);
    }

}
