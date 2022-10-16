package cn.alphahub.multiple.sms.config.entity;

import cn.alphahub.multiple.sms.enums.SmsSupplier;
import jdk.nashorn.internal.scripts.JD;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * jingdong sms properties
 *
 * @author weasley
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = "spring.sms.conf.sms-properties.multi-templates.metadata-mapping.jingdong")
public class JingdongSmsProperties extends AbstractSmsProperties {
    /**
     * 短信accessKeyId
     */
    private String accessKeyId;
    /**
     * 短信secretAccessKey
     */
    private String secretAccessKey;
    /**
     * 短信签名id
     */
    private String signId;
    /**
     * 短信模板ID
     */
    private String templateId;
    /**
     * 区域
     */
    private String region = "cn-north-1";
}
