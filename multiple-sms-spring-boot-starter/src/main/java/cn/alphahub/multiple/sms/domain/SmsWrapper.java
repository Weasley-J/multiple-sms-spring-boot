package cn.alphahub.multiple.sms.domain;

import cn.alphahub.multiple.sms.framework.SmsClient;
import cn.alphahub.multiple.sms.config.SmsConfiguration;
import cn.alphahub.multiple.sms.enums.SmsSupplier;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Map;

import static cn.alphahub.multiple.sms.config.SmsConfiguration.decorateTemplateName;

/**
 * 短信包装类
 *
 * @author weasley
 * @version 1.0
 * @date 2022/7/15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SmsWrapper {
    /**
     * 默认短信模板配置元数据
     */
    private Map<String, SmsConfiguration.SmsTemplateProperties> templateProperties;
    /**
     * 多模板、多供应商短信发送实例对象map集合
     */
    private Map<String, SmsClient> smsClientMap;

    /**
     * 短信模板配置元数据
     *
     * @param smsSupplier  短信供应商
     * @param templateName 配置文件里指定的短信模板名称
     * @return SmsTemplateProperties
     */
    public SmsConfiguration.SmsTemplateProperties getTemplateProperties(SmsSupplier smsSupplier, String templateName) {
        String decorateTemplateName = decorateTemplateName(smsSupplier, templateName);
        return templateProperties.get(decorateTemplateName);
    }

    /**
     * 多模板、多供应商短信发送上层实现
     *
     * @param smsSupplier  短信供应商
     * @param templateName 配置文件里指定的短信模板名称
     * @return SmsClient
     */
    public SmsClient getSmsClient(@NotNull SmsSupplier smsSupplier, @NotEmpty String templateName) {
        String decorateTemplateName = decorateTemplateName(smsSupplier, templateName);
        return smsClientMap.get(decorateTemplateName);
    }
}
