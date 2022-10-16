package cn.alphahub.multiple.sms.framework;

import cn.alphahub.multiple.sms.domain.AbstractSmsRequest;
import cn.alphahub.multiple.sms.domain.AbstractSmsResponse;

/**
 * 多模板、多供应商短信发送上层函数接口
 *
 * @author lwj
 * @version 1.0
 */
@FunctionalInterface
public interface SmsClient {

    /**
     * 发送短信
     */
    AbstractSmsResponse send(AbstractSmsRequest smsRequest);

    /**
     * 默认短信客户端class占位符，{@code @SMS}注解invokeClass默认class的占位符
     *
     * @author lwj
     * @version 1.0
     */
    interface DefaultSmsClientPlaceholder extends SmsClient {

    }
}
