package cn.alphahub.multiple.sms.test.controller;

import cn.alphahub.multiple.sms.SmsTemplate;
import cn.alphahub.multiple.sms.annotation.EnableMultipleSms;
import cn.alphahub.multiple.sms.annotation.SMS;
import cn.alphahub.multiple.sms.domain.AbstractSmsResponse;
import cn.alphahub.multiple.sms.domain.BaseSmsRequest;
import cn.alphahub.multiple.sms.enums.SmsSupplier;
import cn.alphahub.multiple.sms.test.demo.MyCustomSmsClientDemoImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * SMS Service Test Controller
 * <p>多短信供应商、多短信模板示例Controller</p>
 *
 * @author lwj
 * @version 1.0
 * @date 2021-09-29 14:14
 */
@Slf4j
@RestController
@RequestMapping("/sms/support/demo")
@ConditionalOnBean(annotation = {EnableMultipleSms.class})
public class SmsDemoController {

    @Autowired
    private SmsTemplate smsTemplate;

    /**
     * 使用默认短信模板发送短信
     * <p>默认模板可以注解{@code @SMS}，也可以不加</p>
     *
     * @param smsRequest 短信参数
     * @return 发送结果
     */
    @PostMapping("/sendWithDefaultTemplate")
    public AbstractSmsResponse sendWithDefaultTemplate(@RequestBody BaseSmsRequest smsRequest) {
        return smsTemplate.send(smsRequest);
    }

    /**
     * 使用阿里云短信模板1发送短信
     *
     * @param smsRequest 短信参数
     * @return 发送结果
     */
    @SMS(templateName = "促销短信模板")
    @PostMapping("/sendWithAliCloud1")
    public AbstractSmsResponse sendWithAliCloud1(@RequestBody BaseSmsRequest smsRequest) {
        return smsTemplate.send(smsRequest);
    }

    /**
     * 使用阿里云短信模板2发送短信
     *
     * @param smsRequest 短信参数
     * @return 发送结果
     */
    @SMS(templateName = "秒杀短信模板")
    @PostMapping("/sendWithAliCloud2")
    public AbstractSmsResponse sendWithAliCloud2(@RequestBody BaseSmsRequest smsRequest) {
        return smsTemplate.send(smsRequest);
    }

    /**
     * 使用华为云发送短信
     *
     * @param smsRequest 短信参数
     * @return 发送结果
     */
    @SMS(templateName = "验证码短信模板", supplier = SmsSupplier.HUAWEI)
    @PostMapping("/sendWithHuaweiCloud")
    public AbstractSmsResponse sendWithHuaweiCloud(@RequestBody BaseSmsRequest smsRequest) {
        return smsTemplate.send(smsRequest);
    }

    /**
     * 使用京东云发送短信
     *
     * @param smsRequest 短信参数
     * @return 发送结果
     */
    @SMS(templateName = "京东云短信验证码模板", supplier = SmsSupplier.JINGDONG)
    @PostMapping("/sendWithJingdongCloud")
    public AbstractSmsResponse sendWithJingdongCloud(@RequestBody BaseSmsRequest smsRequest) {
        return smsTemplate.send(smsRequest);
    }

    /**
     * 使用七牛云发送短信
     *
     * @param smsRequest 短信参数
     * @return 发送结果
     */
    @SMS(templateName = "验证码短信模板", supplier = SmsSupplier.QINIU)
    @PostMapping("/sendWithQiniuCloud")
    public AbstractSmsResponse sendWithQiniuCloud(@RequestBody BaseSmsRequest smsRequest) {
        return smsTemplate.send(smsRequest);
    }

    /**
     * 使用腾讯云发送短信
     *
     * @param smsRequest 短信参数
     * @return 发送结果
     */
    @SMS(templateName = "腾讯短信模板1", supplier = SmsSupplier.TENCENT)
    @PostMapping("/sendWithTencentCloud")
    public AbstractSmsResponse sendWithTencentCloud(@RequestBody BaseSmsRequest smsRequest) {
        return smsTemplate.send(smsRequest);
    }

    /**
     * 自定义短信实现发送短信
     *
     * @param smsRequest 短信参数
     * @return 发送结果
     */
    @SMS(invokeClass = MyCustomSmsClientDemoImpl.class)
    @PostMapping("/sendCustomSmsClient")
    public AbstractSmsResponse sendWithCustomSmsClient(@RequestBody BaseSmsRequest smsRequest) {
        return smsTemplate.send(smsRequest);
    }


    /**
     * 自定义短信实现发送短信
     *
     * @param smsRequest 短信参数
     * @return 发送结果
     */
    @SMS(invokeClass = MyCustomSmsClientDemoImpl.class)
    @PostMapping("/sendWithCustomSmsClientNested")
    public AbstractSmsResponse sendWithCustomSmsClientNested(@RequestBody BaseSmsRequest smsRequest) {
        SmsDemoController currentProxy = (SmsDemoController) AopContext.currentProxy();
        AbstractSmsResponse send1 = smsTemplate.send(smsRequest);
        AbstractSmsResponse send2 = currentProxy.sendWithHuaweiCloud(smsRequest);
        log.info("{}", send1);
        return send1;
    }

}
