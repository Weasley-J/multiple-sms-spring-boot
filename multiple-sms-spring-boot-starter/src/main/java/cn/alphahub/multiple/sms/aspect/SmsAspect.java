package cn.alphahub.multiple.sms.aspect;

import cn.alphahub.multiple.sms.annotation.EnableMultipleSms;
import cn.alphahub.multiple.sms.annotation.SMS;
import cn.alphahub.multiple.sms.config.DefaultSmsTemplateProperties;
import cn.alphahub.multiple.sms.config.SmsProperties;
import cn.alphahub.multiple.sms.domain.SmsWrapper;
import cn.alphahub.multiple.sms.enums.SmsSupplier;
import cn.alphahub.multiple.sms.framework.SmsClient;
import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;

import static cn.alphahub.multiple.sms.framework.SmsClient.DefaultSmsClientPlaceholder;

/**
 * 多模板短信配置切面
 * <p>
 * 关于注解{@code @SMS}作用在类和方法的优先级问题
 * <ul>
 *     <li>1. 当注解{@code @SMS}作用类上时，该类所有短信模板方法发送短信的客户端都以注解{@code @SMS}指定为准客户端</li>
 *     <li>2. 当注解{@code @SMS}作用方法上时，该方法短信客户端的为注解{@code @SMS}指定的短信客户端</li>
 *     <li>3. 当注解{@code @SMS}同时作用类，和方法上时，方法上注解{@code @SMS}的优先级高于类上{@code @SMS}注解的优先级</li>
 * </ul>
 *
 * @author lwj
 * @version 1.0
 * @apiNote <a href="https://blog.csdn.net/genius_wolf/article/details/109358153">{@code @Aspect}注解在类上不生效的帮助链接</a>
 */
@Slf4j
@Aspect
@Component
@ConditionalOnBean(annotation = {EnableMultipleSms.class})
public class SmsAspect {
    /**
     * sms client thread local
     */
    private static final ThreadLocal<SmsClient> SMS_CLIENT_THREAD_LOCAL = new ThreadLocal<>();

    private final SmsWrapper smsWrapper;
    private final SmsProperties smsProperties;

    public SmsAspect(SmsWrapper smsWrapper, SmsProperties smsProperties) {
        this.smsWrapper = smsWrapper;
        this.smsProperties = smsProperties;
    }

    /////////////////////////////////////////////////////////////
    //                 注解@SMS作用在类上是AOP切入点
    /////////////////////////////////////////////////////////////

    /**
     * 获取短信客户端实例
     *
     * @return instance of SmsClient
     * @see SmsClient
     */
    public SmsClient getSmsClient() {
        SmsClient smsClient = SMS_CLIENT_THREAD_LOCAL.get();
        if (null == smsClient) {
            DefaultSmsTemplateProperties defaultTemplate = smsProperties.getDefaultTemplate();
            SmsSupplier smsSupplier = defaultTemplate.getSmsSupplier();
            String templateName = defaultTemplate.getTemplateName();
            smsClient = smsWrapper.getSmsClient(smsSupplier, templateName);
        }
        return smsClient;
    }

    /**
     * 定义切入点方法
     */
    @Pointcut(value = "@within(cn.alphahub.multiple.sms.annotation.SMS)")
    public void pointcutOnProxyClass() {
        // a void method for proxy class aspect pointcut.
    }

    /**
     * 目标方法执行之前执行
     *
     * @param point 提供对连接点可用状态和有关它的静态信息的反射访问
     * @param sms   多模板短信注解
     * @throws Exception 异常
     */
    @Before("pointcutOnProxyClass() && @within(sms)")
    public void beforeOnProxyClass(JoinPoint point, SMS sms) throws Exception {
        SmsClient smsClient = getSmsClient(sms);
        if (null != sms.invokeClass() && sms.invokeClass() != DefaultSmsClientPlaceholder.class) {
            SmsClient instance = sms.invokeClass().getDeclaredConstructor().newInstance();
            SMS_CLIENT_THREAD_LOCAL.set(instance);
        } else SMS_CLIENT_THREAD_LOCAL.set(smsClient);
    }

    /**
     * 目标方法执行之后必定执行(无论是否报错)
     * <p>
     * 目标方法同时需要{@code @SMS}注解的修饰，并且这里（通知）的形参名要与上面注解中的一致
     *
     * @param sms 多模板短信注解
     */
    @After("pointcutOnProxyClass() && @within(sms)")
    public void afterOnProxyClass(SMS sms) {
        if (SMS_CLIENT_THREAD_LOCAL.get() != null) SMS_CLIENT_THREAD_LOCAL.remove();
    }

