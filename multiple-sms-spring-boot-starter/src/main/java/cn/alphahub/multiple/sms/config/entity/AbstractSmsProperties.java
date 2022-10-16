package cn.alphahub.multiple.sms.config.entity;

import cn.alphahub.multiple.sms.enums.SmsSupplier;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * abstract sms properties
 *
 * @author weasley
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AbstractSmsProperties {
    /**
     * 短信模板名称,不支持短信模板名的短信厂商可忽略此参数
     */
    private String templateName;
    /**
     * 短信供应商类型
     */
    private SmsSupplier smsSupplier;
}
