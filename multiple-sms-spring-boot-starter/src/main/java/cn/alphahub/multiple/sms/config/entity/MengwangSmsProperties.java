package cn.alphahub.multiple.sms.config.entity;

import cn.alphahub.multiple.sms.enums.SmsI18n;
import cn.alphahub.multiple.sms.enums.SmsSupplier;
import cn.alphahub.multiple.sms.exception.SmsException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * mengwang sms properties
 *
 * @author weasley
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = "spring.sms.conf.sms-properties.multi-templates.metadata-mapping.mengwang")
public class MengwangSmsProperties extends AbstractSmsProperties {
    /**
     * 国内短信 + 国际短信短信配置参数映射<br>
     * 此配置参数可以支持国际化<br>
     * <ul>
     *     <li>key: 短信方言类型</li>
     *     <li>val: 账号密码元数据"</li>
     * </ul>
     */
    private Map<SmsI18n, MwSmsProperties> smsI18nMapping;
    /**
     * 签名内容（短信开头的主体名称）;
     * <ul>
     *     <li>chinese: "【柏盛健康】"</li>
     *     <li>english: "【Prosper Health】"</li>
     * </ul>
     */
    private Map<SmsI18n, String> signContents;

    public String getSignContent(SmsI18n smsI18n) {
        String content = signContents.get(smsI18n);
        if (StringUtils.isBlank(content)) throw new SmsException("短信签名内容未配置（短信开头的主体声明文字）！");
        return content;
    }

    /**
     * 推断三方服务器配置属性
     *
     * @param smsI18n 短信类型
     * @return 三方服务器配置属性
     */
    public MwSmsProperties deduceServerProperties(SmsI18n smsI18n) {
        return smsI18nMapping.get(smsI18n);
    }

    /**
     * 短信的'ALL-IN-ONE'参数配置
     */
    @Data
    @Accessors(chain = true)
    public static class MwSmsProperties {
        /**
         * 用户账号：长度最大  6 个字符，统一大写
         */
        private String userid;
        /**
         * 加密后的用户密码：定长小写32 位字符。密码规则详见“3.1鉴权规则”
         */
        private String pwd;
        /**
         * 务器URL地址
         */
        private String serverUrl;
    }
}
