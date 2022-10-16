package cn.alphahub.multiple.sms;

import cn.alphahub.multiple.sms.annotation.EnableMultipleSms;
import cn.alphahub.multiple.sms.aspect.SmsAspect;
import cn.alphahub.multiple.sms.domain.AbstractSmsResponse;
import cn.alphahub.multiple.sms.domain.BaseSmsRequest;
import cn.alphahub.multiple.sms.framework.SmsClient;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.validation.Valid;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 短信模板方法
 * <p> 使用示例
 * <pre>
 * import cn.alphahub.multiple.sms.SmsTemplate;
 * import cn.alphahub.multiple.sms.annotation.SMS;
 * import cn.alphahub.multiple.sms.domain.BaseSmsRequest;
 * import cn.alphahub.multiple.sms.enums.SmsSupplier;
 * import cn.alphahub.multiple.sms.test.demo.MyCustomSmsClientDemoImpl;
 * import lombok.extern.slf4j.Slf4j;
 * import org.springframework.aop.framework.AopContext;
 * import org.springframework.beans.factory.annotation.Autowired;
 * import org.springframework.web.bind.annotation.PostMapping;
 * import org.springframework.web.bind.annotation.RequestBody;
 * import org.springframework.web.bind.annotation.RequestMapping;
 * import org.springframework.web.bind.annotation.RestController;
 *
 * //多短信供应商、多短信模板示例Controller
 * {@code @Slf4j}
 * {@code @RestController}
 * {@code @RequestMapping("/sms/support/demo")}
 * public class SmsDemoController {
 *
 *     {@code @Autowired}
 *     private SmsTemplate smsTemplate;
 *
 *     //使用默认短信模板发送短信,默认模板可以注解{@code @SMS},也可以不加
 *     {@code @PostMapping("/sendWithDefaultTemplate")}
 *     public AbstractSmsResponse sendWithDefaultTemplate({@code @RequestBody} BaseSmsRequest smsRequest) {
 *         return smsTemplate.send(smsRequest);
 *     }
 *
 *     //使用阿里云短信模板1发送短信
 *     {@code @SMS(templateName = "促销短信模板")}
 *     {@code @PostMapping("/sendWithAliCloud1")}
 *     public AbstractSmsResponse sendWithAliCloud1({@code @RequestBody} BaseSmsRequest smsRequest) {
 *         return smsTemplate.send(smsRequest);
 *     }
 *
 *     //使用阿里云短信模板2发送短信
 *     {@code @SMS(templateName = "秒杀短信模板")}
 *     {@code @PostMapping("/sendWithAliCloud2")}
 *     public AbstractSmsResponse sendWithAliCloud2({@code @RequestBody} BaseSmsRequest smsRequest) {
 *         return smsTemplate.send(smsRequest);
 *     }
 *
 *     //使用华为云发送短信
 *     {@code @SMS(templateName = "验证码短信模板", supplier = SmsSupplier.HUAWEI)}
 *     {@code @PostMapping("/sendWithHuaweiCloud")}
 *     public AbstractSmsResponse sendWithHuaweiCloud({@code @RequestBody} BaseSmsRequest smsRequest) {
 *         return smsTemplate.send(smsRequest);
 *     }
 *
 *     //使用京东云发送短信
 *     {@code @SMS(templateName = "京东云短信验证码模板", supplier = SmsSupplier.JINGDONG)}
 *     {@code @PostMapping("/sendWithJingdongCloud")}
 *     public AbstractSmsResponse sendWithJingdongCloud({@code @RequestBody} BaseSmsRequest smsRequest) {
 *         return smsTemplate.send(smsRequest);
 *     }
 *
 *     //使用七牛云发送短信
 *     {@code @SMS(templateName = "验证码短信模板", supplier = SmsSupplier.QINIU)}
 *     {@code PostMapping("/sendWithQiniuCloud")}
 *     public AbstractSmsResponse sendWithQiniuCloud({@code @RequestBody} BaseSmsRequest smsRequest) {
 *         return smsTemplate.send(smsRequest);
 *     }
 *
 *     //使用腾讯云发送短信
 *     {@code @SMS(templateName = "腾讯云内容短信模板", supplier = SmsSupplier.TENCENT)}
 *     {@code @PostMapping("/sendWithTencentCloud")}
 *     public AbstractSmsResponse sendWithTencentCloud({@code @RequestBody} BaseSmsRequest smsRequest) {
 *         return smsTemplate.send(smsRequest);
 *     }
 *
 *     //自定义短信实现发送短信
 *     {@code @SMS(invokeClass = MyCustomSmsClientDemoImpl.class)}
 *     {@code @PostMapping("/sendCustomSmsClient")}
 *     public AbstractSmsResponse sendWithCustomSmsClient({@code @RequestBody} BaseSmsRequest smsRequest) {
 *         return smsTemplate.send(smsRequest);
 *     }
 *
 *     //自定义短信实现发送短信
 *     {@code @SMS(invokeClass = MyCustomSmsClientDemoImpl.class)}
 *     {@code @PostMapping("/sendWithCustomSmsClientNested")}
 *     public AbstractSmsResponse sendWithCustomSmsClientNested({@code @RequestBody} BaseSmsRequest smsRequest) {
 *         SmsDemoController1 currentProxy = (SmsDemoController1) AopContext.currentProxy();
 *         Object send = smsTemplate.send(smsRequest);
 *         Object send0 = currentProxy.sendWithHuaweiCloud(smsRequest);
 *         log.info("{}", send);
 *         return send;
 *     }
 *
 * }
 * </pre>
 *
 * @author lwj
 * @version 1.0
 */
@Slf4j
@Component
@Validated
@ConditionalOnBean(annotation = {EnableMultipleSms.class})
public class SmsTemplate {
    /**
     * 默认线程池
     */
    private final SmsAspect smsAspect;
    private final ThreadPoolExecutor smsThreadPoolExecutor;

    public SmsTemplate(SmsAspect smsAspect, ThreadPoolExecutor smsThreadPoolExecutor) {
        this.smsAspect = smsAspect;
        this.smsThreadPoolExecutor = smsThreadPoolExecutor;
    }

    /**
     * 发送短信
     *
     * @param request 短信参数
     * @return 短信供应商的发送短信后的返回结果
     */
    public AbstractSmsResponse send(@Valid BaseSmsRequest request) {
        SmsClient smsClient = smsAspect.getSmsClient();
        AtomicReference<AbstractSmsResponse> sendResult = new AtomicReference<>();
        RequestAttributes mainThreadRequestAttributes = RequestContextHolder.getRequestAttributes();
        CompletableFuture<AbstractSmsResponse> sendResponseFuture = CompletableFuture.supplyAsync(() -> {
            RequestContextHolder.setRequestAttributes(mainThreadRequestAttributes);
            return smsClient.send(request);
        }, smsThreadPoolExecutor).whenComplete((result, throwable) -> {
            if (Objects.nonNull(result)) {
                sendResult.set(result);
            }
        });
        try {
            CompletableFuture.allOf(sendResponseFuture).get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("sms param:{},{}", JSONUtil.toJsonStr(request), e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
        return sendResult.get();
    }
}
