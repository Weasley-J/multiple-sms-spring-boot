package cn.alphahub.multiple.sms.config.entity;

import cn.alphahub.multiple.sms.enums.SmsSupplier;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * huawei sms properties
 *
 * @author weasley
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = "spring.sms.conf.sms-properties.multi-templates.metadata-mapping.huawei")
public class HuaweiSmsProperties extends AbstractSmsProperties {
    /**
     * 国内短信签名通道号或国际/港澳台短信通道号（跟据自己的配置修改）
     */
    private String sender;
    /**
     * APP_Key
     */
    private String appKey;
    /**
     * APP_Secret
     */
    private String appSecret;
    /**
     * 模板ID
     */
    private String templateId;
    /**
     * 签名名称,条件必填,使用国内短信通用模板时填写
     * 国内短信关注,当templateId指定的模板类型为通用模板时生效且必填,
     * 必须是已审核通过的,与模板类型一致的签名名称,国际/港澳台短信不用关注该参数
     */
    private String signature;
}
