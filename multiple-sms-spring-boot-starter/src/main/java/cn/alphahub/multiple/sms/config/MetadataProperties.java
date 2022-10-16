package cn.alphahub.multiple.sms.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 短信元数据配置属性
 *
 * @author weasley
 * @version 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "spring.sms.conf.sms-properties")
public class MetadataProperties {
    /**
     * 默认短信模板
     */
    private DefaultSmsTemplateProperties defaultTemplate;
    /**
     * 多个短信模板配置属性
     */
    private MultipleSmsTemplatesProperties multipleTemplates;
}
