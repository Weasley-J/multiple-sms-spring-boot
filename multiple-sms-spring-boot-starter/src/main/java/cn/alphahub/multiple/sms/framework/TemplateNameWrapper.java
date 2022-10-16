package cn.alphahub.multiple.sms.framework;

import cn.alphahub.multiple.sms.enums.SmsI18n;
import cn.alphahub.multiple.sms.enums.SmsSupplier;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.annotation.Nullable;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Template Name Wrapper
 *
 * @author weasley
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class TemplateNameWrapper implements Serializable {
    /**
     * Sms I18n Type
     */
    @Nullable
    private SmsI18n smsI18n;
    /**
     * 配置文件里指定的短信模板名称
     */
    @NotEmpty
    private String templateName;
    /**
     * 短信供应商
     */
    @NotNull
    private SmsSupplier smsSupplier;
}
