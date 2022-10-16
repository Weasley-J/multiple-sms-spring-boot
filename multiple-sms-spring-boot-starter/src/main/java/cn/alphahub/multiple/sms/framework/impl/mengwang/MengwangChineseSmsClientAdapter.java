package cn.alphahub.multiple.sms.framework.impl.mengwang;

import cn.alphahub.multiple.sms.annotation.EnableMultipleSms;
import cn.alphahub.multiple.sms.config.entity.MengwangSmsProperties;
import cn.alphahub.multiple.sms.domain.AbstractSmsRequest;
import cn.alphahub.multiple.sms.domain.AbstractSmsResponse;
import cn.alphahub.multiple.sms.domain.BaseSmsRequest;
import cn.alphahub.multiple.sms.enums.SmsI18n;
import cn.alphahub.multiple.sms.framework.impl.mengwang.entity.MwSmsWrapper;
import cn.hutool.json.JSONUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

/**
 * 短信中文实现
 * <p>
 *
 * </p>
 *
 * @author weasley
 * @version 1.0.0
 */
@Slf4j
@Getter
@Component
@ConditionalOnBean(annotation = {EnableMultipleSms.class})
public class MengwangChineseSmsClientAdapter extends AbstractMengwangSmsClient {
    private DefaultMengwangSmsClient clientAggregator;
    private MengwangSmsProperties mengwangSmsProperties;

    public MengwangChineseSmsClientAdapter(MengwangSmsProperties mengwangSmsProperties) {
        this.mengwangSmsProperties = mengwangSmsProperties;
    }

    public MengwangChineseSmsClientAdapter(DefaultMengwangSmsClient clientAggregator, MengwangSmsProperties mengwangSmsProperties) {
        this.clientAggregator = clientAggregator;
        this.mengwangSmsProperties = mengwangSmsProperties;
    }

    @Override
    public AbstractSmsResponse send(AbstractSmsRequest smsRequest) {
        BaseSmsRequest request = (BaseSmsRequest) smsRequest;
        log.info("使用梦网中文短信实现：{}", JSONUtil.toJsonStr(request));
        return clientAggregator.doSend(MwSmsWrapper.builder()
                .signContent(mengwangSmsProperties.getSignContent(SmsI18n.CHINESE))
                .request(request)
                .mengwangSmsProperties(mengwangSmsProperties)
                .build());
    }
}