    /////////////////////////////////////////////////////////////
    //              注解@SMS作用在方法上时AOP切入点
    /////////////////////////////////////////////////////////////

    /**
     * 目标方法抛出异常后执行
     * <p>
     * 目标方法同时需要{@code @SMS}注解的修饰，并且这里（通知）的形参名要与上面注解中的一致,可以声明来获取目标方法抛出的异常
     *
     * @param sms       多模板短信注解
     * @param throwable 异常
     */
    @AfterThrowing(pointcut = "pointcutOnProxyClass() && @within(sms)", throwing = "throwable")
    public void afterThrowingOnProxyClass(SMS sms, Throwable throwable) {
        log.error("{}", throwable.getLocalizedMessage());
    }

    /**
     * 定义切入点方法
     */
    @Pointcut(value = "@annotation(cn.alphahub.multiple.sms.annotation.SMS)")
    public void pointcutOnProxyMethod() {
        // a void method for proxy method aspect pointcut.
    }

    /**
     * 目标方法执行之前执行
     *
     * @param point 切入点
     * @param sms   多模板短信注解
     * @throws Exception 异常
     */
    @Before("pointcutOnProxyMethod() && @annotation(sms)")
    public void beforeOnProxyMethod(JoinPoint point, SMS sms) throws Exception {
        log.info("before.");
        SmsClient smsClient = getSmsClient(sms);
        // 自定义实现发送发送短信的实现类不为空时，将优先使用自定义短信实现
        if (Objects.nonNull(sms.invokeClass()) && sms.invokeClass() != DefaultSmsClientPlaceholder.class) {
            SmsClient instance = sms.invokeClass().getDeclaredConstructor().newInstance();
            SMS_CLIENT_THREAD_LOCAL.set(instance);
        } else SMS_CLIENT_THREAD_LOCAL.set(smsClient);
    }

    /**
     * 环绕通知
     *
     * @param point 切入点
     * @return proceed object
     * @throws Throwable throwable
     */
    @Around("pointcutOnProxyMethod()")
    public Object aroundOnProxyMethod(ProceedingJoinPoint point) throws Throwable {
        log.info("around");
        long beginTime = System.currentTimeMillis();
        Object proceed = point.proceed();
        long endTime = System.currentTimeMillis() - beginTime;
        log.warn("around耗时：{}（ms），开始时间：{}，结束时间：{}", endTime, DateUtil.formatDateTime(new Date(beginTime)), DateUtil.formatDateTime(new Date(endTime)));
        return proceed;
    }

    /**
     * 目标方法执行之后必定执行(无论是否报错)
     * <p>
     * 目标方法同时需要{@code @SMS}注解的修饰，并且这里（通知）的形参名要与上面注解中的一致
     *
     * @param sms 多模板短信注解
     */
    @After("pointcutOnProxyMethod() && @annotation(sms)")
    public void afterOnProxyMethod(SMS sms) {
        log.info("after: {}", sms);
        if (SMS_CLIENT_THREAD_LOCAL.get() != null) SMS_CLIENT_THREAD_LOCAL.remove();
    }

    /**
     * 目标方法有返回值且正常返回后执行
     * <p>
     * 这里切入点方法的形参名{@code pointcut()}要与上面注解中的一致
     *
     * @param point        join point
     * @param responseData response data
     */
    @AfterReturning(pointcut = "pointcutOnProxyMethod()", returning = "responseData")
    public void afterReturningOnProxyMethod(JoinPoint point, Object responseData) {
        log.info("afterReturning, response data: {}", Objects.isNull(responseData) ? "response data is null." : responseData.toString());
        /*Object[] args = point.getArgs();
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = Objects.requireNonNull(attributes).getRequest();*/
    }

    /////////////////////////////////////////////////////////////
    //                  END AOP CONSTRUCTION
    /////////////////////////////////////////////////////////////

    /**
     * 目标方法抛出异常后执行
     * <p>
     * 目标方法同时需要{@code @SMS}注解的修饰，并且这里（通知）的形参名要与上面注解中的一致,可以声明来获取目标方法抛出的异常
     *
     * @param sms       多模板短信注解
     * @param throwable throwable
     */
    @AfterThrowing(pointcut = "pointcutOnProxyMethod() && @annotation(sms)", throwing = "throwable")
    public void afterThrowingOnProxyMethod(SMS sms, Throwable throwable) {
        log.error("{}", throwable.getLocalizedMessage());
    }

    /**
     * 获取短信客户端实例
     *
     * @param sms 多模板短信注解
     * @return SmsClient
     */
    public SmsClient getSmsClient(SMS sms) {
        return smsWrapper.getSmsClient(sms.supplier(), sms.templateName());
    }
}
