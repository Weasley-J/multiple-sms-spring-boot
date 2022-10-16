package cn.alphahub.multiple.sms.config.entity;

import cn.alphahub.multiple.sms.enums.SmsSupplier;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotEmpty;

/**
 * tencent sms properties
 *
 * @author weasley
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = "spring.sms.conf.sms-properties.multi-templates.metadata-mapping.tencent")
public class TencentSmsProperties extends AbstractSmsProperties {
    /**
     * 短信access-key
     */
    @NotEmpty(message = "短信secretId不能为空")
    private String secretId;
    /**
     * 短信secret-key
     */
    @NotEmpty(message = "短信secretKey不能为空")
    private String secretKey;
    /**
     * 添加应用后生成的实际 SdkAppId，示例如1400006666
     * 短信SdkAppId，在<a href="https://console.cloud.tencent.com/smsv2">短信控制台</a>
     * <p>更换为自己的短信SdkAppId</p>
     */
    private String smsSdkAppId;
    /**
     * 模板ID，必须填写已审核通过的模板ID。模板ID可登录<a href="https://console.cloud.tencent.com/smsv2/csms-template">短信控制台</a>查看，若向境外手机号发送短信，仅支持使用国际/港澳台短信模板。
     * <p>更换为自己的短信模板ID</p>
     */
    private String templateId;
    /**
     * 区域
     */
    private String region = "ap-nanjing";
    /**
     * 短信签名，<a href="https://console.cloud.tencent.com/smsv2/csms-sign">这里可以查看模板的签名</a>
     */
    private String signName;
}
