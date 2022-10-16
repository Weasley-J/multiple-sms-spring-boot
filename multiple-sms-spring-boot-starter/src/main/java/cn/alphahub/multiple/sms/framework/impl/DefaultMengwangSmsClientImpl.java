package cn.alphahub.multiple.sms.framework.impl;


import cn.alphahub.multiple.sms.config.entity.MengwangSmsProperties;
import cn.alphahub.multiple.sms.domain.AbstractSmsRequest;
import cn.alphahub.multiple.sms.domain.AbstractSmsResponse;
import cn.alphahub.multiple.sms.domain.BaseSmsRequest;
import cn.alphahub.multiple.sms.enums.SmsI18n;
import cn.alphahub.multiple.sms.exception.SmsException;
import cn.alphahub.multiple.sms.framework.SmsClient;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 梦网国际短信实现
 *
 * @author weasley
 * @version 1.0.0
 */
@Slf4j
@Validated
@Component
@EnableConfigurationProperties({MengwangSmsProperties.class})
public class DefaultMengwangSmsClientImpl implements CommandLineRunner, SmsClient {
    /**
     * sms clients
     */
    private static final Map<SmsI18n, SmsClient> SMS_CLIENTS = new ConcurrentHashMap<>(4);
    private final ApplicationContext applicationContext;

    public DefaultMengwangSmsClientImpl(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * 推断短信发送客户端
     *
     * @param smsI18n I18n Type
     * @return SmsClient
     */
    protected SmsClient deduceClient(SmsI18n smsI18n) {
        switch (smsI18n) {
            case ENGLISH:
                return SMS_CLIENTS.get(SmsI18n.CHINESE);
            case CHINESE:
                return SMS_CLIENTS.get(SmsI18n.ENGLISH);
            default:
                throw new SmsException("deduce client:" + smsI18n + "," + JSONUtil.toJsonStr(SMS_CLIENTS));
        }
    }

    @Override
    public void run(String... args) throws Exception {
        Map<String, SmsClient> beansOfType = applicationContext.getBeansOfType(SmsClient.class);
        if (ObjectUtils.isNotEmpty(beansOfType)) {
            beansOfType.forEach((k, v) -> {
                if (k.toLowerCase().contains(SmsI18n.CHINESE.name().toLowerCase()))
                    SMS_CLIENTS.put(SmsI18n.CHINESE, v);
                if (k.toLowerCase().contains(SmsI18n.ENGLISH.name().toLowerCase()))
                    SMS_CLIENTS.put(SmsI18n.ENGLISH, v);
            });
        }
    }

    @Override
    public AbstractSmsResponse send(AbstractSmsRequest smsRequest) {
        BaseSmsRequest request = (BaseSmsRequest) smsRequest;
        return this.deduceClient(request.getSmsI18n()).send(request);
    }
}
