package cn.alphahub.multiple.sms.framework.impl;

import cn.alphahub.multiple.sms.annotation.EnableMultipleSms;
import cn.alphahub.multiple.sms.config.entity.AliSmsProperties;
import cn.alphahub.multiple.sms.domain.AbstractSmsRequest;
import cn.alphahub.multiple.sms.domain.AbstractSmsResponse;
import cn.alphahub.multiple.sms.domain.BaseSmsResponse;
import cn.alphahub.multiple.sms.exception.SmsException;
import cn.alphahub.multiple.sms.framework.SmsClient;
import cn.hutool.json.JSONUtil;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 阿里云短信实现
 *
 * @author lwj
 * @version 1.0
 */
@Slf4j
@Component
@ConditionalOnBean(annotation = {EnableMultipleSms.class})
public class DefaultAliCloudSmsClientImpl implements SmsClient {
    /**
     * 短信API产品名称(短信产品名固定, 无需修改)
     */
    private static final String PRODUCT = "Dysmsapi";
    /**
     * 短信API产品域名 (接口地址固定, 无需修改)
     */
    private static final String DOMAIN = "dysmsapi.aliyuncs.com";
    private static final String VERSION = "2017-05-25";
    private static final String SEND_SMS = "SendSms";
    private static final String REGION_ID = "cn-hangzhou";

    /**
     * 短信配置元数据
     */
    private final AliSmsProperties smsProperties;

    public DefaultAliCloudSmsClientImpl(AliSmsProperties smsProperties) {
        this.smsProperties = smsProperties;
    }

    @Override
    public AbstractSmsResponse send(@Valid AbstractSmsRequest smsRequest) {
        log.info("sms_request: {}", JSONUtil.toJsonStr(smsRequest));
        CommonResponse response = new CommonResponse();

        // 可选: 模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时, 此处的值为动态获取
        Map<String, Object> templateParamMap = new LinkedHashMap<>();
        // templateParamMap.put("name", "张三");
        // templateParamMap.put("code", "123456");
        if (StringUtils.isNotBlank(smsProperties.getTemplateParams())) {
            String[] templateParams = StringUtils.split(smsProperties.getTemplateParams(), ",");
            String[] templateContents = StringUtils.split(smsRequest.getContents(), ",");
            if (templateParams.length != templateContents.length) {
                throw new SmsException("'模板中的变量的个数'与'模板中变量个数对应的值'的数量不一致！请检查入参：parameters=" + Arrays.toString(templateParams) + ", templateContents=" + Arrays.toString(templateContents));
            }
            for (int i = 0; i < templateParams.length; i++) {
                templateParamMap.put(templateParams[i], templateContents[i]);
            }
        }
        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain(DOMAIN);
        request.setSysVersion(VERSION);
        request.setSysAction(SEND_SMS);

        request.putQueryParameter("RegionId", ObjectUtils.defaultIfNull(smsProperties.getRegionId(), REGION_ID));
        // 支持对多个手机号码发送短信，手机号码之间以英文逗号","分隔。上限为1000个手机号码。批量调用相对于单条调用及时性稍有延迟。验证码类型短信，建议使用单独发送的方式。
        request.putQueryParameter("SignName", smsProperties.getSignName());
        request.putQueryParameter("TemplateCode", smsProperties.getTemplateCode());
        request.putQueryParameter("PhoneNumbers", StringUtils.join(smsRequest.getPhones(), ","));
        request.putQueryParameter("TemplateParam", JSONUtil.toJsonStr(templateParamMap));

        try {
            response = this.getAcsClient().getCommonResponse(request);
        } catch (ClientException e) {
            log.error("{} 发送短信异常:{}", JSONUtil.toJsonStr(smsRequest), e.getMessage(), e);
        }
        log.info("发送短信状态:{}", JSONUtil.toJsonStr(response.getData()));

        AbstractSmsResponse smsResponse = new BaseSmsResponse().setThirdResult(response);
        log.info("sms_response: {}", JSONUtil.toJsonStr(smsResponse));
        return smsResponse;
    }

    /**
     * 获取IAcsClient实例
     *
     * @return IAcsClient instance
     */
    public IAcsClient getAcsClient() {
        DefaultProfile profile = DefaultProfile.getProfile(smsProperties.getRegionId(), smsProperties.getAccessKey(), smsProperties.getSecretKey());
        DefaultProfile.addEndpoint(REGION_ID, PRODUCT, DOMAIN);
        return new DefaultAcsClient(profile);
    }
}
