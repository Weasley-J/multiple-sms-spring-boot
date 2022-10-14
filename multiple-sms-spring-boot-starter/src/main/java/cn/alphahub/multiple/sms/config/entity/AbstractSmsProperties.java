package cn.alphahub.multiple.sms.config.entity;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * abstract sms properties
 *
 * @author weasley
 * @version 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "spring.sms.sms-properties.def")
public abstract class AbstractSmsProperties {
}
