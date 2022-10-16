package cn.alphahub.multiple.sms.config;

import cn.alphahub.multiple.sms.annotation.EnableMultipleSms;
import cn.alphahub.multiple.sms.annotation.SMS;
import cn.alphahub.multiple.sms.config.entity.AbstractSmsProperties;
import cn.alphahub.multiple.sms.config.entity.AliSmsProperties;
import cn.alphahub.multiple.sms.config.entity.HuaweiSmsProperties;
import cn.alphahub.multiple.sms.config.entity.JingdongSmsProperties;
import cn.alphahub.multiple.sms.config.entity.MengwangSmsProperties;
import cn.alphahub.multiple.sms.config.entity.QiniuSmsProperties;
import cn.alphahub.multiple.sms.config.entity.TencentSmsProperties;
import cn.alphahub.multiple.sms.domain.SmsWrapper;
import cn.alphahub.multiple.sms.enums.SmsI18n;
import cn.alphahub.multiple.sms.enums.SmsSupplier;
import cn.alphahub.multiple.sms.exception.SmsException;
import cn.alphahub.multiple.sms.framework.SmsClient;
import cn.alphahub.multiple.sms.framework.TemplateNameDecorator;
import cn.alphahub.multiple.sms.framework.TemplateNameWrapper;
import cn.alphahub.multiple.sms.framework.impl.DefaultAliCloudSmsClientImpl;
import cn.alphahub.multiple.sms.framework.impl.DefaultHuaweiCloudSmsClientImpl;
import cn.alphahub.multiple.sms.framework.impl.DefaultJingdongCloudSmsClientImpl;
import cn.alphahub.multiple.sms.framework.impl.DefaultQiniuCloudSmsClientImpl;
import cn.alphahub.multiple.sms.framework.impl.DefaultTencentCloudSmsClientImpl;
import cn.alphahub.multiple.sms.framework.impl.mengwang.MengwangChineseSmsClientAdapter;
import cn.alphahub.multiple.sms.framework.impl.mengwang.MengwangEnglishSmsClientAdapter;
import cn.hutool.core.collection.CollUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 多模板短信配置
 *
 * @author lwj
 * @version 1.0
 * @date 2021-09-24
 */
@Slf4j
@Validated
@Configuration(proxyBeanMethods = false)
@ConditionalOnBean(annotation = {EnableMultipleSms.class})
@ConfigurationPropertiesScan({"cn.alphahub.multiple.sms.config"})
@EnableConfigurationProperties({
        SmsThreadPoolProperties.class, AliSmsProperties.class, AliSmsProperties.class,
        HuaweiSmsProperties.class, JingdongSmsProperties.class, MengwangSmsProperties.class,
        QiniuSmsProperties.class, TencentSmsProperties.class, SmsMetadataProperties.class,
})
public class SmsConfiguration {

    private final ApplicationContext applicationContext;

