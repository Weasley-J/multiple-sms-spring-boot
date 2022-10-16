package cn.alphahub.multiple.sms.config.entity;

import lombok.Data;

/**
 * abstract sms properties
 *
 * @author weasley
 * @version 1.0.0
 */
@Data
public abstract class AbstractSmsProperties {
    /**
     * 短信模板名称,不支持短信模板名的短信厂商可忽略此参数
     */
    private String templateName;

    public AbstractSmsProperties(String templateName) {
        this.templateName = templateName;
    }

    public AbstractSmsProperties() {
    }
}
