package cn.alphahub.multiple.sms.test.demo;

import cn.alphahub.multiple.sms.framework.SmsClient;
import cn.alphahub.multiple.sms.domain.AbstractSmsRequest;
import cn.alphahub.multiple.sms.domain.AbstractSmsResponse;
import cn.alphahub.multiple.sms.domain.BaseSmsResponse;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

import javax.validation.Valid;

/**
 * 自定义短信发送实现类实例
 * <p>这个示例是自定义实现短信服务的示例</p>
 *
 * @author lwj
 * @version 1.0
 */
@Slf4j
public class MyCustomSmsClientDemoImpl implements SmsClient {

    /**
     * 发送短信
     *
     * @return 短信供应商的发送短信后的返回结果
     */
    @Override
    public AbstractSmsResponse send(@Valid AbstractSmsRequest smsRequest) {
        log.info("sms_request: {}", JSONUtil.toJsonStr(smsRequest));
        return new BaseSmsResponse().setThirdResult(JSONUtil.toJsonPrettyStr(smsRequest));
    }
}
