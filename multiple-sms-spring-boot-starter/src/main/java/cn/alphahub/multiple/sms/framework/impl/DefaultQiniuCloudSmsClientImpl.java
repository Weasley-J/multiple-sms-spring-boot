package cn.alphahub.multiple.sms.framework.impl;

import cn.alphahub.multiple.sms.framework.SmsClient;
import cn.alphahub.multiple.sms.annotation.EnableMultipleSms;
import cn.alphahub.multiple.sms.config.entity.QiniuSmsProperties;
import cn.alphahub.multiple.sms.domain.AbstractSmsRequest;
import cn.alphahub.multiple.sms.domain.AbstractSmsResponse;
import cn.alphahub.multiple.sms.domain.BaseSmsResponse;
import cn.alphahub.multiple.sms.exception.SmsException;
import cn.hutool.json.JSONUtil;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.sms.SmsManager;
import com.qiniu.util.Auth;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 七牛云短信实现
 *
 * @author lwj
 * @version 1.0
 * @apiNote <a href="https://developer.qiniu.com/sms/5897/sms-api-send-message">七牛云短信帮助书文档</a>
 * @date 2021-09-24
 */
@Slf4j
@Component
@ConditionalOnBean(annotation = {EnableMultipleSms.class})
public class DefaultQiniuCloudSmsClientImpl implements SmsClient {

    /**
     * 短信配置元数据
     */
    private final QiniuSmsProperties smsProperties;

    public DefaultQiniuCloudSmsClientImpl(QiniuSmsProperties smsProperties) {
        this.smsProperties = smsProperties;
    }

    @Override
    public AbstractSmsResponse send(@Valid AbstractSmsRequest smsRequest) {
        log.info("sms_request: {}", JSONUtil.toJsonStr(smsRequest));
        //设置需要操作的账号的AK和SK
        Auth auth = Auth.create(smsProperties.getAccessKey(), smsProperties.getSecretKey());
        //实例化一个SmsManager对象
        SmsManager smsManager = new SmsManager(auth);
        Response resp = null;
        try {
            //短信模板id为空发送全文短信
            if (StringUtils.isBlank(smsProperties.getTemplateId())) {
                resp = smsManager.sendFulltextMessage(smsRequest.getPhones(), smsRequest.getContents());
            } else {
                //自定义模板变量（更具自己的情况修改），变量设置在创建模板时，参数template指定, 传递的变量参数不支持中文，请使用英语或数字
                Map<String, String> parameters = new LinkedHashMap<>();
                // parameters.put("name", "张三"); parameters.put("code", "123456");
                if (StringUtils.isNotBlank(smsProperties.getParameters())) {
                    String[] params = StringUtils.split(smsProperties.getParameters(), ",");
                    String[] contents = StringUtils.split(smsRequest.getContents(), ",");
                    if (params.length != contents.length) {
                        throw new SmsException("'模板中的变量的个数'与'模板中变量个数对应的值'的数量不一致！请检查入参：parameters=" + Arrays.toString(params) + ", contents=" + Arrays.toString(contents));
                    }
                    for (int i = 0; i < params.length; i++) {
                        parameters.put(params[i], contents[i]);
                    }
                }
                resp = smsManager.sendMessage(smsProperties.getTemplateId(), smsRequest.getPhones(), parameters);
            }
            log.info("response:{}", JSONUtil.toJsonStr(resp));
            /*
            resp = smsManager.describeSignature("passed", 0, 0);
            resp = smsManager.createSignature("signature", "app", new String[]{"data:image/gif;base64,xxxxxxxxxx"});
            resp = smsManager.describeTemplate("passed", 0, 0);
            resp = smsManager.createTemplate("name", "template", "notification", "test", "signatureId");
            resp = smsManager.modifyTemplate("templateId", "name", "template", "test", "signatureId");
            resp = smsManager.modifySignature("SignatureId", "signature");
            resp = smsManager.deleteSignature("signatureId");
            resp = smsManager.deleteTemplate("templateId");
            SignatureInfo sinfo = smsManager.describeSignatureItems("", 0, 0);
            System.out.println(sinfo.getItems().get(0).getAuditStatus());
            TemplateInfo tinfo = smsManager.describeTemplateItems("", 0, 0);
            System.out.println(tinfo.getItems().get(0).getAuditStatus());
            */
        } catch (QiniuException e) {
            log.error("{} {}", JSONUtil.toJsonStr(smsRequest), e.getLocalizedMessage(), e);
            Map<String, Object> responseMap = new LinkedHashMap<>(1);
            responseMap.put("phones", smsRequest.getPhones());
            responseMap.put("content", smsRequest.getContents());
            responseMap.put("error", e.getLocalizedMessage());
            return new BaseSmsResponse().setThirdResult(responseMap);
        }
        AbstractSmsResponse smsResponse = new BaseSmsResponse().setThirdResult(resp);
        log.info("sms_response: {}", JSONUtil.toJsonStr(smsResponse));
        return smsResponse;
    }
}
