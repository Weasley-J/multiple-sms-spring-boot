package cn.alphahub.multiple.sms.framework;

/**
 * template name decorator
 *
 * @author weasley
 * @version 1.0.0
 */
public final class TemplateNameDecorator {
    /**
     * 获取装饰过的模版名称
     *
     * @param wrapper Template Name Wrapper
     * @return 模版名称
     */
    public static String decorateTemplateName(TemplateNameWrapper wrapper) {
        String decorateTemplateName = wrapper.getSmsSupplier().getCode() + ":" + wrapper.getTemplateName();
        if (wrapper.getSmsI18n() != null) {
            decorateTemplateName = decorateTemplateName + ":" + wrapper.getSmsI18n();
        }
        return decorateTemplateName;
    }
}