    public SmsConfiguration(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * 装饰的短信模板名称
     *
     * @param smsSupplier  短信供应商
     * @param templateName 配置文件里指定的短信模板名称
     * @return 短信模板名称
     */
    public static String decorateTemplateName(@NotNull SmsSupplier smsSupplier, @NotEmpty String templateName, @Nullable SmsI18n smsI18n) {
        String name = smsSupplier.getCode() + ":" + templateName;
        if (smsI18n != null) {
            name = name + ":" + smsI18n.name();
        }
        return name;
    }

    /**
     * Process sms templates map
     */
    protected static void processSmsTemplatesMap(Map<String, AbstractSmsProperties> smsPropertiesMap,
                                                 Map<SmsSupplier, List<? extends AbstractSmsProperties>> smsTemplatesMap) {
        smsTemplatesMap.forEach((smsSupplier, abstractSmsProperties) -> abstractSmsProperties.forEach(smsProperty -> {
            TemplateNameWrapper wrapper = new TemplateNameWrapper();
            wrapper.setTemplateName(smsProperty.getTemplateName());
            wrapper.setSmsSupplier(smsSupplier);
            String decorateTemplateName = TemplateNameDecorator.decorateTemplateName(wrapper);
            smsPropertiesMap.put(decorateTemplateName, smsProperty);
            if (log.isInfoEnabled())
                log.info("Loaded multiple sms template: {}", decorateTemplateName);
        }));
    }

    /**
     * 短信配置元数据映射
     *
     * @param metadataProperties 短信元数据配置属性
     * @return smsTemplatesMap
     */
    @Bean(name = {"smsTemplatesMap"})
    public Map<SmsSupplier, List<? extends AbstractSmsProperties>> smsTemplatesMap(@Valid SmsMetadataProperties metadataProperties) {
        MultipleSmsTemplatesProperties templates = metadataProperties.getMultipleTemplates();
        Map<SmsSupplier, List<? extends AbstractSmsProperties>> templatesMapping = new ConcurrentHashMap<>();
        if (null != templates) {
            if (!CollectionUtils.isEmpty(templates.getAli())) {
                templatesMapping.put(SmsSupplier.ALI, templates.getAli());
            }
            if (!CollectionUtils.isEmpty(templates.getHuawei())) {
                templatesMapping.put(SmsSupplier.HUAWEI, templates.getHuawei());
            }
            if (!CollectionUtils.isEmpty(templates.getJingdong())) {
                templatesMapping.put(SmsSupplier.JINGDONG, templates.getJingdong());
            }
            if (!CollectionUtils.isEmpty(templates.getMengwang())) {
                templatesMapping.put(SmsSupplier.MENGWANG, templates.getMengwang());
            }
            if (!CollectionUtils.isEmpty(templates.getQiniu())) {
                templatesMapping.put(SmsSupplier.QINIU, templates.getQiniu());
            }
            if (!CollectionUtils.isEmpty(templates.getTencent())) {
                templatesMapping.put(SmsSupplier.TENCENT, templates.getTencent());
            }
        }
        return templatesMapping;
    }

    /**
     * 短信模板配置map集合
     *
     * @param defaultTemplate 默认短信配置元数据
     * @param smsTemplatesMap 短信配置元数据映射
     * @return 短信模板配置集合
     */
    @Bean({"smsPropertiesMap"})
    public Map<String, AbstractSmsProperties> smsPropertiesMap(@Valid DefaultSmsTemplateProperties defaultTemplate,
                                                               @Qualifier("smsTemplatesMap") Map<SmsSupplier, List<? extends AbstractSmsProperties>> smsTemplatesMap

    ) {
        Map<String, AbstractSmsProperties> smsPropertiesMap = new ConcurrentHashMap<>();
        if (null != defaultTemplate) {
            String defaultTemplateName = defaultTemplate.getTemplateName();
            if (StringUtils.isBlank(defaultTemplateName)) defaultTemplate.setTemplateName(SMS.DEFAULT_TEMPLATE);
            TemplateNameWrapper wrapper = new TemplateNameWrapper();
            wrapper.setTemplateName(defaultTemplate.getTemplateName());
            wrapper.setSmsSupplier(defaultTemplate.getSmsSupplier());
            String decorateTemplateName = TemplateNameDecorator.decorateTemplateName(wrapper);
            List<? extends AbstractSmsProperties> smsProperties = smsTemplatesMap.get(defaultTemplate.getSmsSupplier());
            for (AbstractSmsProperties smsProperty : smsProperties) {
                if (defaultTemplateName.equals(smsProperty.getTemplateName())) {
                    smsPropertiesMap.put(decorateTemplateName, smsProperty);
                    break;
                }
            }
            if (smsPropertiesMap.get(decorateTemplateName) == null)
                throw new SmsException("默认短信模板配置不正确：" + decorateTemplateName);
            if (log.isInfoEnabled())
                log.info("Loaded default sms template: {}", decorateTemplateName);
        }
        if (CollUtil.isNotEmpty(smsTemplatesMap)) {
            processSmsTemplatesMap(smsPropertiesMap, smsTemplatesMap);
        }
        if (log.isDebugEnabled()) {
            log.debug("SMS templates has loaded: {}", smsPropertiesMap);
        }
        return smsPropertiesMap;
    }

    /**
     * 多模板、多供应商短信发送实例对象map集合
     *
     * @param smsPropertiesMap 短信模板配置集合
     * @return 多模板、多供应商短信发送实例对象集合
     */
    @Bean({"smsClientMap"})
    @DependsOn({"smsPropertiesMap"})
    public Map<String, SmsClient> smsClientMap(@Qualifier("smsPropertiesMap") Map<String, AbstractSmsProperties> smsPropertiesMap) {
        Map<String, SmsClient> smsClientMap = new ConcurrentHashMap<>();
        smsPropertiesMap.forEach((templateName, smsProperties) -> {
            switch (smsProperties.getSmsSupplier()) {
                case ALI:
                    smsClientMap.put(templateName, new DefaultAliCloudSmsClientImpl((AliSmsProperties) smsProperties));
                    break;
                case HUAWEI:
                    smsClientMap.put(templateName, new DefaultHuaweiCloudSmsClientImpl((HuaweiSmsProperties) smsProperties));
                    break;
                case JINGDONG:
                    smsClientMap.put(templateName, new DefaultJingdongCloudSmsClientImpl((JingdongSmsProperties) smsProperties));
                    break;
                case MENGWANG:
                    assert smsProperties instanceof MengwangSmsProperties;
                    MengwangSmsProperties mwSmsProperties = (MengwangSmsProperties) smsProperties;
                    if (mwSmsProperties.getSmsI18nMapping().get(SmsI18n.CHINESE) != null) {
                        smsClientMap.put(templateName + ":" + SmsI18n.CHINESE, new MengwangChineseSmsClientAdapter((MengwangSmsProperties) smsProperties));
                    }
                    if (mwSmsProperties.getSmsI18nMapping().get(SmsI18n.ENGLISH) != null) {
                        smsClientMap.put(templateName + ":" + SmsI18n.ENGLISH, new MengwangEnglishSmsClientAdapter(mwSmsProperties));
                    }
                    break;
                case QINIU:
                    smsClientMap.put(templateName, new DefaultQiniuCloudSmsClientImpl((QiniuSmsProperties) smsProperties));
                    break;
                case TENCENT:
                    smsClientMap.put(templateName, new DefaultTencentCloudSmsClientImpl((TencentSmsProperties) smsProperties));
                    break;
                default:
                    break;
            }
        });
        return smsClientMap;
    }

    /**
     * sms wrapper
     *
     * @param smsPropertiesMap 默认短信模板配置元数据Map
     * @param smsClientMap     多模板、多供应商短信发送实例对象map集合
     * @return SmsWrapper
     */
    @Bean
    @DependsOn({"smsPropertiesMap", "smsClientMap"})
    public SmsWrapper smsWrapper(@Qualifier("smsClientMap") Map<String, SmsClient> smsClientMap,
                                 @Qualifier("smsPropertiesMap") Map<String, AbstractSmsProperties> smsPropertiesMap) {
        return new SmsWrapper(smsClientMap, smsPropertiesMap);
    }

    /**
     * 线程池
     *
     * @param smsThreadPoolProperties 线程池配置参数
     * @return thread pool executor
     */
    @Bean(name = {"multipleSmsThreadPoolExecutor"})
    @ConditionalOnMissingBean(value = {ThreadPoolExecutor.class, Executor.class})
    public ThreadPoolExecutor multipleSmsThreadPoolExecutor(SmsThreadPoolProperties smsThreadPoolProperties) {
        return new ThreadPoolExecutor(
                smsThreadPoolProperties.getCorePoolSize(),
                smsThreadPoolProperties.getMaximumPoolSize(),
                smsThreadPoolProperties.getKeepAliveTime(),
                smsThreadPoolProperties.getTimeUnit(),
                new LinkedBlockingQueue<>(smsThreadPoolProperties.getCapacity()),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());
    }

}
