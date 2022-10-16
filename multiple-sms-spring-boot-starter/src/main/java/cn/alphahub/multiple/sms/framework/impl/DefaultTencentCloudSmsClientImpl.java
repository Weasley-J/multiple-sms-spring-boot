package cn.alphahub.multiple.sms.framework.impl;

import cn.alphahub.multiple.sms.annotation.EnableMultipleSms;
import cn.alphahub.multiple.sms.config.entity.TencentSmsProperties;
import cn.alphahub.multiple.sms.domain.AbstractSmsRequest;
import cn.alphahub.multiple.sms.domain.AbstractSmsResponse;
import cn.alphahub.multiple.sms.domain.BaseSmsResponse;
import cn.alphahub.multiple.sms.framework.SmsClient;
import cn.hutool.json.JSONUtil;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sms.v20210111.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20210111.models.SendSmsResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

/**
 * 腾讯云短信实现
 *
 * @author lwj
 * @version 1.0
 */
@Slf4j
@Validated
@Component
@ConditionalOnBean(annotation = {EnableMultipleSms.class})
public class DefaultTencentCloudSmsClientImpl implements SmsClient {
    /**
     * 短信配置元数据
     */
    private final TencentSmsProperties smsProperties;

    public DefaultTencentCloudSmsClientImpl(TencentSmsProperties smsProperties) {
        this.smsProperties = smsProperties;
    }


    @Override
    public AbstractSmsResponse send(@Valid AbstractSmsRequest smsRequest) {
        log.info("sms_request: {}", JSONUtil.toJsonStr(smsRequest));
        SendSmsResponse resp = new SendSmsResponse();
        try {
            // 实例化一个认证对象，入参需要传入腾讯云账户secretId，secretKey,此处还需注意密钥对的保密
            // 密钥可前往https://console.cloud.tencent.com/cam/capi网站进行获取
            Credential cred = new Credential(smsProperties.getSecretId(), smsProperties.getSecretKey());
            // 实例化一个http选项，可选的，没有特殊需求可以跳过
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("sms.tencentcloudapi.com");
            // 实例化一个client选项，可选的，没有特殊需求可以跳过
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            // 实例化要请求产品的client对象,clientProfile是可选的
            com.tencentcloudapi.sms.v20210111.SmsClient client = new com.tencentcloudapi.sms.v20210111.SmsClient(cred, smsProperties.getRegion(), clientProfile);
            // 实例化一个请求对象,每个接口都会对应一个request对象
            SendSmsRequest req = new SendSmsRequest();
            req.setPhoneNumberSet(smsRequest.getPhones());

            req.setSmsSdkAppId(smsProperties.getSmsSdkAppId());
            req.setTemplateId(smsProperties.getTemplateId());
            // 这里可以查看模板的签名[https://console.cloud.tencent.com/smsv2/csms-sign]
            req.setSignName(smsProperties.getSignName());
            //模板参数，若无模板参数，则设置为空。 模板参数的个数需要与 TemplateId 对应模板的变量个数保持一致。
            req.setTemplateParamSet(StringUtils.split(smsRequest.getContents(), ","));
            // 返回的resp是一个SendSmsResponse的实例，与请求对象对应
            resp = client.SendSms(req);
            // 输出json格式的字符串回包
            log.info("response:{}", JSONUtil.toJsonStr(resp));
        } catch (TencentCloudSDKException e) {
            log.error("{}", e, e);
        }
        AbstractSmsResponse smsResponse = new BaseSmsResponse().setThirdResult(resp);
        log.info("sms_response: {}", JSONUtil.toJsonStr(smsResponse));
        return smsResponse;
    }
}
