package cn.alphahub.multiple.sms.domain;

import cn.alphahub.multiple.sms.config.entity.AbstractSmsProperties;
import cn.alphahub.multiple.sms.enums.SmsSupplier;
import cn.alphahub.multiple.sms.exception.SmsException;
import cn.alphahub.multiple.sms.framework.SmsClient;
import cn.alphahub.multiple.sms.framework.TemplateNameDecorator;
import cn.alphahub.multiple.sms.framework.TemplateNameWrapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * Sms Wrapper
 *
 * @author weasley
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SmsWrapper {
    /**
     * 多模板、多供应商短信发送实例对象map集合
     */
    private Map<String, SmsClient> smsClientMap;
    /**
     * 默认短信模板配置元数据
     */
    private Map<String, AbstractSmsProperties> templateProperties;

    /**
     * 多模板、多供应商短信发送上层实现
     *
     * @param smsSupplier  短信供应商
     * @param templateName 配置文件里指定的短信模板名称
     * @return SmsClient
     */
    public SmsClient getSmsClient(@NotNull SmsSupplier smsSupplier, @NotEmpty String templateName) {
        TemplateNameWrapper wrapper = new TemplateNameWrapper();
        wrapper.setSmsI18n(null);
        wrapper.setTemplateName(templateName);
        wrapper.setSmsSupplier(smsSupplier);
        String decorateTemplateName = TemplateNameDecorator.decorateTemplateName(wrapper);
        if (smsClientMap.containsKey(decorateTemplateName)) {
            return smsClientMap.get(decorateTemplateName);
        }
        throw new SmsException("模板名称不匹配：" + decorateTemplateName);
    }
}
