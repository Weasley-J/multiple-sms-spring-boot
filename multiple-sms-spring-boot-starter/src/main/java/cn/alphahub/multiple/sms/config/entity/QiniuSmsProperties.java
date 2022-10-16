package cn.alphahub.multiple.sms.config.entity;

import cn.alphahub.multiple.sms.enums.SmsSupplier;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * qiniu sms properties
 *
 * @author weasley
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = "spring.sms.conf.sms-properties.multi-templates.metadata-mapping.qiniu")
public class QiniuSmsProperties extends AbstractSmsProperties {
    /**
     * 账号的AK
     */
    private String accessKey;
    /**
     * 账号的SK
     */
    private String secretKey;
    /**
     * 模板Id,短信模板id为空发送全文短信
     */
    private String templateId;
    /**
     * 自定义模板变量（更具自己的情况修改），变量设置在创建模板时，参数template指定, 传递的变量参数不支持中文，请使用英语或数字<br>
     * 示例(保证顺序和ECS后台配置的一致,多个用','隔开): name,code
     */
    private String parameters;
}
