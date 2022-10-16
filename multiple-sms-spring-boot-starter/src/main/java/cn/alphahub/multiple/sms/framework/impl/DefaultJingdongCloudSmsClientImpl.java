package cn.alphahub.multiple.sms.framework.impl;

import cn.alphahub.multiple.sms.annotation.EnableMultipleSms;
import cn.alphahub.multiple.sms.config.entity.JingdongSmsProperties;
import cn.alphahub.multiple.sms.domain.AbstractSmsRequest;
import cn.alphahub.multiple.sms.domain.AbstractSmsResponse;
import cn.alphahub.multiple.sms.domain.BaseSmsResponse;
import cn.alphahub.multiple.sms.framework.SmsClient;
import cn.hutool.json.JSONUtil;
import com.jdcloud.sdk.auth.CredentialsProvider;
import com.jdcloud.sdk.auth.StaticCredentialsProvider;
import com.jdcloud.sdk.http.HttpRequestConfig;
import com.jdcloud.sdk.http.Protocol;
import com.jdcloud.sdk.service.sms.model.BatchSendRequest;
import com.jdcloud.sdk.service.sms.model.BatchSendResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import java.util.Arrays;

/**
 * 京东云短信实现
 *
 * @author lwj
 * @version 1.0
 * @implNote 京东云暂不支持申请个人账号短信
 */
@Slf4j
@Component
@ConditionalOnBean(annotation = {EnableMultipleSms.class})
public class DefaultJingdongCloudSmsClientImpl implements SmsClient {
    /**
     * 地域信息不用修改
     */
    private static final String REGION = "cn-north-1";
    /**
     * 短信配置元数据
     */
    private final JingdongSmsProperties smsProperties;

    public DefaultJingdongCloudSmsClientImpl(JingdongSmsProperties smsProperties) {
        this.smsProperties = smsProperties;
    }

    @Override
    public AbstractSmsResponse send(@Valid AbstractSmsRequest smsRequest) {
        log.info("sms_request: {}", JSONUtil.toJsonStr(smsRequest));
        BatchSendRequest request = new BatchSendRequest();
        request.setRegionId(ObjectUtils.defaultIfNull(smsProperties.getRegion(), REGION));
        // 设置模板ID 应用管理-文本短信-短信模板 页面可以查看模板ID
        request.setTemplateId(smsProperties.getTemplateId());
        // 设置签名ID 应用管理-文本短信-短信签名 页面可以查看签名ID
        request.setSignId(smsProperties.getSignId());
        // 设置下发手机号list
        request.setPhoneList(Arrays.asList(smsRequest.getPhones()));
        // 设置模板参数，非必传，如果模板中包含变量请填写对应参数，否则变量信息将不做替换。
        request.setParams(Arrays.asList(smsRequest.getContents().split(",")));
        BatchSendResponse response = this.getSmsClient().batchSend(request);
        AbstractSmsResponse smsResponse = new BaseSmsResponse().setThirdResult(response);
        log.info("sms_response: {}", JSONUtil.toJsonStr(smsResponse));
        return smsResponse;
    }

    /**
     * 初始换京东云短信客户端
     *
     * @return {@code com.jdcloud.sdk.service.sms.client.AbstractMengwangSmsClient}
     * @apiNote 普通用户ak/sk （应用管理-文本短信-概览 页面可以查看自己ak/sk）
     */
    public com.jdcloud.sdk.service.sms.client.SmsClient getSmsClient() {
        CredentialsProvider credentialsProvider = new StaticCredentialsProvider(smsProperties.getAccessKeyId(), smsProperties.getSecretAccessKey());
        return com.jdcloud.sdk.service.sms.client.SmsClient.builder()
                .credentialsProvider(credentialsProvider)
                //默认为HTTPS
                .httpRequestConfig(new HttpRequestConfig.Builder().protocol(Protocol.HTTP).build())
                .build();
    }
}
