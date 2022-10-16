package cn.alphahub.multiple.sms.config.entity;

import cn.alphahub.multiple.sms.enums.SmsSupplier;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotEmpty;


/**
 * ali sms properties
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
@ConfigurationProperties(prefix = "spring.sms.conf.sms-properties.multi-templates.metadata-mapping.ali")
public class AliSmsProperties extends AbstractSmsProperties {
    /**
     * 短信access-key
     */
    @NotEmpty(message = "短信accessKey不能为空")
    private String accessKey;
    /**
     * 短信secret-key
     */
    @NotEmpty(message = "短信secretKey不能为空")
    private String secretKey;
    /**
     * 区域
     */
    private String regionId;
    /**
     * 短信签名、短信签名id
     */
    private String signName;
    /**
     * 短信模板code、短信模板id
     */
    private String templateCode;
    /**
     * 可选配置: 模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时，此处模板中的变量的值为动态入参<br>
     * 示例(保证顺序和ECS后台配置的一直): name,code
     */
    private String templateParams;
}
