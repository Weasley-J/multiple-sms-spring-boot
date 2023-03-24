package cn.alphahub.multiple.sms.test;

import cn.alphahub.multiple.sms.annotation.EnableMultipleSms;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

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
    }

    /*@Bean
    public Executor threadPoolExecutor0() {
        return new ThreadPoolExecutor(2, 4, 10, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(), Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
    }

    @Bean
    public ThreadPoolExecutor threadPoolExecutor1() {
        return new ThreadPoolExecutor(2, 4, 10, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(), Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
    }

    @Bean
    public ThreadPoolExecutor multipleSmsThreadPoolExecutor1() {
        return new ThreadPoolExecutor(2, 4, 10, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(), Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
    }

    @Bean
    public ThreadPoolExecutor threadPoolExecutor3() {
        return new ThreadPoolExecutor(2, 4, 10, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(), Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
    }

    @Bean
    @Primary
    public SmsTemplate smsTemplate(SmsAspect smsAspect, ThreadPoolExecutor threadPoolExecutor3) {
        return new SmsTemplate(smsAspect, threadPoolExecutor3);
    }*/
}
