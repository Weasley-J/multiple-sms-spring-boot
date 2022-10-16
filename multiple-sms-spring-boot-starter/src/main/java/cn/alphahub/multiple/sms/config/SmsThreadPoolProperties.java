package cn.alphahub.multiple.sms.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.concurrent.TimeUnit;

/**
 * 线程池配置参数
 */
@Data
@ConfigurationProperties(prefix = "spring.sms.conf.thread")
public class SmsThreadPoolProperties {
    /**
     * 核心线程池数量，默认：50
     */
    private Integer corePoolSize = 50;
    /**
     * 最大线程数，默认：200
     */
    private Integer maximumPoolSize = 200;
    /**
     * 存活时间，默认：10
     */
    private Long keepAliveTime = 10L;
    /**
     * 存活时间单位，默认：{@code TimeUnit.SECONDS}
     *
     * @see TimeUnit
     */
    private TimeUnit timeUnit = TimeUnit.SECONDS;
    /**
     * 最大任务数量，默认：2000
     */
    private Integer capacity = 2000;
}
