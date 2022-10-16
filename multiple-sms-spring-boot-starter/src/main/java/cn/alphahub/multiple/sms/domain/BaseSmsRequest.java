package cn.alphahub.multiple.sms.domain;

import cn.alphahub.multiple.sms.enums.SmsI18n;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 短信请求基础入参
 *
 * @author weasley
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class BaseSmsRequest extends AbstractSmsRequest {
    /**
     * 国际化类型: 国内、国外
     *
     * @see SmsI18n
     */
    private SmsI18n smsI18n;
}
