package cn.alphahub.multiple.sms.config;

import cn.alphahub.multiple.sms.enums.SmsSupplier;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * 默认短信模板
 *
 * @author weasley
 * @version 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "spring.sms.conf.sms-properties.default-template")
public class DefaultSmsTemplateProperties {
    /**
     * 短信模板名称,不支持短信模板名的短信厂商可忽略此参数
     */
    @NotEmpty
    private String templateName;
    /**
     * 短信供应商
     */
    @NotNull
    private SmsSupplier smsSupplier;
}
