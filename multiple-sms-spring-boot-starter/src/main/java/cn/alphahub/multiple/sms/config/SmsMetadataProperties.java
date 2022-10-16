package cn.alphahub.multiple.sms.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import javax.validation.Valid;

/**
 * 短信元数据配置属性
 *
 * @author weasley
 * @version 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "spring.sms.conf.sms-properties")
public class SmsMetadataProperties {
    /**
     * 默认短信模板
     */
    @Valid
    @NestedConfigurationProperty
    private DefaultSmsTemplateProperties defaultTemplate;
    /**
     * 多个短信模板配置属性
     */
    @Valid
    @NestedConfigurationProperty
    private MultipleSmsTemplatesProperties multipleTemplates;
}
